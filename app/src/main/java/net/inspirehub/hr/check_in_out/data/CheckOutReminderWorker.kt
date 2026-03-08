package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.*
import net.inspirehub.hr.R
import java.util.concurrent.TimeUnit
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class CheckOutReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        Log.d("CheckOutScheduler", "🔥 Worker is running even if app closed")
        Log.d("CheckOutScheduler", "✅ Worker started")
        sendCheckOutNotification()
        Log.d("CheckOutScheduler", "✅ Notification sent")
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendCheckOutNotification() {
        val sharedPref = net.inspirehub.hr.SharedPrefManager(applicationContext)
        val lang = sharedPref.getLanguage()

        val notificationText = if (lang.startsWith("ar")) {
            "تذكير بعمل تسجيل الخروج"
        } else {
            "Check-Out Reminder"
        }

        val notificationTitle = if (lang.startsWith("ar")) {
            "تذكير"
        } else {
            "Reminder"
        }

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager


        val notification = android.app.Notification.Builder(applicationContext, "check_out_channel")
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.alarm)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
        Log.d("CheckOutScheduler", "📣 Notification triggered")

    }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val sharedPref = net.inspirehub.hr.SharedPrefManager(context)
        val lang = sharedPref.getLanguage()

        val descriptionText = if (lang.startsWith("ar")) {
            "تذكير بعمل تسجيل الخروج"
        } else {
            "Check-Out Reminder"
        }
        val name = "Reminder"
        val importance = android.app.NotificationManager.IMPORTANCE_HIGH
        val channel = android.app.NotificationChannel("check_out_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)
        Log.d("CheckOutScheduler", "✅ Notification channel created")

    }
}


fun scheduleCheckOutReminder(
    context: Context,
    scheduledHours: Double
) {

    Log.d("CheckOutScheduler", "⏰ Scheduling Check-Out reminder")

    createNotificationChannel(context)
    val delayMinutes = (scheduledHours * 60).toLong()

    val workRequest =
        OneTimeWorkRequestBuilder<CheckOutReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .addTag("check_out_reminder")
            .build()

    WorkManager
        .getInstance(context)
        .enqueueUniqueWork(
            "check_out_reminder_work",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

    Log.d("CheckOutScheduler", "✅ WorkManager enqueued")
}
