package net.inspirehub.hr.check_in_out.data

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
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

            // Reminder disabled
            if (!sharedPrefManager.isAttendanceReminderEnabled()) {
                Log.d(TAG, "Alarm Reminder disabled - skipping alarm")
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

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

            fusedLocationClient
                .getCurrentLocation(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).addOnSuccessListener { location ->

                if (location == null) {
                    Log.d(TAG, "Alarm No current location available" )

                    // Retry after minimum interval
                    LocalAttendanceReminderManager.scheduleNextAlarm(
                        context = appContext,
                        delayMinutes = LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES
                    )

                    pendingResult.finish()

                    return@addOnSuccessListener
                }

                Log.d(TAG,"Alarm Location = " + "${location.latitude}, " + "${location.longitude}")

                checkAttendanceReminder(
                    context = appContext,
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                pendingResult.finish()
            }
                .addOnFailureListener { exception ->

                    Log.e( TAG, "Alarm Failed to get location", exception)

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



    private fun checkAttendanceReminder(
        context: Context,
        latitude: Double,
        longitude: Double
    ) {

        val sharedPrefManager = SharedPrefManager(context)

        if (!sharedPrefManager.isAttendanceReminderEnabled()) {
            return
        }

        val companies = sharedPrefManager.getCompaniesLatLng()

        if (companies.isEmpty()) {

            Log.d(TAG, "Alarm No company locations found")

            // Try again after minimum interval
            LocalAttendanceReminderManager.scheduleNextAlarm(
                context = context,
                delayMinutes = LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES
            )

            return
        }

        val attendanceStatus = sharedPrefManager.getAttendanceStatus()

        // Find nearest company
        var nearestDistance = Float.MAX_VALUE

        var nearestCompanyName = ""

        val isInsideAnyCompany =
            companies.any { company ->
                val distance =
                    calculateDistance(
                        latitude = latitude,
                        longitude = longitude,
                        companyLat = company.lat,
                        companyLng = company.lng
                    )

                if (distance < nearestDistance) {
                    nearestDistance = distance
                    nearestCompanyName = company.name
                }

                Log.d( TAG, "Alarm Company=${company.name}, " + "distance=$distance, " + "allowed=${company.allowedDistance}"  )
                distance <= company.allowedDistance
            }

        Log.d(TAG, "Alarm nearestCompany=$nearestCompanyName nearestDistance=$nearestDistance")

        Log.d(TAG, "Alarm isInside=$isInsideAnyCompany attendanceStatus=$attendanceStatus")

        val lastReminderType = sharedPrefManager.getLastAttendanceReminderType()

        when {

            // Employee is inside company but didn't check in
            isInsideAnyCompany && attendanceStatus == "checked_out" -> {

                if (lastReminderType != CHECK_IN_REMINDER) {
                    showReminderNotification(
                        context = context,
                        title = context.getString(R.string.check_in_reminder),
                        message = context.getString(R.string.you_are_inside_work_location_without_check_in)
                    )

                    sharedPrefManager.saveLastAttendanceReminderType(CHECK_IN_REMINDER)

                    Log.d(TAG, "Alarm CHECK-IN reminder shown")

                } else {

                    Log.d(TAG, "Alarm CHECK-IN reminder already shown")
                }
            }

            // Employee left company but didn't check out
            !isInsideAnyCompany && attendanceStatus == "checked_in" -> {

                if (lastReminderType != CHECK_OUT_REMINDER) {

                    showReminderNotification(
                        context = context,
                        title = context.getString(R.string.check_out_reminder),
                        message = context.getString(R.string.you_left_work_location_without_check_out)
                    )

                    sharedPrefManager.saveLastAttendanceReminderType(CHECK_OUT_REMINDER)

                    Log.d(TAG, "Alarm CHECK-OUT reminder shown")

                } else {

                    Log.d(TAG, "Alarm CHECK-OUT reminder already shown")
                }
            }

            else -> {

                if (lastReminderType != null) {
                    sharedPrefManager.saveLastAttendanceReminderType(null)

                    Log.d(TAG, "Alarm Reminder state reset")
                }
            }
        }

        // Schedule next check based on distance
        val nextIntervalMinutes = calculateNextIntervalMinutes(
                distanceMeters = nearestDistance
            )

        Log.d(TAG, "Alarm next check after $nextIntervalMinutes minutes")

        LocalAttendanceReminderManager.scheduleNextAlarm(
            context = context,
            delayMinutes = nextIntervalMinutes
        )
    }


    private fun showReminderNotification(
        context: Context,
        title: String,
        message: String
    ) {

        val notificationManager = context.getSystemService(android.app.NotificationManager::class.java)

        val notification = androidx.core.app.NotificationCompat
                .Builder(
                    context,
                    REMINDER_CHANNEL_ID
                )
                .setSmallIcon(R.drawable.inspire_hub_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setCategory(androidx.core.app.NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .build()

        notificationManager.notify(REMINDER_NOTIFICATION_ID, notification)

        Log.d(TAG, "Alarm Reminder notification shown: $title")
    }

    private fun createNotificationChannel(
        context: Context
    ) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
        ) {

            val channel =
                android.app.NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "Attendance Reminders",
                    android.app.NotificationManager.IMPORTANCE_HIGH
                )

            val manager = context.getSystemService(
                    android.app.NotificationManager::class.java
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

        android.location.Location.distanceBetween(
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

        private const val REMINDER_CHANNEL_ID = "attendance_reminders"

        private const val CHECK_IN_REMINDER = "CHECK_IN_REMINDER"

        private const val CHECK_OUT_REMINDER = "CHECK_OUT_REMINDER"
    }
}