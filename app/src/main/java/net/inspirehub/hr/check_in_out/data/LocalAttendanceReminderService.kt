package net.inspirehub.hr.check_in_out.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager

class LocalAttendanceReminderService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPrefManager: SharedPrefManager
    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {

            val location = result.lastLocation ?: return

            checkAttendanceReminder(
                latitude = location.latitude,
                longitude = location.longitude
            )
        }
    }

    override fun onCreate() {
        super.onCreate()

        sharedPrefManager = SharedPrefManager(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createNotificationChannel()

        startForeground(
            FOREGROUND_NOTIFICATION_ID,
            createForegroundNotification()
        )

        startLocationUpdates()

        Log.d(TAG, "Local Attendance Reminder Service started")
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (!sharedPrefManager.isAttendanceReminderEnabled()) {
            stopSelf()
            return START_NOT_STICKY
        }

        return START_STICKY
    }

    private fun startLocationUpdates() {

        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Local Location permission not granted")
            stopSelf()
            return
        }

        val locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                60_000L
            )
                .setMinUpdateIntervalMillis(60_000L)
                .setMaxUpdateDelayMillis(60_000L)
                .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        Log.d(TAG, "Local Location updates started - every 1 minute")
    }


    private fun checkAttendanceReminder(
        latitude: Double,
        longitude: Double
    ) {

        if (!sharedPrefManager.isAttendanceReminderEnabled()) {
            return
        }

        val companies =
            sharedPrefManager.getCompaniesLatLng()

        if (companies.isEmpty()) {
            Log.d(TAG, "Local No company locations found")
            return
        }

        val attendanceStatus =
            sharedPrefManager.getAttendanceStatus()

        val isInsideAnyCompany =
            companies.any { company ->

                val distance = calculateDistance(
                    latitude,
                    longitude,
                    company.lat,
                    company.lng
                )

                Log.d(
                    TAG,
                    "Local Company=${company.name}, " +
                            "distance=$distance, " +
                            "allowed=${company.allowedDistance}"
                )

                distance <= company.allowedDistance
            }

        Log.d(
            TAG,
            "isInside=$isInsideAnyCompany " +
                    "attendanceStatus=$attendanceStatus"
        )

        val lastReminderType =
            sharedPrefManager.getLastAttendanceReminderType()

        when {

            // Employee is inside company but didn't check in
            isInsideAnyCompany && attendanceStatus == "checked_out" -> {

                if (lastReminderType != CHECK_IN_REMINDER) {

                    showReminderNotification(
                        title = getString(
                            R.string.check_in_reminder
                        ),
                        message = getString(
                            R.string.you_are_inside_work_location_without_check_in
                        )
                    )

                    sharedPrefManager.saveLastAttendanceReminderType(
                        CHECK_IN_REMINDER
                    )

                    Log.d(
                        TAG,
                        "Local CHECK-IN reminder shown once"
                    )
                } else {

                    Log.d(
                        TAG,
                        "Local CHECK-IN reminder already shown"
                    )
                }
            }

            // Employee left company but didn't check out
            !isInsideAnyCompany && attendanceStatus == "checked_in" -> {

                if (lastReminderType != CHECK_OUT_REMINDER) {

                    showReminderNotification(
                        title = getString(
                            R.string.check_out_reminder
                        ),
                        message = getString(
                            R.string.you_left_work_location_without_check_out
                        )
                    )

                    sharedPrefManager.saveLastAttendanceReminderType(CHECK_OUT_REMINDER)

                    Log.d(
                        TAG,
                        "Local CHECK-OUT reminder shown once"
                    )
                } else {

                    Log.d(
                        TAG,
                        "Local CHECK-OUT reminder already shown"
                    )
                }
            }

            else -> {


                if (lastReminderType != null) {

                    sharedPrefManager
                        .saveLastAttendanceReminderType(null)

                    Log.d(
                        TAG,
                        "Local Attendance reminder state reset"
                    )
                }
            }
        }
    }

    private fun showReminderNotification(
        title: String,
        message: String
    ) {

        val notificationManager =
            getSystemService(
                NotificationManager::class.java
            )

        val notification =
            NotificationCompat.Builder(
                this,
                REMINDER_CHANNEL_ID
            )
                .setSmallIcon(R.drawable.inspire_hub_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .build()

        notificationManager.notify(
            REMINDER_NOTIFICATION_ID,
            notification
        )

        Log.d(TAG, "Local Attendance reminder shown: $title")
    }

    private fun createForegroundNotification() =
        NotificationCompat.Builder(
            this,
            FOREGROUND_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.inspire_hub_logo)
            .setContentTitle(getString(R.string.attendance_reminder))
            .setContentText(getString(R.string.attendance_reminder_running))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val foregroundChannel =
                NotificationChannel(
                    FOREGROUND_CHANNEL_ID,
                    "Attendance Reminder Service",
                    NotificationManager.IMPORTANCE_LOW
                )

            val reminderChannel =
                NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "Attendance Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                )

            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(foregroundChannel)

            manager.createNotificationChannel(reminderChannel)
        }
    }

    private fun calculateDistance(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Float {

        val results = FloatArray(1)

        android.location.Location.distanceBetween(
            lat1,
            lng1,
            lat2,
            lng2,
            results
        )

        return results[0]
    }

    override fun onDestroy() {

        fusedLocationClient.removeLocationUpdates(
            locationCallback
        )

        Log.d(TAG, "Local Attendance Reminder Service stopped")

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {

        private const val TAG = "LOCAL_ATTENDANCE_REMINDER"

        private const val FOREGROUND_NOTIFICATION_ID = 9001

        private const val REMINDER_NOTIFICATION_ID = 9002

        private const val FOREGROUND_CHANNEL_ID = "attendance_reminder_service"

        private const val REMINDER_CHANNEL_ID = "attendance_reminders"

        private const val CHECK_IN_REMINDER = "CHECK_IN_REMINDER"
        private const val CHECK_OUT_REMINDER = "CHECK_OUT_REMINDER"
    }
}
