package net.inspirehub.hr.notifications.presentation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.notifications.components.NoNotifications
import net.inspirehub.hr.notifications.components.NotificationItem
import net.inspirehub.hr.notifications.data.NotificationDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.core.content.edit
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.notifications.components.NotificationPermissionDialog


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val colors = appColors()
    val isLoading = remember { mutableStateOf(true) }
    val showPermissionDialog = remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            val prefs = context.getSharedPreferences("notif_prefs", Context.MODE_PRIVATE)
            prefs.edit { putLong("last_open_time", System.currentTimeMillis()) }
        }
    }





    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                showPermissionDialog.value = true
            }
        }
    }

    if (showPermissionDialog.value) {
        NotificationPermissionDialog(
            onDismiss = { showPermissionDialog.value = false },
            onGoToSettings = {
                val intent =Intent(
                    android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                ).apply {
                    putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
                showPermissionDialog.value = false
            }
        )
    }

    // Using Flow to collect notifications interactively
    val notificationsFlow = remember {
        NotificationDatabase.getDatabase(context).notificationDao().getAllNotifications()
    }
    val notifications by notificationsFlow.collectAsState(initial = emptyList())
    val sortedNotifications = notifications.sortedByDescending { it.timestamp }
    val groupedNotifications = sortedNotifications.groupBy { formatTimestamp(it.timestamp).first }

    // Broadcast Receiver to receive new notifications
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val title = intent?.getStringExtra("title")
                val message = intent?.getStringExtra("message")
                Log.d("NotificationsScreen", "📩 new notification: $title - $message")
                // No need to manually update notifications because Flow will update automatically
            }
        }

        val filter = IntentFilter("net.inspirehub.hr.NEW_NOTIFICATION")
        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.registerReceiver(receiver, filter)

        onDispose {
            lbm.unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(notifications) {
        kotlinx.coroutines.delay(2000)
        // First time data was received from the flow -> Stop loading
        isLoading.value = false
    }




    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = {
            MyAppBar(
                label = stringResource(R.string.notification),
                onBackClick = {
                    navController.navigate("CheckInOutScreen") {
                        popUpTo("CheckInOutScreen") { inclusive = true }
                    }
                }
            )
        },
        bottomBar = { BottomBar(navController = navController) }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.onSecondary)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoading.value -> {
                    FullLoading()
                }

                sortedNotifications.isEmpty() -> {
                    NoNotifications()
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        groupedNotifications.forEach { (date, list) ->
                            item {
                                Text(
                                    text = formatDateHeader(date),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            items(list) { notification ->
                                NotificationItem(notification = notification)
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): Pair<String, String> {
    return try {
        val date = java.util.Date(timestamp)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val dateStr = dateFormat.format(date)
        val timeStr = timeFormat.format(date)

        Pair(dateStr, timeStr)

    } catch (e: Exception) {
        Pair("${e}Unknown date", "")
    }
}

private fun formatDateHeader(dateStr: String): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = sdf.parse(dateStr) ?: return dateStr

    val todayCal = Calendar.getInstance()
    val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    val dateCal = Calendar.getInstance().apply { time = date }

    return when {
        dateCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                dateCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR) -> "Today"

        dateCal.get(Calendar.YEAR) == yesterdayCal.get(Calendar.YEAR) &&
                dateCal.get(Calendar.DAY_OF_YEAR) == yesterdayCal.get(Calendar.DAY_OF_YEAR) -> "Yesterday"

        else -> dateStr
    }
}