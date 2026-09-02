package net.inspirehub.hr.settings.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.AudioAttributes
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import net.inspirehub.hr.MainActivity
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import androidx.core.net.toUri

class LocalAttendanceReminderReceiver : BroadcastReceiver() {

    /**
     * Woken up by AlarmManager. This runs even if the app was closed and its process
     * was gone - Android recreates the process just to deliver this.
     *
     * Order of business, and the order matters:
     *   1. schedule the NEXT alarm immediately, so the chain can never break, even
     *      if everything below fails,
     *   2. hold the CPU awake,
     *   3. hand the reading to the foreground service,
     *   4. if Android refuses the service start, take the reading right here.
     */
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {
        val appContext = context.applicationContext

        val sharedPrefManager = SharedPrefManager(appContext)

//        Check if at least one reminder is enabled
        val checkInEnabled = sharedPrefManager.isCheckInReminderEnabled()
        val checkOutEnabled = sharedPrefManager.isCheckOutReminderEnabled()

        if (!checkInEnabled && !checkOutEnabled) {
            Log.d(TAG, "No attendance reminders enabled - no new alarm scheduled")

            return
        }

        /*
         * The next alarm is booked HERE, before anything that can fail, so the
         * chain can never break. Everything below only ever moves it: the fix
         * reschedules with a distance-based delay when it finishes. If every one
         * of those paths fails we still wake up again in MIN_INTERVAL_MINUTES
         * instead of stopping for good.
         */
        LocalAttendanceReminderManager.scheduleNextAlarm(
            context = appContext,
            delayMinutes = LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES
        )

        // Check location permission
        if (
            ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Log.e(TAG, "Alarm Location permission not granted")

            return
        }

        Log.d(
            TAG,
            "Alarm fired -> requesting location from foreground service"
        )

        /*
         * Held for the gap between this broadcast and the reading actually
         * starting. Whoever finishes the reading releases it, and the safety
         * timeout inside AttendanceReminderWakelock covers the case where
         * nothing ever starts.
         */
        AttendanceReminderWakelock.acquire(appContext)

        try {

            appContext.startAttendanceLocationService(
                AttendanceLocationForegroundService.ACTION_GET_LOCATION
            )

        } catch (t: Throwable) {

            /*
             * ForegroundServiceStartNotAllowedException: we are a background
             * process, and from Android 12 on a background process may not start
             * a foreground service unless the app is exempt from battery
             * optimisation. Take the reading right here instead - a broadcast has
             * far less time to work with, but it beats skipping the round, and no
             * notification is shown at all on this path.
             */

            Log.e(
                TAG,
                "Foreground service refused -> taking the reading inside the alarm",
                t
            )

            val pendingResult = goAsync()

            AttendanceLocationForegroundService.isTakingFix = true

            AttendanceLocationFix.request(
                context = appContext
            ) { location ->

                AttendanceLocationForegroundService.isTakingFix = false

                handleFixResult(appContext, location)

                AttendanceLocationNotification.onFixDone(appContext)

                AttendanceReminderWakelock.release()

                runCatching { pendingResult.finish() }
            }
        }
    }

    private fun calculateNextIntervalMinutes(
        distanceMeters: Float
    ): Long {

        val distanceKm = distanceMeters / 1000f

        val minutes = distanceKm.toLong()

        return minutes.coerceAtLeast(LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES)
    }


