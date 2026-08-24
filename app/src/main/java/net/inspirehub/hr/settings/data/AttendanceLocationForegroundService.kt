package net.inspirehub.hr.settings.data

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import net.inspirehub.hr.MainActivity
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager

class AttendanceLocationForegroundService : Service() {

    companion object {

        const val ACTION_START = "net.inspirehub.hr.ATTENDANCE_LOCATION_START"

        const val ACTION_GET_LOCATION = "net.inspirehub.hr.ATTENDANCE_LOCATION_GET"

        const val ACTION_STOP = "net.inspirehub.hr.ATTENDANCE_LOCATION_STOP"

        private const val CHANNEL_ID = "attendance_location_service"

        private const val NOTIFICATION_ID = 9100

        @Volatile
        var isRunning = false
            private set
    }

    private var locationRequestInProgress = false

    override fun onCreate() {
        super.onCreate()

        isRunning = true

        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (!startAsForeground()) {
            return START_NOT_STICKY
        }

        when (intent?.action) {

            ACTION_START -> {
                requestLocation()
            }

            ACTION_GET_LOCATION -> {
                requestLocation()
            }

            ACTION_STOP -> {
                stopService()
                return START_NOT_STICKY
            }
        }

        return START_STICKY
    }

    private fun requestLocation() {

        if (locationRequestInProgress) {

            Log.d("Alarm", "Location request already running -> skip")

            return
        }

        val sharedPrefManager = SharedPrefManager(applicationContext)

        if (
            !sharedPrefManager.isCheckInReminderEnabled() &&
            !sharedPrefManager.isCheckOutReminderEnabled()
        ) {

            Log.d(
                "Alarm",
                "No reminder enabled -> stopping service"
            )

            stopService()
            return
        }

        if (
            checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Log.e(
                "Alarm",
                "Fine location permission missing"
            )

            return
        }

        locationRequestInProgress = true

        AttendanceLocationFix.request(
            context = applicationContext
        ) { location ->

            locationRequestInProgress = false

            if (location == null) {

                Log.d(
                    "Alarm",
                    "Foreground service -> no GPS location"
                )

                LocalAttendanceReminderManager.scheduleNextAlarm(
                    applicationContext,
                    LocalAttendanceReminderManager.MIN_INTERVAL_MINUTES
                )

                return@request
            }

            Log.d(
                "Alarm",
                "Foreground service -> GPS Location = " +
                        "${location.latitude}, " +
                        "${location.longitude}, " +
                        "accuracy=${location.accuracy}"
            )

            LocalAttendanceReminderReceiver.handleLocation(
                context = applicationContext,
                latitude = location.latitude,
                longitude = location.longitude
            )
        }
    }

    private fun startAsForeground(): Boolean {

        if (
            checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Log.e("Alarm", "Cannot start location foreground service: permission missing")

            stopSelf()

            return false
        }

        return try {

            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                buildNotification(),
                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.Q
                ) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                } else {
                    0
                }
            )

            true

        } catch (e: Throwable) {

            Log.e("Alarm", "Could not promote service to foreground", e)

            stopSelf()

            false
        }
    }

    private fun buildNotification(): Notification {

        val openAppIntent =
            Intent(
                this,
                MainActivity::class.java
            ).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                9101,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.inspire_hub_logo)
            .setContentTitle(getString(R.string.attendance_reminders_active))
            .setContentText(getString(R.string.work_location_checked_periodically))
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(false)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Attendance location tracking",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {

                    description = "Used while attendance reminders are enabled"

                    setShowBadge(false)
                }

            getSystemService(
                NotificationManager::class.java
            ).createNotificationChannel(channel)
        }
    }

    private fun stopService() {

        isRunning = false

        ServiceCompat.stopForeground(
            this,
            ServiceCompat.STOP_FOREGROUND_REMOVE
        )

        stopSelf()
    }

    override fun onDestroy() {

        isRunning = false

        locationRequestInProgress = false

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = null
}