package net.inspirehub.hr.notifications.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log



class NotificationsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: return
        val message = intent.getStringExtra("message") ?: return

        Log.d("FCM_DEBUG", "📣 Broadcast received: $title - $message")

        // Here you can save it in SharedPreferences or any local database such as Room
    }
}


