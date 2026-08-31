package net.inspirehub.hr.settings.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import net.inspirehub.hr.SharedPrefManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * The clock of the whole feature, and the one door in and out of it.
 *
 * The alarm IS the feature; the foreground service it wakes is only the alarm's
 * hands. That is why every entry point here makes sure an alarm is booked, and why
 * nothing here assumes the service is still alive - between two readings nothing of
 * ours is supposed to be running.
 *
 * ELAPSED_REALTIME_WAKEUP rather than RTC_WAKEUP on purpose: the interval is "in N
 * minutes from now", not "at this wall-clock time", so a timezone change or a
 * corrected clock must not move it. setAndAllowWhileIdle is the inexact variant -
 * it needs no exact-alarm permission and Android may shift it slightly to batch
 * wake-ups, which is fine for a "roughly every N minutes" check.
 */
object LocalAttendanceReminderManager {

    private const val REQUEST_CODE = 9001
    const val MIN_INTERVAL_MINUTES = 1L

    private val CLOCK_FMT = SimpleDateFormat("HH:mm:ss", Locale.US)

    fun startCheckIn(context: Context) {

        val appContext = context.applicationContext
        val sharedPrefManager = SharedPrefManager(appContext)

        sharedPrefManager.setCheckInReminderEnabled(true)

        sharedPrefManager.saveLastAttendanceReminderType(null)

        startReminders(appContext)
    }

    fun stopCheckIn(context: Context) {

        val appContext = context.applicationContext
        val sharedPrefManager = SharedPrefManager(appContext)

        sharedPrefManager.setCheckInReminderEnabled(false)

        sharedPrefManager.saveLastAttendanceReminderType(null)

        // The reminder that is on screen belongs to a rule that no longer applies.
        LocalAttendanceReminderReceiver.cancelReminderNotification(appContext)

        stopIfNoReminderEnabled(appContext)
    }

    fun startCheckOut(context: Context) {

        val appContext = context.applicationContext
        val sharedPrefManager = SharedPrefManager(appContext)

        sharedPrefManager.setCheckOutReminderEnabled(true)

        sharedPrefManager.saveLastAttendanceReminderType(null)

        startReminders(appContext)
    }

    fun stopCheckOut(context: Context) {

        val appContext = context.applicationContext
        val sharedPrefManager = SharedPrefManager(appContext)

        sharedPrefManager.setCheckOutReminderEnabled(false)

        sharedPrefManager.saveLastAttendanceReminderType(null)

        // The reminder that is on screen belongs to a rule that no longer applies.
        LocalAttendanceReminderReceiver.cancelReminderNotification(appContext)

        stopIfNoReminderEnabled(appContext)
    }

    /**
     * Repair function. Called every time the app is opened and after a reboot.
     *
     * A missing alarm is the only real failure: between two readings nothing of ours
     * runs, so a dead service means nothing. The isScheduled() guard is what makes
     * this safe to call on every app launch - without it, opening the app would push
     * the next check another full interval into the future every single time.
     */
    fun restore(context: Context) {

        val appContext = context.applicationContext
        val sharedPrefManager = SharedPrefManager(appContext)

        val checkInEnabled =
            sharedPrefManager.isCheckInReminderEnabled()

        val checkOutEnabled =
            sharedPrefManager.isCheckOutReminderEnabled()

        if (!checkInEnabled && !checkOutEnabled) {
            return
        }

        if (!isScheduled(appContext)) {

            Log.w(
                "Alarm AttendanceReminderManager",
                "Alarm was missing -> rescheduling"
            )

            scheduleNextAlarm(
                context = appContext,
                delayMinutes = MIN_INTERVAL_MINUTES
            )
        }

        /*
         * A reboot clears notifications, and in persistent mode ours can also be
         * swiped away, so put it back. In the default (hidden) mode this does
         * nothing at all.
         */
        AttendanceLocationNotification.onRemindersEnabled(appContext)
    }

