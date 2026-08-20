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
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import net.inspirehub.hr.MainActivity
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager

class LocalAttendanceReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {
        val pendingResult = goAsync()
        val appContext = context.applicationContext
        createNotificationChannel(appContext)

        try {
            val sharedPrefManager = SharedPrefManager(appContext)

//        Check if at least one reminder is enabled
            val checkInEnabled = sharedPrefManager.isCheckInReminderEnabled()
            val checkOutEnabled = sharedPrefManager.isCheckOutReminderEnabled()

            if (!checkInEnabled && !checkOutEnabled) {
                Log.d(TAG, "No attendance reminders enabled - skipping alarm")
                pendingResult.finish()
                return
            }

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

                pendingResult.finish()
                return
            }

//        Get location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

            fusedLocationClient
                .getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).addOnSuccessListener { location ->

                    if (location == null) {
                        Log.d(TAG, "Alarm No current location available")

                        // Retry after minimum interval
                        LocalAttendanceReminderManager.scheduleNextAlarm(
                            context = appContext,
                            delayMinutes = LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES
                        )

                        pendingResult.finish()
                        return@addOnSuccessListener
                    }

                    Log.d(
                        TAG,
                        "Alarm Location = " + "${location.latitude}, " + "${location.longitude}"
                    )

                    checkAttendanceReminder(
                        context = appContext,
                        latitude = location.latitude,
                        longitude = location.longitude
                    )

                    pendingResult.finish()
                }
                .addOnFailureListener { exception ->

                    Log.e(TAG, "Alarm Failed to get location", exception)

                    // Retry after minimum interval
                    LocalAttendanceReminderManager.scheduleNextAlarm(
                        context = appContext,
                        delayMinutes = LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES
                    )

                    pendingResult.finish()
                }

        } catch (e: Exception) {

            Log.e(TAG, "Alarm Error inside reminder receiver", e)

            pendingResult.finish()
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

        val actionIntent = Intent(
            context,
            AttendanceReminderActionReceiver::class.java
        ).apply {
            putExtra(
                "REMINDER_TYPE",
                type
            )
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                if (type == CHECK_IN_REMINDER) {
                    CHECK_IN_REQUEST_CODE
                } else {
                    CHECK_OUT_REQUEST_CODE
                },
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

//        val actionText =
//            if (type == CHECK_IN_REMINDER) {
//                context.getString(R.string.check_in)
//            } else {
//                context.getString(R.string.check_out)
//            }

        val notification =
            NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.inspire_hub_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setOngoing(true)
                .setContentIntent(openAppPendingIntent)
//                .addAction(
//                    0,
////                    actionText,
//                    pendingIntent
//                )
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

            val channel =
                NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "Attendance Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                )

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
        private const val CHECK_IN_REQUEST_CODE = 9003
        private const val CHECK_OUT_REQUEST_CODE = 9004
        private const val REMINDER_CHANNEL_ID = "attendance_reminders"
        private const val CHECK_IN_REMINDER = "CHECK_IN_REMINDER"
        private const val CHECK_OUT_REMINDER = "CHECK_OUT_REMINDER"

        fun cancelReminderNotification(context: Context) {

            val notificationManager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            notificationManager.cancel(REMINDER_NOTIFICATION_ID)

            Log.d(
                TAG,
                "Alarm Reminder notification cancelled"
            )
        }
    }
}