package net.inspirehub.hr.check_in_out.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import net.inspirehub.hr.SharedPrefManager

/**
 * The clock of tracking when SharedPrefManager.getShowNotification() is false.
 *
 * Same shape as LocalAttendanceReminderManager, and for the same reason: the alarm
 * IS the feature, the foreground service is only its hands. Between two readings
 * nothing of ours runs, which is precisely what lets the notification go away.
 *
 * ELAPSED_REALTIME_WAKEUP, not RTC_WAKEUP: the interval is "in N minutes from now",
 * so a timezone change must not move it. setAndAllowWhileIdle is the inexact
 * variant - it needs no exact-alarm permission and fires through Doze, at the price
 * of Android batching it. In Doze that means roughly once every 9-15 minutes no
 * matter what interval the backend configured; awake, it lands close to the asked
 * time. A tracking interval below ~10 minutes is therefore a best effort in this
 * mode, which is the trade the showNotification flag is choosing.
 */
object TrackingAlarmManager {

    private const val REQUEST_CODE = 9201

    /** Guards against a runaway chain if the backend ever sends 0. */
    const val MIN_INTERVAL_MINUTES = 1f

    /**
     * Make sure the chain is running, and take one reading now if it was not.
     *
     * The isScheduled() guard is what makes this safe to call on every app resume:
     * without it, opening the app would push the next reading a full interval away
     * every single time.
     */
    fun start(context: Context) {

        val appContext = context.applicationContext

        if (isScheduled(appContext)) {

            Log.d("TEST_TRACKING_ALARM", "Chain already running -> nothing to do")

            return
        }

        Log.d("TEST_TRACKING_ALARM", "🚀 Starting tracking alarm chain")

        // Book the chain first: if the reading below is refused, the feature is still
        // scheduled instead of dead.
        scheduleNext(appContext)

        // Called from a screen, so the app is in the foreground and Android allows
        // the start - but never let a refusal reach the UI.
        runCatching {
            appContext.startTrackingSingleFix()
        }.onFailure {
            Log.e(
                "TEST_TRACKING_ALARM",
                "Could not take the first reading - the alarm will take it",
                it
            )
        }
    }

    /** Tracking is off, or we moved back to the foreground-service mode. */
    fun stop(context: Context) {

        val appContext = context.applicationContext

        val alarmManager =
            appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = createPendingIntent(appContext)

        alarmManager.cancel(pendingIntent)

        // Drop the PendingIntent itself too, otherwise isScheduled() would keep
        // reporting an alarm that no longer exists.
        pendingIntent.cancel()

        Log.d("TEST_TRACKING_ALARM", "🛑 Tracking alarm chain cancelled")
    }

    fun scheduleNext(context: Context) {

        val appContext = context.applicationContext

        val alarmManager =
            appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intervalMinutes =
            SharedPrefManager(appContext)
                .getTrackingIntervalMinutes()
                .coerceAtLeast(MIN_INTERVAL_MINUTES)

        val delayMillis = (intervalMinutes * 60_000L).toLong()

        try {

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + delayMillis,
                createPendingIntent(appContext)
            )

            Log.d(
                "TEST_TRACKING_ALARM",
                "Next reading scheduled in $intervalMinutes minutes"
            )

        } catch (t: Throwable) {

            Log.e("TEST_TRACKING_ALARM", "Could not schedule the next reading", t)

            /*
             * Drop the PendingIntent as well, so isScheduled() reports the chain as
             * dead and the next app resume revives it instead of trusting a
             * PendingIntent that has no alarm behind it.
             */
            stop(appContext)
        }
    }

    /** True when an alarm is already waiting in the system. */
    fun isScheduled(context: Context): Boolean {

        return PendingIntent.getBroadcast(
            context.applicationContext,
            REQUEST_CODE,
            Intent(context.applicationContext, TrackingAlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) != null
    }

    private fun createPendingIntent(context: Context): PendingIntent {

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, TrackingAlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

/**
 * Ask the service for exactly one reading.
 *
 * Deliberately does NOT swallow the failure: from Android 12 on this throws
 * ForegroundServiceStartNotAllowedException in the background, and the alarm
 * receiver has to know so it can take the reading itself rather than lose the round.
 * Callers on a foreground path wrap it in runCatching.
 */
fun Context.startTrackingSingleFix() {

    val intent = Intent(
        this,
        LocationForegroundService::class.java
    ).apply {
        action = LocationForegroundService.ACTION_SINGLE_FIX
    }

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}
