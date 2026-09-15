package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
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

    /**
     * Every transition runs here, so only one of them is ever half-done.
     *
     * This object is called from a background coroutine (an FCM config update, a
     * boot broadcast) and from the main thread (the check in/out screen on resume,
     * an attendance action) - and when the backend flips is_tracked mid-session,
     * both happen within the same second. Two threads deciding "stop" and "start"
     * against the same service at once is what ends as
     * ForegroundServiceDidNotStartInTimeException: the stop wins the race, the
     * service is destroyed, and the start request that was already in flight is left
     * with a foreground clock nobody can answer.
     */
    private val gate = Handler(Looper.getMainLooper())

    /**
     * stopService() only asks; the service is destroyed a little later, on its own
     * main-thread turn. A startForegroundService() issued inside that window lands on
     * a service that is already on its way out, so its onStartCommand - and with it
     * its startForeground() - is never delivered. Starts are held back until the
     * previous stop has actually been through the queue.
     */
    private const val STOP_SETTLE_MS = 400L

    /** Marks the starts held back by that window, so a stop can cancel just those. */
    private val DEFERRED_START = Any()

    private var lastStopUptime = 0L

    private fun onGate(block: () -> Unit) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            gate.post(block)
        }
    }

    fun updateTracking(
        context: Context,
        attendanceStatus: String? = null
    ) = onGate {

        applyTracking(
            context = context,
            attendanceStatus = attendanceStatus
        )
    }

    private fun applyTracking(
        context: Context,
        attendanceStatus: String?
    ) {
        val appContext = context.applicationContext

        val sharedPref = SharedPrefManager(appContext)

        /*
         * Nothing below may be attempted without the location permissions: the
         * service would have to stop itself before ever showing its notification,
         * and Android kills the process for that. Tear the machine down instead and
         * let the next resume - after the employee has granted them - rebuild it.
         */
        if (!hasTrackingLocationPermissions(appContext)) {

            Log.d(
                "Test TRACKING_MANAGER",
                "Location permissions missing -> tracking stays off"
            )

            TrackingAlarmManager.stop(appContext)

            stopService(appContext)

            return
        }

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

        /*
         * Delayed, because TrackingAlarmManager.start() takes a first reading through
         * the same service we have just asked to stop - the one order that is
         * guaranteed to produce the crash this whole gate exists to prevent.
         */
        HandlerCompat.postDelayed(
            gate,
            { TrackingAlarmManager.start(appContext) },
            DEFERRED_START,
            STOP_SETTLE_MS
        )
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
    fun refreshNotification(context: Context) = onGate {

        val appContext = context.applicationContext

        if (!SharedPrefManager(appContext).getShowNotification()) return@onGate

        if (!hasTrackingLocationPermissions(appContext)) return@onGate

        if (!LocationFixHandler.shouldTrack(appContext)) return@onGate

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

        val sinceStop = SystemClock.uptimeMillis() - lastStopUptime

        /*
         * A stop we asked for is still working its way through the main-thread queue.
         * Starting now would hand the request to a service that is about to be
         * destroyed, and the request would die with it - still holding the foreground
         * clock startForegroundService() armed. Let the stop land first.
         */
        if (sinceStop < STOP_SETTLE_MS) {

            Log.d(
                "Test TRACKING_MANAGER",
                "A stop is still settling -> start in ${STOP_SETTLE_MS - sinceStop}ms"
            )

            val appContext = context.applicationContext

            HandlerCompat.postDelayed(
                gate,
                { startServiceNow(appContext) },
                DEFERRED_START,
                STOP_SETTLE_MS - sinceStop
            )

            return
        }

        startServiceNow(context)
    }

    private fun startServiceNow(context: Context) {

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

        /*
         * Drops any start still sitting in the queue behind an earlier stop: this one
         * supersedes it, and letting it fire afterwards would put the service back up
         * a moment after we decided it should be gone. The token keeps this to the
         * deferred starts - an updateTracking() posted here from a background thread
         * is a decision of its own and must still run.
         */
        gate.removeCallbacksAndMessages(DEFERRED_START)

        lastStopUptime = SystemClock.uptimeMillis()

        context.stopService(
            Intent(
                context,
                LocationForegroundService::class.java
            )
        )
    }
}