    // MAIN REMINDER LOGIC
    private fun checkAttendanceReminder(
        context: Context,
        latitude: Double,
        longitude: Double
    ) {

        val sharedPrefManager = SharedPrefManager(context)
        val checkInEnabled = sharedPrefManager.isCheckInReminderEnabled()
        val checkOutEnabled = sharedPrefManager.isCheckOutReminderEnabled()
        val attendanceStatus = sharedPrefManager.getAttendanceStatus()
        val lastReminderType = sharedPrefManager.getLastAttendanceReminderType()

        Log.d(
            TAG,
            "Alarm Reminder check -> " +
                    "checkInEnabled=$checkInEnabled, " +
                    "checkOutEnabled=$checkOutEnabled, " +
                    "attendanceStatus=$attendanceStatus"
        )

        // 1️⃣ Get companies
        val companies = sharedPrefManager.getCompaniesLatLng()

        if (companies.isEmpty()) {
            Log.d(TAG, "Alarm No company locations found")

            LocalAttendanceReminderManager.scheduleNextAlarm(
                context = context,
                delayMinutes = LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES
            )
            return
        }

        // 2️⃣ Find nearest company
        var nearestDistance = Float.MAX_VALUE
        var nearestCompanyName = ""

        companies.forEach { company ->

            val distance = calculateDistance(
                latitude = latitude,
                longitude = longitude,
                companyLat = company.lat,
                companyLng = company.lng
            )

            Log.d(
                TAG,
                "Alarm Company=${company.name}, " +
                        "distance=$distance, " +
                        "allowed=${company.allowedDistance}"
            )

            if (distance < nearestDistance) {
                nearestDistance = distance
                nearestCompanyName = company.name
            }
        }

        // 3️⃣ Determine whether inside allowed area
        val nearestCompany = companies.minByOrNull { company ->

            calculateDistance(
                latitude = latitude,
                longitude = longitude,
                companyLat = company.lat,
                companyLng = company.lng
            )
        }

        val allowedDistance =  nearestCompany?.allowedDistance?.toFloat() ?: 50f
        val isInside = nearestDistance <= allowedDistance

        Log.d(TAG, "Alarm Nearest company=$nearestCompanyName")
        Log.d(TAG, "Alarm Nearest distance=$nearestDistance meters")
        Log.d(TAG, "Alarm Allowed distance=$allowedDistance meters")
        Log.d(TAG, "Alarm Is inside=$isInside")

        // 4️⃣ CHECK-IN LOGIC
        if (attendanceStatus == "checked_out") {
            // Check-in reminder is OFF
            if (!checkInEnabled) {
                Log.d(TAG, "Alarm Check-in reminder disabled -> nothing to do")
                cancelReminderNotification(context)

                LocalAttendanceReminderManager.scheduleNextAlarm(
                    context = context,
                    delayMinutes = LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES
                )

                return
            }

            // Employee is INSIDE
            if (isInside) {
                if (lastReminderType == CHECK_IN_REMINDER) {
                    Log.d(
                        TAG,
                        "Alarm Check-in reminder already shown -> skip notification"
                    )
                } else {
                    showReminderNotification(
                        context = context,
                        type = CHECK_IN_REMINDER,
                        title = context.getString(R.string.check_in_reminder),
                        message = context.getString(R.string.you_are_inside_work_location_without_check_in)
                    )
                    sharedPrefManager.saveLastAttendanceReminderType(CHECK_IN_REMINDER)
                }

            } else {
                // Employee is OUTSIDE
                Log.d(TAG, "Alarm Employee is outside -> remove Check-in notification")
                cancelReminderNotification(context)
                sharedPrefManager.saveLastAttendanceReminderType(null)
            }

            scheduleNextCheck(
                context = context,
                distanceMeters = nearestDistance
            )

            return
        }

        // 5️⃣ CHECK-OUT LOGIC
        if (attendanceStatus == "checked_in") {
            // Check-out reminder is OFF
            if (!checkOutEnabled) {
                Log.d(TAG, "Alarm Check-out reminder disabled -> nothing to do")
                cancelReminderNotification(context)
                LocalAttendanceReminderManager.scheduleNextAlarm(
                    context = context,
                    delayMinutes = LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES
                )

                return
            }

            // Employee is OUTSIDE
            if (!isInside) {
                if (lastReminderType == CHECK_OUT_REMINDER) {
                    Log.d(TAG, "Alarm Check-out reminder already shown -> skip notification")
                } else {
                    showReminderNotification(
                        context = context,
                        type = CHECK_OUT_REMINDER,
                        title = context.getString(R.string.check_out_reminder),
                        message = context.getString(R.string.you_left_work_location_without_check_out)
                    )
                    sharedPrefManager.saveLastAttendanceReminderType(CHECK_OUT_REMINDER)
                }

            } else {
                // Employee came back inside
                Log.d(TAG, "Alarm Employee is inside -> remove Check-out notification")
                cancelReminderNotification(context)
                sharedPrefManager.saveLastAttendanceReminderType(null)
            }

            scheduleNextCheck(
                context = context,
                distanceMeters = nearestDistance
            )

            return
        }

        // 6️⃣ Unknown status
        Log.d(TAG, "Alarm Unknown attendance status=$attendanceStatus")
        cancelReminderNotification(context)

        LocalAttendanceReminderManager.scheduleNextAlarm(
            context = context,
            delayMinutes = LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES
        )
    }

