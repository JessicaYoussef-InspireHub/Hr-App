package net.inspirehub.hr.settings.data

import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * Convenience so the receiver and the settings screen read the same way.
 *
 * It deliberately does NOT swallow the failure any more. From Android 12 on,
 * startForegroundService() from a background process throws
 * ForegroundServiceStartNotAllowedException, and the alarm receiver has to know
 * about that so it can take the reading itself instead of silently losing the
 * round. Callers that are on a foreground path wrap this in runCatching.
 */
fun Context.startAttendanceLocationService(
    action: String
) {

    val intent = Intent(
        this,
        AttendanceLocationForegroundService::class.java
    ).apply {
        this.action = action
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        startForegroundService(intent)

    } else {

        startService(intent)
    }
}
