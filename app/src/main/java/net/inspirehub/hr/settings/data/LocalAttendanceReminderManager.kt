package net.inspirehub.hr.settings.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import net.inspirehub.hr.SharedPrefManager

object LocalAttendanceReminderManager {

    private const val REQUEST_CODE = 9001

    const val MIN_INTERVAL_MINUTES = 10L

    fun startCheckIn(context: Context) {

        val appContext = context.applicationContext
        val sharedPrefManager = SharedPrefManager(appContext)

        sharedPrefManager.setCheckInReminderEnabled(true)

        sharedPrefManager.saveLastAttendanceReminderType(null)

        // Start foreground location service
        appContext.startAttendanceLocationService(
            AttendanceLocationForegroundService.ACTION_START
        )
    }

    fun stopCheckIn(context: Context) {

        val appContext = context.applicationContext
        val sharedPrefManager = SharedPrefManager(appContext)

        sharedPrefManager.setCheckInReminderEnabled(false)

        sharedPrefManager.saveLastAttendanceReminderType(null)

        stopIfNoReminderEnabled(appContext)
    }

    fun startCheckOut(context: Context) {

        val appContext = context.applicationContext
        val sharedPrefManager = SharedPrefManager(appContext)

        sharedPrefManager.setCheckOutReminderEnabled(true)

        sharedPrefManager.saveLastAttendanceReminderType(null)

        // Start foreground location service
        appContext.startAttendanceLocationService(
            AttendanceLocationForegroundService.ACTION_START
        )
    }

    fun stopCheckOut(context: Context) {

        val appContext = context.applicationContext
        val sharedPrefManager = SharedPrefManager(appContext)

        sharedPrefManager.setCheckOutReminderEnabled(false)

        sharedPrefManager.saveLastAttendanceReminderType(null)

        stopIfNoReminderEnabled(appContext)
    }

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

        scheduleNextAlarm(
            context = appContext,
            delayMinutes = MIN_INTERVAL_MINUTES
        )
    }

    private fun stopIfNoReminderEnabled(context: Context) {

        val sharedPrefManager = SharedPrefManager(context)

        val checkInEnabled =
            sharedPrefManager.isCheckInReminderEnabled()

        val checkOutEnabled =
            sharedPrefManager.isCheckOutReminderEnabled()

        if (!checkInEnabled && !checkOutEnabled) {
            cancelAlarm(context)
            context.startAttendanceLocationService(
                AttendanceLocationForegroundService.ACTION_STOP
            )
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

        alarmManager.cancel(pendingIntent)

        val safeDelayMinutes =
            delayMinutes.coerceAtLeast(MIN_INTERVAL_MINUTES)

        val delayMillis =
            safeDelayMinutes * 60_000L


        val triggerAtMillis =
            System.currentTimeMillis() + delayMillis

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )

        Log.d(
            "Alarm  AttendanceReminderManager",
            "Next alarm scheduled after $safeDelayMinutes minutes"
        )
    }

    private fun cancelAlarm(context: Context) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val pendingIntent =
            createPendingIntent(context)

        alarmManager.cancel(pendingIntent)

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