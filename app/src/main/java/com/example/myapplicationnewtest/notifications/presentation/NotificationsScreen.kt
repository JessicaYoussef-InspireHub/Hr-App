package com.example.myapplicationnewtest.notifications.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplicationnewtest.BottomBar
import com.example.myapplicationnewtest.MyAppBar
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.appColors
import com.example.myapplicationnewtest.check_in_out.data.fetchServerTime
import com.example.myapplicationnewtest.notifications.components.NotificationsCards
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch


@OptIn(DelicateCoroutinesApi::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
) {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPrefManager = SharedPrefManager(context)
    val token = sharedPrefManager.getToken()

    val exitTime = sharedPrefManager.getServerExitTime()
    Log.d("StoredTime", "🕒 Stored exit time from Notifications: $exitTime")

//    LaunchedEffect(Unit) {
//        val exitTime = sharedPrefManager.getServerExitTime()
//        Log.d("StoredTime", "🕒 Stored exit time from Notifications: $exitTime")
//    }

    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = {
            MyAppBar(
                label = stringResource(R.string.notification),
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->

        DisposableEffect(Unit) {
            onDispose {
                Log.d("ServerTime", "🧩 onDispose triggered!")

                kotlinx.coroutines.GlobalScope.launch {
                    token?.let {
                        val serverTime = fetchServerTime(it)
                        if (serverTime != null) {
                            Log.d("ServerTime", "🕒 Server time on exit: $serverTime")
                            sharedPrefManager.saveServerExitTime(serverTime)
                            Log.d("ServerTime", "✅ Saved to SharedPrefs: $serverTime")
                        } else {
                            Log.d("ServerTime", "❌ Failed to fetch server time")
                        }
                    } ?: Log.d("ServerTime", "❌ Token is null, cannot fetch server time")
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.onSecondaryColor)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                NotificationsCards(exitTime = exitTime)
            }
        }
    }
}