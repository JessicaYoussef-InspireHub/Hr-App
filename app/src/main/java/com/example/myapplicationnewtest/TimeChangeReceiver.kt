package com.example.myapplicationnewtest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket


class TimeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TimeChangeReceiver", "⏰ Broadcast captured for time change")
        if (context == null) return

        CoroutineScope(Dispatchers.IO).launch {
            val isOnlineNow = checkInternetConnection(context)
            val sharedPref = SharedPrefManager(context)
            sharedPref.saveWasOfflineDuringTimeChange(!isOnlineNow)

            val message = when (intent?.action) {
                Intent.ACTION_TIME_CHANGED -> "🕒 The clock was manually changed"
                Intent.ACTION_DATE_CHANGED -> "📅 The date was changed"
                Intent.ACTION_TIMEZONE_CHANGED -> "🌍 The time zone was changed"
                Intent.ACTION_BOOT_COMPLETED -> "🔄 The device was rebooted"
                else -> "A time change occurred"
            } + if (isOnlineNow) " | 🟢 Online" else " | 🔴 Offline"

            Log.i("BroadCast", message)
        }
    }

    private fun checkInternetConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        val hasNetwork = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        if (!hasNetwork) return false

        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}

