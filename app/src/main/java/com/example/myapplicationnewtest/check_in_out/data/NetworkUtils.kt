package com.example.myapplicationnewtest.check_in_out.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {

    // This is just a check of the network presence.
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // This actually checks whether the internet is working or not.
    suspend fun hasRealInternet(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val url = URL("https://clients3.google.com/generate_204")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Android")
            connection.setRequestProperty("Connection", "close")
            connection.connectTimeout = 1500
            connection.readTimeout = 1500
            connection.connect()
            connection.responseCode == 204
        } catch (e: Exception) {
            false
        }
    }
}