package net.inspirehub.hr.check_in_out.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.inspirehub.hr.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.BatteryManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.delay
import net.inspirehub.hr.SharedPrefManager
import android.content.pm.ServiceInfo
class LocationForegroundService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationDao: LocationDao
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    companion object {
        const val CHANNEL_ID = "location_service_channel"
        const val NOTIFICATION_ID = 1001
    }

    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null


    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationDao = LocationDatabaseProvider.getDatabase(this).locationDao()

        createNotificationChannel()

        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {

                CoroutineScope(Dispatchers.IO).launch {
                    repeat(5) {
                    if (NetworkUtils.hasRealInternet()) {

                        Log.d("TEST_NETWORK", "Real Internet Restored")

                        sendOfflineLocations(this@LocationForegroundService)
                        return@launch
                    }

                        delay(2000)
                    }
                }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        Log.d("TEST_NETWORK", "Network Callback Registered")
    }

    private fun hasLocationPermission(): Boolean {

        val fineGranted =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        Log.d("SERVICE_TEST", "onStartCommand")

        if (!hasLocationPermission()) {
            Log.e(
                "TEST LOCATION_SERVICE",
                "❌ Location permission missing"
            )
            stopSelf()
            return START_NOT_STICKY
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if (!hasBackgroundLocationPermission(this)) {
                Log.e(
                    "TEST LOCATION_SERVICE",
                    "❌ Background location permission missing"
                )
                stopSelf()
                return START_NOT_STICKY
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(),
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(
                NOTIFICATION_ID,
                createNotification()
            )
        }

        val sharedPref = SharedPrefManager(this)

        val isTracked = sharedPref.getIsTracked()
        val workingHoursOnly = sharedPref.getWorkingHoursOnly()
        val attendanceStatus = sharedPref.getAttendanceStatus()

        val shouldTrack = when {
            !isTracked -> false

            !workingHoursOnly -> true

            else -> attendanceStatus == "checked_in"
        }

        Log.d(
            "TEST LOCATION_SERVICE",
            "isTracked=$isTracked | " +
                    "workingHoursOnly=$workingHoursOnly | " +
                    "attendanceStatus=$attendanceStatus | " +
                    "shouldTrack=$shouldTrack"
        )

        if (!shouldTrack) {

            Log.d(
                "TEST LOCATION_SERVICE",
                "❌ Tracking should not run → stopping service"
            )

            stopSelf()

            return START_NOT_STICKY
        }

        Log.d(
            "TEST LOCATION_SERVICE",
            "✅ Permissions + tracking conditions OK"
        )

        startLocationUpdates()

        return START_STICKY
    }

    private fun getBatteryLevel(): Int {

        val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager

        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY
        )
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        Log.d("SERVICE_TEST", "startLocationUpdates called")
        if (!hasLocationPermission()) {

            Log.e("TEST LOCATION_SERVICE", "❌ Location permission missing" )

            stopSelf()
            return
        }

        val sharedPref = SharedPrefManager(this)
        val intervalMinutes = sharedPref.getTrackingIntervalMinutes()
        val intervalMillis = (intervalMinutes * 60_000L).toLong()
        Log.d("TEST_LOCATION_CONFIG",
            "Tracking interval = $intervalMinutes minutes ($intervalMillis ms)"
        )

        val request = LocationRequest.Builder( Priority.PRIORITY_HIGH_ACCURACY, intervalMillis )
                .setMinUpdateIntervalMillis(intervalMillis)
                .build()

        locationCallback = object : LocationCallback() {

                override fun onLocationResult( result: LocationResult ) {
                    Log.d("SERVICE_TEST", "Location callback fired")
                    val sharedPref = SharedPrefManager(this@LocationForegroundService)

                    val isTracked = sharedPref.getIsTracked()
                    val workingHoursOnly = sharedPref.getWorkingHoursOnly()
                    val attendanceStatus = sharedPref.getAttendanceStatus()

                    val shouldTrack = when {
                        !isTracked -> false

                        !workingHoursOnly -> true

                        else -> attendanceStatus == "checked_in"
                    }

                    Log.d(
                        "TEST LOCATION_SERVICE",
                        "Callback check -> " +
                                "isTracked=$isTracked | " +
                                "workingHoursOnly=$workingHoursOnly | " +
                                "attendanceStatus=$attendanceStatus | " +
                                "shouldTrack=$shouldTrack"
                    )

                    if (!shouldTrack) {

                        Log.d(
                            "TEST LOCATION_SERVICE",
                            "🛑 Tracking should stop -> removing location updates"
                        )

                        fusedLocationClient.removeLocationUpdates(locationCallback)

                        stopSelf()

                        return
                    }
                    val location = result.lastLocation ?: return
                    val lat = location.latitude
                    val lng = location.longitude
                    val accuracy = location.accuracy

                    if (lastLatitude == null || lastLongitude == null) {

                        lastLatitude = lat
                        lastLongitude = lng

                        return
                    }

                    val results = FloatArray(1)

                    Location.distanceBetween(
                        lastLatitude!!,
                        lastLongitude!!,
                        lat,
                        lng,
                        results
                    )

                    val distance = results[0]
                    val time = SimpleDateFormat(
                        "HH:mm:ss",
                        Locale.getDefault()
                    ).format(Date())

                    Log.d("TEST_LOCATION", "New Location -> lat=$lat , lng=$lng , distance=$distance , accuracy=$accuracy , time=$time")

                    val minDistanceMeters = sharedPref.getMinDistanceMeters()
                    Log.d("TEST_LOCATION_CONFIG", "$minDistanceMeters meters")

                    if (distance >= minDistanceMeters) {

                        lastLatitude = lat
                        lastLongitude = lng

                        // Send API
                        CoroutineScope(Dispatchers.IO).launch {
                            Log.d("TEST_NETWORK", "Checking internet...")
                            val hasInternet = NetworkUtils.hasRealInternet()
                            if (hasInternet) {
                                try {
                                    Log.d("TEST_NETWORK", "Internet Available")

                                    sendOfflineLocations(this@LocationForegroundService)
                                    Log.d("TEST_API", "Sending Current Location...")

                                    val sharedPref = SharedPrefManager(this@LocationForegroundService)
                                    val token = sharedPref.getToken()

                                    if (token.isNullOrEmpty()) {
                                        Log.e("TEST_API", "Employee token is null")
                                        return@launch
                                    }

                                    val response = LocationApiService.sendLocation(
                                        context = this@LocationForegroundService,
                                        employeeToken = token,
                                        latitude = lat,
                                        longitude = lng,
                                        accuracy = accuracy,
                                        speed = location.speed,
                                        battery = getBatteryLevel()
                                    )

                                    Log.d("Test LOCATION_API", "Success = ${response.result?.status}")
                                    Log.d("TEST_API", "Current Location Sent Successfully")

                                } catch (e: Exception) {
                                    Log.e("Test LOCATION_API", "Error = ${e.message}")
                                }
                            } else {
                                Log.d("TEST_NETWORK", "No Internet")
                                locationDao.insert(
                                    LocationLogEntity(
                                        latitude = lat,
                                        longitude = lng,
                                        accuracy = accuracy,
                                        speed = location.speed,
                                        battery = getBatteryLevel(),
                                        createdAt = System.currentTimeMillis()
                                    )
                                )
                                Log.d("TEST_ROOM", "Saved Offline")


                                val count = locationDao.getAll().size

                                Log.d("TEST_ROOM", "Offline Count = $count")

                                LocationWorkScheduler.enqueueOfflineLocationSync(
                                    this@LocationForegroundService
                                )
                            }
                        }
                    }
                }
            }
        Log.d("SERVICE_TEST", "Requesting location updates")
        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onDestroy() {
        Log.d("SERVICE_TEST", "onDestroy")

        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            Log.e("SERVICE_TEST", "Error unregistering network callback: ${e.message}")
        }

        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            Log.d("SERVICE_TEST", "Location updates removed")
        }

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle(getString(R.string.location_service))
            .setContentText(getString(R.string.getting_your_location))
            .setSmallIcon(R.drawable.inspire_hub_logo)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Location Service",
                    NotificationManager.IMPORTANCE_LOW
                )

            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }
}