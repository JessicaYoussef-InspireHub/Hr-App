package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import net.inspirehub.hr.SharedPrefManager

/**
 * The one door in and out of location tracking, and the place the two modes are
 * chosen between.
 *
 *  showNotification = true  - a long-lived foreground service holding a
 *                             LocationCallback. Android keeps its notification on
 *                             screen for the life of the service; the Settings
 *                             hide-auto-notification switch can only minimise it.
 *  showNotification = false - an AlarmManager chain: one tick, one reading, service
 *                             dies, notification goes with it.
 *
 * Both are driven from here, so every caller - the check in/out screen on resume, an
 * attendance action, an FCM config update - keeps working unchanged.
 */
object LocationTrackingManager {

    fun updateTracking(
        context: Context,
        attendanceStatus: String? = null
    ) {
        val appContext = context.applicationContext

        val sharedPref = SharedPrefManager(appContext)

        val shouldTrack = LocationFixHandler.shouldTrack(
            context = appContext,
            attendanceStatus = attendanceStatus
        )

        val showNotification = sharedPref.getShowNotification()

        Log.d(
            "Test TRACKING_MANAGER",
            "isTracked=${sharedPref.getIsTracked()} | " +
                    "workingHoursOnly=${sharedPref.getWorkingHoursOnly()} | " +
                    "attendanceStatus=${attendanceStatus ?: sharedPref.getAttendanceStatus()} | " +
                    "showNotification=$showNotification | " +
                    "shouldTrack=$shouldTrack"
        )

        if (!shouldTrack) {

            Log.d("Test TRACKING_MANAGER", "🛑 Stopping location tracking")

            TrackingAlarmManager.stop(appContext)

            stopService(appContext)

            // The next reading after tracking resumes starts a fresh baseline.
            sharedPref.clearLastTrackedLocation()

            return
        }

        if (showNotification) {

            Log.d("Test TRACKING_MANAGER", "🚀 Starting LocationForegroundService")

            // In case the backend just switched us out of alarm mode.
            TrackingAlarmManager.stop(appContext)

            startService(appContext)

            return
        }

        Log.d("Test TRACKING_MANAGER", "🚀 Starting tracking in alarm mode")

        // In case the backend just switched us out of foreground-service mode. The
        // running service would otherwise keep its own callback and its notification.
        stopService(appContext)

        TrackingAlarmManager.start(appContext)
    }

    /**
     * Put the current notification back on screen with the settings the
     * hide-auto-notification switch now asks for.
     *
     * A foreground service's notification cannot be moved to another channel in
     * place, so the service is restarted - which is cheap, since onStartCommand only
     * re-registers a callback it already guards against duplicating. Does nothing in
     * alarm mode, where there is no notification to restyle between readings.
     */
    fun refreshNotification(context: Context) {

        val appContext = context.applicationContext

        if (!SharedPrefManager(appContext).getShowNotification()) return

        if (!LocationFixHandler.shouldTrack(appContext)) return

        Log.d("Test TRACKING_MANAGER", "🔄 Restyling the tracking notification")

        stopService(appContext)

        startService(appContext)
    }

    /**
     * Never lets a refusal reach the caller. From Android 12 on a background process
     * may not start a foreground service, and this is reached from places that are in
     * the background - a boot broadcast, an FCM config update. The check in/out screen
     * calls updateTracking() on every resume, so a refused start is picked up again
     * the next time the employee opens the app.
     */
    private fun startService(context: Context) {

        val serviceIntent = Intent(
            context,
            LocationForegroundService::class.java
        )

        runCatching {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

        }.onFailure {

            Log.e(
                "Test TRACKING_MANAGER",
                "Could not start the tracking service from here",
                it
            )
        }
    }

    private fun stopService(context: Context) {

        context.stopService(
            Intent(
                context,
                LocationForegroundService::class.java
            )
        )
    }
}
