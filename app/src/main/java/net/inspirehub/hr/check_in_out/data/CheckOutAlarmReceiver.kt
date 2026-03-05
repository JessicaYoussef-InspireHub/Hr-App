package net.inspirehub.hr.check_in_out.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.widget.Toast
import java.util.concurrent.TimeUnit


class CheckOutAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("CheckOutAlarm", "📣 Alarm received!")

        val sharedPref = SharedPrefManager(context)
        val lang = sharedPref.getLanguage()


        val notificationText = if (lang.startsWith("ar")) "تذكير بعمل تسجيل الخروج" else "Check-Out Reminder"
        val notificationTitle = if (lang.startsWith("ar")) "تذكير" else "Reminder"

        val channelId = "check_out_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = notificationText
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.alarm)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}






fun scheduleCheckOutAlarm(context: Context, scheduledHours: Double) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Log.w("CheckOutAlarm", "⚠️ Exact alarms not allowed!")

            AlertDialog.Builder(context)
                .setTitle("Permission needed")
                .setMessage("Please enable exact alarms in settings to get reminders on time.")
                .setPositiveButton("Open Settings") { _, _ ->
                    val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(
                        context,
                        "⚠️ Reminder may not work on time without exact alarm permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .show()

            return
        }
    }

    val intent = Intent(context, CheckOutAlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        1001,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerAtMillis = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(scheduledHours.toLong())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    } else {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    Log.d("CheckOutAlarm", "⏰ Alarm scheduled in $scheduledHours hours")

    saveCheckOutScheduledTime(context, triggerAtMillis)

}

fun rescheduleCheckOutAlarms(context: Context) {
    val sharedPref = SharedPrefManager(context)
    val lastScheduledTime = sharedPref.getCheckOutScheduledTime() // مفروض تحفظ الوقت اللي المفروض المنبه يرن فيه

    if (lastScheduledTime != null) {
        val currentTime = System.currentTimeMillis()
        val remainingMillis = lastScheduledTime - currentTime

        if (remainingMillis > 0) {
            val scheduledHours = remainingMillis.toDouble() / (1000 * 60 * 60)
            Log.d("CheckOutAlarm", "🔄 Rescheduling alarm in $scheduledHours hours")
            scheduleCheckOutAlarm(context, scheduledHours)
        } else {
            Log.d("CheckOutAlarm", "⏰ Scheduled time already passed, skipping")
        }
    } else {
        Log.d("CheckOutAlarm", "No previously scheduled alarm found")
    }
}

fun saveCheckOutScheduledTime(context: Context, triggerTimeMillis: Long) {
    val sharedPref = SharedPrefManager(context)
    sharedPref.saveCheckOutScheduledTime(triggerTimeMillis)
}

