package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.location.Location
import android.os.BatteryManager
import android.util.Log
import net.inspirehub.hr.SharedPrefManager

/**
 * What to do with one position, wherever it came from.
 *
 * There are two ways a reading reaches us and they must behave identically:
 *
 *   - SharedPrefManager.getShowNotification() == true: the LocationCallback inside
 *     the long-lived LocationForegroundService,
 *   - == false: a single fix taken for one alarm tick, either by the same service
 *     started for that one reading or, when Android refuses to start it from the
 *     background, by TrackingAlarmReceiver itself.
 *
 * Keeping the decision here is what makes those paths interchangeable. The "did we
 * move far enough" baseline lives in prefs rather than in a field, because on the
 * alarm path the process is gone between two readings.
 */
object LocationFixHandler {

    /**
     * @return true when the reading was far enough from the last reported one to be
     * sent (or queued offline).
     */
    suspend fun handle(
        context: Context,
        location: Location
    ): Boolean {

        val appContext = context.applicationContext

        val sharedPref = SharedPrefManager(appContext)

        val lat = location.latitude
        val lng = location.longitude
        val accuracy = location.accuracy

        val lastLatitude = sharedPref.getLastTrackedLatitude()
        val lastLongitude = sharedPref.getLastTrackedLongitude()

        if (lastLatitude == null || lastLongitude == null) {

            // Nothing to compare against yet: this one only sets the baseline.
            sharedPref.saveLastTrackedLocation(lat, lng)

            Log.d(
                "TEST_LOCATION",
                "First reading -> baseline saved (lat=$lat , lng=$lng)"
            )

            return false
        }

        val results = FloatArray(1)

        Location.distanceBetween(
            lastLatitude,
            lastLongitude,
            lat,
            lng,
            results
        )

        val distance = results[0]

        val minDistanceMeters = sharedPref.getMinDistanceMeters()

        Log.d(
            "TEST_LOCATION",
            "New Location -> lat=$lat , lng=$lng , distance=$distance , " +
                    "accuracy=$accuracy , minDistance=$minDistanceMeters"
        )

        if (distance < minDistanceMeters) {
            return false
        }

        sharedPref.saveLastTrackedLocation(lat, lng)

        send(
            context = appContext,
            location = location
        )

        return true
    }

    private suspend fun send(
        context: Context,
        location: Location
    ) {

        val sharedPref = SharedPrefManager(context)

        Log.d("TEST_NETWORK", "Checking internet...")

        if (NetworkUtils.hasRealInternet()) {

            try {

                Log.d("TEST_NETWORK", "Internet Available")

                sendOfflineLocations(context)

                Log.d("TEST_API", "Sending Current Location...")

                val token = sharedPref.getToken()

                if (token.isNullOrEmpty()) {
                    Log.e("TEST_API", "Employee token is null")
                    return
                }

                val response = LocationApiService.sendLocation(
                    context = context,
                    employeeToken = token,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy,
                    speed = location.speed,
                    battery = batteryLevel(context)
                )

                Log.d("Test LOCATION_API", "Success = ${response.result?.status}")
                Log.d("TEST_API", "Current Location Sent Successfully")

            } catch (e: Exception) {

                Log.e("Test LOCATION_API", "Error = ${e.message}")
            }

            return
        }

        Log.d("TEST_NETWORK", "No Internet")

        val locationDao = LocationDatabaseProvider
            .getDatabase(context)
            .locationDao()

        locationDao.insert(
            LocationLogEntity(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                speed = location.speed,
                battery = batteryLevel(context),
                createdAt = System.currentTimeMillis()
            )
        )

        Log.d("TEST_ROOM", "Saved Offline")

        Log.d("TEST_ROOM", "Offline Count = ${locationDao.getAll().size}")

        LocationWorkScheduler.enqueueOfflineLocationSync(context)
    }

    private fun batteryLevel(context: Context): Int {

        val batteryManager =
            context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        return batteryManager.getIntProperty(
            BatteryManager.BATTERY_PROPERTY_CAPACITY
        )
    }

    /**
     * Is tracking supposed to be running right now? Every entry point asks this, so
     * it lives in one place.
     */
    fun shouldTrack(
        context: Context,
        attendanceStatus: String? = null
    ): Boolean {

        val sharedPref = SharedPrefManager(context.applicationContext)

        val isTracked = sharedPref.getIsTracked()
        val workingHoursOnly = sharedPref.getWorkingHoursOnly()

        val status = attendanceStatus ?: sharedPref.getAttendanceStatus()

        return when {

            !isTracked -> false

            !workingHoursOnly -> true

            else -> status == "checked_in"
        }
    }
}
