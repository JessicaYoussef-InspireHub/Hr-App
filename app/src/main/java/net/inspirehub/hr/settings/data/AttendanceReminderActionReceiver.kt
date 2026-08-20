package net.inspirehub.hr.settings.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AttendanceReminderActionReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {

        val appContext = context.applicationContext

        val reminderType =
            intent?.getStringExtra(
                "REMINDER_TYPE"
            )

        Log.d("Alarm AttendanceReminderAction", "User clicked reminder: $reminderType")

        when (reminderType) {

            "CHECK_IN_REMINDER" -> {

                /*
                 * هنا لازم ننفذ نفس عملية Check-In
                 * المستخدمة في الشاشة الرئيسية.
                 */
            }

            "CHECK_OUT_REMINDER" -> {

                /*
                 * هنا لازم ننفذ نفس عملية Check-Out
                 * المستخدمة في الشاشة الرئيسية.
                 */
            }
        }
    }
}