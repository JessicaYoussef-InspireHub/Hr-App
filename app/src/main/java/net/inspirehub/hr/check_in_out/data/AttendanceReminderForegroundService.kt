//package net.inspirehub.hr.check_in_out.data
//
//import android.Manifest
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.Service
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Build
//import android.os.IBinder
//import android.util.Log
//import androidx.core.app.ActivityCompat
//import androidx.core.app.NotificationCompat
//import com.google.android.gms.location.*
//import net.inspirehub.hr.R
//import net.inspirehub.hr.SharedPrefManager
//
//class AttendanceReminderForegroundService : Service() {
//
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//    private lateinit var locationCallback: LocationCallback
//
//    override fun onCreate() {
//        super.onCreate()
//
//        Log.d(TAG, "Attendance Reminder Foreground Service created")
//
//        createNotificationChannel()
//
//        startForeground(
//            FOREGROUND_NOTIFICATION_ID,
//            createForegroundNotification()
//        )
//
//        fusedLocationClient =
//            LocationServices.getFusedLocationProviderClient(
//                this
//            )
//
//        setupLocationCallback()
//
//        startLocationUpdates()
//    }
//
//    override fun onStartCommand(
//        intent: Intent?,
//        flags: Int,
//        startId: Int
//    ): Int {
//
//        Log.d(
//            TAG,
//            "Attendance Reminder Foreground Service started"
//        )
//
//        return START_STICKY
//    }
//
//    private fun setupLocationCallback() {
//
//        locationCallback =
//            object : LocationCallback() {
//
//                override fun onLocationResult(
//                    result: LocationResult
//                ) {
//
//                    val location =
//                        result.lastLocation
//                            ?: return
//
//                    Log.d(
//                        TAG,
//                        "Location = ${location.latitude}, ${location.longitude}"
//                    )
//
//                    checkAttendanceReminder(
//                        latitude = location.latitude,
//                        longitude = location.longitude
//                    )
//                }
//            }
//    }
//
//    private fun startLocationUpdates() {
//
//        if (
//            ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//            &&
//            ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//
//            Log.e(TAG, "Location permission not granted")
//
//            stopSelf()
//
//            return
//        }
//
//        val locationRequest =
//            LocationRequest.Builder(
//                Priority.PRIORITY_HIGH_ACCURACY,
//                10_000L
//            )
//                .setMinUpdateIntervalMillis(10_000L)
//                .setMinUpdateDistanceMeters(0f)
//                .build()
//
//        fusedLocationClient.requestLocationUpdates(
//            locationRequest,
//            locationCallback,
//            mainLooper
//        )
//
//        Log.d(
//            TAG,
//            "Location updates started - every 10 seconds"
//        )
//    }
//
////    private fun startLocationUpdates() {
////
////        if (
////            ActivityCompat.checkSelfPermission(
////                this,
////                Manifest.permission.ACCESS_FINE_LOCATION
////            ) != PackageManager.PERMISSION_GRANTED
////            &&
////            ActivityCompat.checkSelfPermission(
////                this,
////                Manifest.permission.ACCESS_COARSE_LOCATION
////            ) != PackageManager.PERMISSION_GRANTED
////        ) {
////
////            Log.e(
////                TAG,
////                "Location permission not granted"
////            )
////
////            stopSelf()
////
////            return
////        }
////
////        val locationRequest =
////            LocationRequest.Builder(
////                Priority.PRIORITY_HIGH_ACCURACY,
////                LOCATION_INTERVAL
////            )
////                .setMinUpdateIntervalMillis(LOCATION_INTERVAL)
////                .setMinUpdateDistanceMeters(3f)
////                .build()
////
////        fusedLocationClient.requestLocationUpdates(
////            locationRequest,
////            locationCallback,
////            mainLooper
////        )
////
////        Log.d(TAG, "Location updates started - every 10 minutes")
////    }
//
//    private fun checkAttendanceReminder(
//        latitude: Double,
//        longitude: Double
//    ) {
//
//        val sharedPrefManager =
//            SharedPrefManager(this)
//
//        if (
//            !sharedPrefManager
//                .isAttendanceReminderEnabled()
//        ) {
//
//            Log.d(
//                TAG,
//                "Reminder disabled - stopping service"
//            )
//
//            stopSelf()
//
//            return
//        }
//
//        val companies =
//            sharedPrefManager.getCompaniesLatLng()
//
//        if (companies.isEmpty()) {
//
//            Log.d(
//                TAG,
//                "No company locations found"
//            )
//
//            return
//        }
//
//        val attendanceStatus =
//            sharedPrefManager.getAttendanceStatus()
//
//        var nearestDistance =
//            Float.MAX_VALUE
//
//        var nearestCompanyName = ""
//
//        val isInsideAnyCompany =
//            companies.any { company ->
//
//                val distance =
//                    calculateDistance(
//                        latitude = latitude,
//                        longitude = longitude,
//                        companyLat = company.lat,
//                        companyLng = company.lng
//                    )
//
//                if (distance < nearestDistance) {
//
//                    nearestDistance = distance
//
//                    nearestCompanyName =
//                        company.name
//                }
//
//                Log.d(
//                    TAG,
//                    "Company=${company.name}, " +
//                            "distance=$distance, " +
//                            "allowed=${company.allowedDistance}"
//                )
//
//                distance <= company.allowedDistance
//            }
//
//        Log.d(
//            TAG,
//            "Nearest company=$nearestCompanyName " +
//                    "distance=$nearestDistance"
//        )
//
//        Log.d(
//            TAG,
//            "isInside=$isInsideAnyCompany " +
//                    "attendanceStatus=$attendanceStatus"
//        )
//
//        when {
//
//            // Employee is inside company but didn't check in
//            isInsideAnyCompany &&
//                    attendanceStatus == "checked_out" -> {
//
//                showReminderNotification(
//                    title = getString(
//                        R.string.check_in_reminder
//                    ),
//                    message = getString(
//                        R.string.you_are_inside_work_location_without_check_in
//                    )
//                )
//
//                Log.d(
//                    TAG,
//                    "CHECK-IN reminder notification shown"
//                )
//            }
//
//            // Employee left company but didn't check out
//            !isInsideAnyCompany &&
//                    attendanceStatus == "checked_in" -> {
//
//                showReminderNotification(
//                    title = getString(
//                        R.string.check_out_reminder
//                    ),
//                    message = getString(
//                        R.string.you_left_work_location_without_check_out
//                    )
//                )
//
//                Log.d(
//                    TAG,
//                    "CHECK-OUT reminder notification shown"
//                )
//            }
//        }
//
////        val lastReminderType =
////            sharedPrefManager
////                .getLastAttendanceReminderType()
////
////        when {
////
////            /*
////             * Employee is inside company
////             * but hasn't checked in.
////             */
////            isInsideAnyCompany &&
////                    attendanceStatus == "checked_out" -> {
////
////                if (
////                    lastReminderType !=
////                    CHECK_IN_REMINDER
////                ) {
////
////                    showReminderNotification(
////                        title = getString(
////                            R.string.check_in_reminder
////                        ),
////                        message = getString(
////                            R.string.you_are_inside_work_location_without_check_in
////                        )
////                    )
////
////                    sharedPrefManager
////                        .saveLastAttendanceReminderType(
////                            CHECK_IN_REMINDER
////                        )
////
////                    Log.d(
////                        TAG,
////                        "CHECK-IN reminder shown"
////                    )
////                }
////            }
////
////            /*
////             * Employee left company
////             * but hasn't checked out.
////             */
////            !isInsideAnyCompany &&
////                    attendanceStatus == "checked_in" -> {
////
////                if (
////                    lastReminderType !=
////                    CHECK_OUT_REMINDER
////                ) {
////
////                    showReminderNotification(
////                        title = getString(
////                            R.string.check_out_reminder
////                        ),
////                        message = getString(
////                            R.string.you_left_work_location_without_check_out
////                        )
////                    )
////
////                    sharedPrefManager
////                        .saveLastAttendanceReminderType(
////                            CHECK_OUT_REMINDER
////                        )
////
////                    Log.d(
////                        TAG,
////                        "CHECK-OUT reminder shown"
////                    )
////                }
////            }
////
////            else -> {
////
////                if (lastReminderType != null) {
////
////                    sharedPrefManager
////                        .saveLastAttendanceReminderType(
////                            null
////                        )
////
////                    Log.d(
////                        TAG,
////                        "Reminder state reset"
////                    )
////                }
////            }
////        }
//    }
//
//    private fun showReminderNotification(
//        title: String,
//        message: String
//    ) {
//
//        val notificationManager =
//            getSystemService(
//                NotificationManager::class.java
//            )
//
//        val notification =
//            NotificationCompat.Builder(
//                this,
//                REMINDER_CHANNEL_ID
//            )
//                .setSmallIcon(
//                    R.drawable.inspire_hub_logo
//                )
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(
//                    NotificationCompat.PRIORITY_HIGH
//                )
//                .setCategory(
//                    NotificationCompat.CATEGORY_REMINDER
//                )
//                .setAutoCancel(true)
//                .build()
//
//        notificationManager.notify(
//            REMINDER_NOTIFICATION_ID,
//            notification
//        )
//    }
//
//    private fun createForegroundNotification() =
//        NotificationCompat.Builder(
//            this,
//            FOREGROUND_CHANNEL_ID
//        )
//            .setSmallIcon(
//                R.drawable.inspire_hub_logo
//            )
//            .setContentTitle(
//                getString(
//                    R.string.reminders
//                )
//            )
//            .setContentText(
//                "Attendance reminder is active"
//            )
//            .setOngoing(true)
//            .setPriority(
//                NotificationCompat.PRIORITY_LOW
//            )
//            .build()
//
//    private fun createNotificationChannel() {
//
//        if (
//            Build.VERSION.SDK_INT >=
//            Build.VERSION_CODES.O
//        ) {
//
//            val manager =
//                getSystemService(
//                    NotificationManager::class.java
//                )
//
//            val foregroundChannel =
//                NotificationChannel(
//                    FOREGROUND_CHANNEL_ID,
//                    "Attendance Reminder Service",
//                    NotificationManager.IMPORTANCE_LOW
//                )
//
//            val reminderChannel =
//                NotificationChannel(
//                    REMINDER_CHANNEL_ID,
//                    "Attendance Reminders",
//                    NotificationManager.IMPORTANCE_HIGH
//                )
//
//            manager.createNotificationChannel(
//                foregroundChannel
//            )
//
//            manager.createNotificationChannel(
//                reminderChannel
//            )
//        }
//    }
//
//    private fun calculateDistance(
//        latitude: Double,
//        longitude: Double,
//        companyLat: Double,
//        companyLng: Double
//    ): Float {
//
//        val results = FloatArray(1)
//
//        android.location.Location.distanceBetween(
//            latitude,
//            longitude,
//            companyLat,
//            companyLng,
//            results
//        )
//
//        return results[0]
//    }
//
//    override fun onDestroy() {
//
//        Log.d(
//            TAG,
//            "Attendance Reminder Foreground Service destroyed"
//        )
//
//        if (
//            ::fusedLocationClient.isInitialized &&
//            ::locationCallback.isInitialized
//        ) {
//
//            fusedLocationClient.removeLocationUpdates(
//                locationCallback
//            )
//        }
//
//        super.onDestroy()
//    }
//
//    override fun onBind(
//        intent: Intent?
//    ): IBinder? = null
//
//    companion object {
//
//        private const val TAG =
//            "ATTENDANCE_REMINDER_FGS"
//
//        private const val LOCATION_INTERVAL =
//            10 * 1000L
//
//        private const val FOREGROUND_NOTIFICATION_ID =
//            9001
//
//        private const val REMINDER_NOTIFICATION_ID =
//            9002
//
//        private const val FOREGROUND_CHANNEL_ID =
//            "attendance_reminder_foreground"
//
//        private const val REMINDER_CHANNEL_ID =
//            "attendance_reminders"
//
//        private const val CHECK_IN_REMINDER =
//            "CHECK_IN_REMINDER"
//
//        private const val CHECK_OUT_REMINDER =
//            "CHECK_OUT_REMINDER"
//    }
//}