    private fun scheduleNextCheck(
        context: Context,
        distanceMeters: Float
    ) {

        val nextIntervalMinutes = calculateNextIntervalMinutes(distanceMeters)

        Log.d(TAG, "Alarm Next location check after $nextIntervalMinutes minutes")

        LocalAttendanceReminderManager.scheduleNextAlarm(
            context = context,
            delayMinutes = nextIntervalMinutes
        )
    }


    private fun showReminderNotification(
        context: Context,
        type: String,
        title: String,
        message: String
    ) {

        /*
         * Cheap and idempotent, and it has to happen here rather than only in
         * onReceive(): this also runs from the foreground service, which can be
         * started straight from the settings screen without any alarm having fired
         * first. Posting to a channel that does not exist is silently dropped.
         */
        createNotificationChannel(context)

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        val openAppIntent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val openAppPendingIntent =
            PendingIntent.getActivity(
                context,
                9005,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )


        val notification =
            NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.inspire_hub_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setOngoing(true)
                .setContentIntent(openAppPendingIntent)
                .build()

        notificationManager.notify(
            REMINDER_NOTIFICATION_ID,
            notification
        )

        Log.d(TAG, "Alarm Reminder notification shown: $type")
    }

    private fun createNotificationChannel(
        context: Context
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        ) {

            val soundUri = "android.resource://${context.packageName}/${R.raw.attendance_reminder}".toUri()

            val audioAttributes =
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

            val channel =
                NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "Attendance Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description =  "Attendance check-in and check-out reminders"
                    setSound(soundUri, audioAttributes)
                    enableVibration(true)
                    setShowBadge(true)
                }

            val manager = context.getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(channel)
        }
    }

    private fun calculateDistance(
        latitude: Double,
        longitude: Double,
        companyLat: Double,
        companyLng: Double
    ): Float {

        val results = FloatArray(1)

        Location.distanceBetween(
            latitude,
            longitude,
            companyLat,
            companyLng,
            results
        )

        return results[0]
    }

    companion object {

        private const val TAG = "LOCAL_ATTENDANCE_REMINDER"
        private const val REMINDER_NOTIFICATION_ID = 9002
        private const val REMINDER_CHANNEL_ID = "attendance_reminders_v2"

        private const val CHECK_IN_REMINDER = "CHECK_IN_REMINDER"
        private const val CHECK_OUT_REMINDER = "CHECK_OUT_REMINDER"

        /**
         * The single place where the outcome of one GPS reading is turned into a
         * decision, whoever took it: the foreground service on the normal path, or
         * the alarm receiver itself when Android refused the service.
         */
        fun handleFixResult(
            context: Context,
            location: Location?
        ) {

            val sharedPrefManager = SharedPrefManager(context)

            /*
             * A fix can land after the employee switched the reminders off - the GPS
             * was already searching when they flipped the switch. Acting on it would
             * show a reminder for a moment they asked not to be reminded about.
             */
            if (
                !sharedPrefManager.isCheckInReminderEnabled() &&
                !sharedPrefManager.isCheckOutReminderEnabled()
            ) {

                Log.d(TAG, "Alarm Fix arrived after reminders were disabled -> discarded")

                return
            }

            if (location == null) {

                Log.d(TAG, "Alarm No GPS location for this round")

                LocalAttendanceReminderManager.scheduleNextAlarm(
                    context = context,
                    delayMinutes = LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES
                )

                return
            }

            handleLocation(
                context = context,
                latitude = location.latitude,
                longitude = location.longitude
            )
        }

        fun handleLocation(
            context: Context,
            latitude: Double,
            longitude: Double
        ) {

            val receiver =
                LocalAttendanceReminderReceiver()

            receiver.checkAttendanceReminder(
                context = context,
                latitude = latitude,
                longitude = longitude
            )
        }

        fun cancelReminderNotification(context: Context) {

            val notificationManager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            notificationManager.cancel(
                REMINDER_NOTIFICATION_ID
            )

            Log.d(
                TAG,
                "Alarm Reminder notification cancelled"
            )
        }
    }
}