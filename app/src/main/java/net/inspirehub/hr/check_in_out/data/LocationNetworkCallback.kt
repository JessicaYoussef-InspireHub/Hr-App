package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.util.Log
import net.inspirehub.hr.SharedPrefManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val formatter = SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss",
    Locale.US
)

suspend fun sendOfflineLocations(
    context: Context
) {

    val sharedPref = SharedPrefManager(context)
    val token = sharedPref.getToken()

    if (!sharedPref.getIsTracked()) {
        Log.d("TEST_SYNC", "Tracking disabled")
        LocationDatabaseProvider
            .getDatabase(context)
            .locationDao()
            .deleteAll()

        return
    }

    if (!NetworkUtils.hasRealInternet()) {
        Log.d("TEST_SYNC", "No real internet")
        return
    }

    val dao = LocationDatabaseProvider
            .getDatabase(context)
            .locationDao()

    val locations = dao.getAll()

    if (locations.isEmpty()) {
        Log.d("TEST_SYNC", "No offline locations")
        return
    }


    if (token.isNullOrEmpty()) {
        Log.e("TEST_SYNC", "Employee token is null")
        return
    }

    val formatter = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss",
        Locale.US
    )

    val pings = locations.map {
        PingItem(
            latitude = it.latitude,
            longitude = it.longitude,
            timestamp = formatter.format(Date(it.createdAt)),
            accuracy = it.accuracy,
            speed = it.speed,
            battery = it.battery
        )
    }

    val response = LocationApiService.sendBatchLocations(
        context = context,
        employeeToken = token,
        pings = pings
        )

    if (response.result?.status == "success") {

        dao.deleteAll()
        Log.d("TEST_SYNC", "Batch sent successfully")

    } else {

        Log.e("TEST_SYNC", "Batch failed" )
    }
}