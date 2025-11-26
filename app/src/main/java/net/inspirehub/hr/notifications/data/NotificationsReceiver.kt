package net.inspirehub.hr.notifications.data


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase



class NotificationsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: return
        val message = intent.getStringExtra("message") ?: return

        Log.d("FCM_DEBUG", "📣 Broadcast received: $title - $message")

        // هنا ممكن تحفظيها في SharedPreferences أو أي قاعدة بيانات محلية مثل Room
    }
}


