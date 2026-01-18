package net.inspirehub.hr.notifications.data


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.inspirehub.hr.MainActivity
import net.inspirehub.hr.R


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {


    private fun sendBroadcast(title: String, message: String) {
        val intent = Intent("net.inspirehub.hr.NEW_NOTIFICATION")
        intent.putExtra("title", title)
        intent.putExtra("message", message)

        val lbm = LocalBroadcastManager.getInstance(applicationContext)
        lbm.sendBroadcast(intent)
    }

    fun saveNotificationToRoom(title: String, message: String) {
        val db = NotificationDatabase.getDatabase(applicationContext)
        val notification = NotificationEntity(
            title = title,
            message = message,
            timestamp = System.currentTimeMillis()
        )

        // Using Coroutines correctly
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.notificationDao().insert(notification)
                Log.d("FCM_DEBUG", "✅ Notification saved: $title")
            } catch (e: Exception) {
                Log.e("FCM_DEBUG", "❌ Failed to save: ${e.message}")
            }
        }
    }



    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        //  Extract data from data payload instead of notification
        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: "New notification"
        val message = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: "You have a new notification"
        Log.e("FCM_CHECK", "notification = ${remoteMessage.notification}")
        Log.e("FCM_CHECK", "data = ${remoteMessage.data}")

        // Save in Room
        saveNotificationToRoom(title, message)

        // View notification
        sendNotification(title, message)

        //  sendBroadcast
        sendBroadcast(title, message)
    }





    private fun sendNotification(title: String?, message: String?) {
        val channelId = "default_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

       // Intent opens the vacation page when clicked
//        val intent = Intent(this, MainActivity::class.java).apply {
////            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            putExtra("navigateTo", "NotificationsScreen")
//        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "NotificationsScreen")
        }


        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)


//        notificationManager.notify(0, notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //Here you can send the token to the server
    }
}