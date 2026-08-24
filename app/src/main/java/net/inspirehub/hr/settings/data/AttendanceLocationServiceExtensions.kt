package net.inspirehub.hr.settings.data

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

fun Context.startAttendanceLocationService(
    action: String
) {

    val intent = Intent(
        this,
        AttendanceLocationForegroundService::class.java
    ).apply {
        this.action = action
    }

    try {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            startForegroundService(intent)

        } else {

            startService(intent)
        }

    } catch (e: Throwable) {

        Log.e("Alarm", "Could not start attendance location service", e)
    }
}