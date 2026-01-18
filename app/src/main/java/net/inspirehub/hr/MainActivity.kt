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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
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
import kotlinx.coroutines.CoroutineScope
import net.inspirehub.hr.scan_qr_code.data.AppConfig


class MainActivity : AppCompatActivity() {




    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var navController: NavController
    private var notificationIntent: Intent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Hr)

        super.onCreate(savedInstanceState)

        notificationIntent = intent

        WindowCompat.setDecorFitsSystemWindows(window, true)
        val sharedPref = SharedPrefManager(this)
        val lang = sharedPref.getLanguage()
        val locale = Locale(lang)
        Locale.setDefault(locale)
        AppConfig.init(this)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)


        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d("FCM_TOKEN", "🔥 FCM Token = $token")
            }
            .addOnFailureListener { e ->
                Log.e("FCM_TOKEN", "❌ Failed to get token", e)
            }


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                println("FCM Token: $token")
            }
        }
        setupBroadcastReceiver()




        intent.extras?.let { extras ->
            val title = extras.getString("title")
            val message = extras.getString("message")
            if (title != null && message != null) {
                // هنا نحتاج نضيف رسالة للـ Room
                saveNotificationToRoom(title, message)
            }
        }


        handleNotificationIntent(intent)

        setContent {

            val navController = rememberNavController()
            val intentState = rememberSaveable { mutableStateOf(notificationIntent) }

            val context = LocalContext.current
            val sharedPrefManager = SharedPrefManager(context)
            val darkModeState =
                rememberSaveable { mutableStateOf(sharedPrefManager.isDarkModeEnabled()) }

//            val localNavController = rememberNavController()
//            navController = localNavController

//            LaunchedEffect(intentState.value) {
//                val navigateTo = intentState.value?.getStringExtra("navigateTo")
//                if (navigateTo == "NotificationsScreen") {
//                    navController.navigate("NotificationsScreen") {
//                        launchSingleTop = true
//                    }
//                    // امسح intent بعد المعالجة
//                    intentState.value = null
//                }
//            }

            CompositionLocalProvider(
                LocalDarkMode provides darkModeState
            ) {
                HrTheme {
                    val viewModel: ScanQrCodeViewModel = viewModel()
//                    val navController = rememberNavController()



//
//                    val navigateToNotifications =
//                        rememberSaveable { mutableStateOf(false) }
//                    val navigateTo = intent.getStringExtra("navigateTo")
////                    LaunchedEffect(navigateTo) {
////                        if (navigateTo == "NotificationsScreen") {
////                            navController.navigate("NotificationsScreen")
////                        }
////                    }
//
//
//
//                    LaunchedEffect(Unit) {
//                        if (intent.getStringExtra("navigateTo") == "NotificationsScreen") {
//                            navigateToNotifications.value = true
//                        }
//                    }
//
//                    LaunchedEffect(navigateToNotifications.value) {
//                        if (navigateToNotifications.value) {
//                            navController.navigate("NotificationsScreen") {
//                                launchSingleTop = true
//                            }
//                            navigateToNotifications.value = false
//                        }
//                    }

                    val openedFromNotification =
                        intent.getStringExtra("navigateTo") == "NotificationsScreen"

                    MyAppNavHost(
                        viewModel = viewModel(),
                        navController = navController,
                        openedFromNotification = openedFromNotification)



                }

//                handleIntent(intent)

            }
        }
    }




//    private fun handleIntent(intent: Intent?) {
//        val navigateTo = intent?.getStringExtra("navigateTo")
//        if (navigateTo == "NotificationsScreen" && ::navController.isInitialized) {
//            navController.navigate("NotificationsScreen") {
//                launchSingleTop = true
//            }
//        }
//    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationIntent = intent
    handleNotificationIntent(intent)

}

    private fun handleNotificationIntent(intent: Intent?) {
        intent?.extras?.let { extras ->
            val title = extras.getString("title")
            val message = extras.getString("body")
            if (title != null && message != null) {
                saveNotificationToRoom(title, message)
            }
        }
    }

    private fun saveNotificationToRoom(title: String, message: String) {
        val db = NotificationDatabase.getDatabase(this)
        val notification = NotificationEntity(
            title = title,
            message = message,
            timestamp = System.currentTimeMillis()
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.notificationDao().insert(notification)
        }
    }


//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        setIntent(intent)
//    }



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



class NavViewModel : ViewModel() {
    var navController: NavController? = null
}