    /**
     * Book the chain, then try for one reading straight away so the employee gets
     * immediate feedback. The alarm comes first on purpose: if the service start is
     * refused, the feature is still scheduled instead of dead.
     */
    private fun startReminders(context: Context) {

        scheduleNextAlarm(
            context = context,
            delayMinutes = MIN_INTERVAL_MINUTES
        )

        AttendanceLocationNotification.onRemindersEnabled(context)

        // Called from the settings screen, so the app is in the foreground and
        // Android allows the start - but never let a refusal reach the UI.
        runCatching {
            context.startAttendanceLocationService(
                AttendanceLocationForegroundService.ACTION_START
            )
        }.onFailure {
            Log.e(
                "Alarm AttendanceReminderManager",
                "Could not start the first reading - the alarm will take it",
                it
            )
        }
    }

    private fun stopIfNoReminderEnabled(context: Context) {

        val sharedPrefManager = SharedPrefManager(context)

        val checkInEnabled =
            sharedPrefManager.isCheckInReminderEnabled()

        val checkOutEnabled =
            sharedPrefManager.isCheckOutReminderEnabled()

        if (!checkInEnabled && !checkOutEnabled) {

            cancelAlarm(context)

            /*
             * Only worth an Intent when something is actually running. The service
             * stops itself after every reading, so most of the time there is nothing
             * to stop, and asking Android to start a foreground service just to shut
             * it down is exactly the call that gets refused in the background.
             */
            if (AttendanceLocationForegroundService.isTakingFix) {

                runCatching {
                    context.startAttendanceLocationService(
                        AttendanceLocationForegroundService.ACTION_STOP
                    )
                }
            }

            // Take the notification down here as well, so it goes away even if the
            // Intent above never arrives.
            AttendanceLocationNotification.onRemindersDisabled(context)
        }
    }

    fun checkNow(context: Context) {
        val appContext = context.applicationContext

        val intent = Intent(
            appContext,
            LocalAttendanceReminderReceiver::class.java
        ).apply {
            action = "ACTION_CHECK_NOW"
        }

        appContext.sendBroadcast(intent)

        Log.d(
            "Alarm AttendanceReminderManager",
            "Immediate attendance reminder check triggered"
        )
    }

    fun scheduleNextAlarm(
        context: Context,
        delayMinutes: Long
    ) {

        val appContext = context.applicationContext

        val alarmManager =
            appContext.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val pendingIntent =
            createPendingIntent(appContext)

        val safeDelayMinutes =
            delayMinutes.coerceAtLeast(MIN_INTERVAL_MINUTES)

        val delayMillis =
            safeDelayMinutes * 60_000L

        try {

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + delayMillis,
                pendingIntent
            )

            Log.d(
                "Alarm  AttendanceReminderManager",
                "Next alarm scheduled after $safeDelayMinutes minutes " +
                        "(around ${CLOCK_FMT.format(Date(System.currentTimeMillis() + delayMillis))})"
            )

        } catch (t: Throwable) {

            Log.e(
                "Alarm  AttendanceReminderManager",
                "Could not schedule the next check",
                t
            )
        }
    }

    /** True when an alarm is already waiting in the system. */
    private fun isScheduled(context: Context): Boolean {

        val intent = Intent(
            context,
            LocalAttendanceReminderReceiver::class.java
        )

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or
                    PendingIntent.FLAG_IMMUTABLE
        ) != null
    }

    private fun cancelAlarm(context: Context) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val pendingIntent =
            createPendingIntent(context)

        alarmManager.cancel(pendingIntent)

        // Drop the PendingIntent itself too, otherwise isScheduled() would keep
        // reporting an alarm that no longer exists.
        pendingIntent.cancel()

        Log.d(
            "AttendanceReminderManager",
            "Alarm cancelled"
        )
    }

    private fun createPendingIntent(
        context: Context
    ): PendingIntent {

        val intent = Intent(
            context,
            LocalAttendanceReminderReceiver::class.java
        )

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }
}
