package net.inspirehub.hr.check_in_out.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import net.inspirehub.hr.SharedPrefManager


object LocalAttendanceReminderManager {

    private const val REQUEST_CODE = 9001

    // Minimum interval = 10 minutes
    const val MIN_INTERVAL_MINUTES = 10L

    fun start(context: Context) {

        val appContext = context.applicationContext

        val sharedPrefManager = SharedPrefManager(appContext)

        sharedPrefManager.setAttendanceReminderEnabled(true)

        // Schedule first check after 3 minutes
        scheduleNextAlarm(
            context = appContext,
            delayMinutes = MIN_INTERVAL_MINUTES
        )
    }

    fun stop(context: Context) {

        val appContext = context.applicationContext

        val sharedPrefManager = SharedPrefManager(appContext)

        sharedPrefManager.setAttendanceReminderEnabled(false)

        cancelAlarm(appContext)
    }

    fun restore(context: Context) {

        val appContext = context.applicationContext

        val sharedPrefManager = SharedPrefManager(appContext)

        if (!sharedPrefManager.isAttendanceReminderEnabled()) {
            return
        }

        // After device restart, start checking again
        scheduleNextAlarm(
            context = appContext,
            delayMinutes = MIN_INTERVAL_MINUTES
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

        val pendingIntent = createPendingIntent(appContext)

        // Cancel previous alarm first
        alarmManager.cancel(pendingIntent)

        val safeDelayMinutes = delayMinutes.coerceAtLeast(MIN_INTERVAL_MINUTES)

        val delayMillis = safeDelayMinutes * 60_000L

        val triggerAtMillis = System.currentTimeMillis() + delayMillis

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    private fun cancelAlarm(
        context: Context
    ) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val pendingIntent = createPendingIntent(context)

        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(
        context: Context
    ): PendingIntent {

        val intent =
            Intent(
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