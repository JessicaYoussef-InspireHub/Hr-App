//package net.inspirehub.hr.check_in_out.data
//
//import android.content.Context
//import android.content.Intent
//import androidx.core.content.ContextCompat
//import net.inspirehub.hr.SharedPrefManager
//import android.Manifest
//import android.content.pm.PackageManager
//import android.util.Log
//
//object AttendanceReminderForegroundManager {
//
//
//    fun start(context: Context) {
//
//        val appContext = context.applicationContext
//
//        val fineGranted = ContextCompat.checkSelfPermission(
//                appContext,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//
//        val coarseGranted =
//            ContextCompat.checkSelfPermission(
//                appContext,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//
//        if (!fineGranted && !coarseGranted) {
//            Log.e(
//                "ATTENDANCE_REMINDER",
//                "Cannot start FGS: location permission not granted"
//            )
//            return
//        }
//
//        SharedPrefManager(appContext)
//            .setAttendanceReminderEnabled(true)
//
//        val intent = Intent(
//            appContext,
//            AttendanceReminderForegroundService::class.java
//        )
//
//        ContextCompat.startForegroundService(
//            appContext,
//            intent
//        )
//    }
//
//    fun stop(context: Context) {
//
//        val appContext = context.applicationContext
//
//        SharedPrefManager(appContext).setAttendanceReminderEnabled(false)
//
//        val intent = Intent(
//            appContext,
//            AttendanceReminderForegroundService::class.java
//        )
//
//        appContext.stopService(intent)
//    }
//
//    fun restore(context: Context) {
//
//        val appContext = context.applicationContext
//
//        val sharedPref = SharedPrefManager(appContext)
//
//        // Reminder was not enabled
//        if (!sharedPref.isAttendanceReminderEnabled()) {
//            return
//        }
//
//        val fineGranted =
//            ContextCompat.checkSelfPermission(
//                appContext,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//
//        val coarseGranted =
//            ContextCompat.checkSelfPermission(
//                appContext,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//
//        if (!fineGranted && !coarseGranted) {
//            return
//        }
//
//        start(appContext)
//    }
//}
