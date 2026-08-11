package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import net.inspirehub.hr.SharedPrefManager

object LocationTrackingManager {

    fun updateTracking(
        context: Context,
        attendanceStatus: String? = null
    ) {
        val sharedPref = SharedPrefManager(context)

        val isTracked = sharedPref.getIsTracked()
        val workingHoursOnly = sharedPref.getWorkingHoursOnly()

        val currentAttendanceStatus = attendanceStatus ?: sharedPref.getAttendanceStatus()

        val shouldTrack = when {
            !isTracked -> false

            !workingHoursOnly -> true

            else -> currentAttendanceStatus == "checked_in"
        }


       Log.d(
            "Test TRACKING_MANAGER",
            "isTracked=$isTracked | " +
                    "workingHoursOnly=$workingHoursOnly | " +
                    "attendanceStatus=$currentAttendanceStatus  | " +
                    "shouldTrack=$shouldTrack"
        )

        val serviceIntent = Intent(
            context,
            LocationForegroundService::class.java
        )

        if (shouldTrack) {

            Log.d("Test TRACKING_MANAGER", "🚀 Starting LocationForegroundService")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(
                    context,
                    serviceIntent
                )
            } else {
                context.startService(serviceIntent)
            }

        } else {

            Log.d("Test TRACKING_MANAGER", "🛑 Stopping LocationForegroundService")

            context.stopService(serviceIntent)
        }
    }
}