package net.inspirehub.hr


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.inspirehub.hr.notifications.data.NotificationDatabase
import net.inspirehub.hr.notifications.data.NotificationEntity
import net.inspirehub.hr.scan_qr_code.data.ScanQrCodeViewModel
import net.inspirehub.hr.ui.theme.LocalDarkMode
import net.inspirehub.hr.ui.theme.HrTheme
import java.util.Locale
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

    private lateinit var broadcastReceiver: BroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Hr)

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val sharedPref = SharedPrefManager(this)
        val lang = sharedPref.getLanguage()
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                println("FCM Token: $token")
            }
        }


        setupBroadcastReceiver()

        setContent {



            val context = LocalContext.current
            val sharedPrefManager = SharedPrefManager(context)
            val darkModeState =
                rememberSaveable { mutableStateOf(sharedPrefManager.isDarkModeEnabled()) }

            CompositionLocalProvider(
                LocalDarkMode provides darkModeState
            ) {
                HrTheme {
                    val viewModel: ScanQrCodeViewModel = viewModel()
                    val navController = rememberNavController()

                    val navigateTo = intent.getStringExtra("navigateTo")
                    LaunchedEffect(navigateTo) {
                        if (navigateTo == "NotificationsScreen") {
                            navController.navigate("NotificationsScreen")
                        }
                    }

                    MyAppNavHost(viewModel, navController)

                }
            }
        }
    }

    private fun setupBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val title = intent?.getStringExtra("title") ?: ""
                val message = intent?.getStringExtra("message") ?: ""

                Log.d("FCM_DEBUG", "📱 Broadcast received in MainActivity: $title - $message")

                // تحديث UI فوراً عند استلام إشعار جديد
                updateNotificationsList()
            }
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter("net.inspirehub.hr.NEW_NOTIFICATION"))
    }

    private fun updateNotificationsList() {
        lifecycleScope.launch {
            try {
                loadAllNotifications()
            } catch (e: Exception) {
                Log.e("NOTIFICATIONS", "Error updating notifications: ${e.message}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadAllNotifications()
    }

    private fun loadAllNotifications() {
        lifecycleScope.launch {
            try {
                val notifications = withContext(Dispatchers.IO) {
                    NotificationDatabase.getDatabase(this@MainActivity)
                        .notificationDao()
                        .getAllNotifications()
                }
                // استخدم first() للحصول على القائمة ثم size
                notifications.collect { notificationsList ->
                    Log.d("NOTIFICATIONS", "📋 Loaded ${notificationsList.size} notifications")
                    // هنا يمكنك التعامل مع القائمة
                    // يمكنك إرسالها إلى ViewModel أو تحديث State
                }
            } catch (e: Exception) {
                Log.e("NOTIFICATIONS", "Error loading notifications: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // إلغاء تسجيل الـ Broadcast Receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

}