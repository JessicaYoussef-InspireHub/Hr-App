package net.inspirehub.hr.settings.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * A reboot wipes every pending alarm, and installing a new build kills whatever was
 * running. This puts the machine back together in both cases - otherwise an employee
 * who restarts the phone at lunch would silently stop being reminded.
 *
 * Re-arming the alarm is the whole repair: nothing else of ours is supposed to be
 * running between two readings.
 */
class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {

        val action = intent?.action ?: return

        if (
            action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            return
        }

        Log.d("Alarm", "$action received -> restoring attendance reminder alarm")

        LocalAttendanceReminderManager.restore(
            context
        )
    }
}
