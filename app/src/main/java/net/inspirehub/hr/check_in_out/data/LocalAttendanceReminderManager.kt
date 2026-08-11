package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import net.inspirehub.hr.SharedPrefManager

object LocalAttendanceReminderManager {

    fun start(context: Context) {

        val sharedPrefManager = SharedPrefManager(context)

        sharedPrefManager.setAttendanceReminderEnabled(true)

        val intent =
            Intent(
                context,
                LocalAttendanceReminderService::class.java
            )

        ContextCompat.startForegroundService(
            context,
            intent
        )
    }

    fun stop(context: Context) {

        val sharedPrefManager = SharedPrefManager(context)

        sharedPrefManager.setAttendanceReminderEnabled(false)

        val intent =
            Intent(
                context,
                LocalAttendanceReminderService::class.java
            )

        context.stopService(intent)
    }

    fun restore(context: Context) {

        val sharedPrefManager = SharedPrefManager(context)

        if (!sharedPrefManager.isAttendanceReminderEnabled()) {
            return
        }

        val intent = Intent(
            context,
            LocalAttendanceReminderService::class.java
        )

        ContextCompat.startForegroundService(
            context,
            intent
        )
    }
}