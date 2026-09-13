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
import androidx.core.app.ServiceCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.inspirehub.hr.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.delay
import net.inspirehub.hr.SharedPrefManager
import android.content.pm.ServiceInfo
class LocationForegroundService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private var isLocationUpdatesStarted = false
    companion object {
        const val CHANNEL_ID = "location_service_channel"

        /**
         * Second channel, IMPORTANCE_MIN, used when the employee turned the
         * hide-auto-notification switch on while the backend still wants the
         * long-lived service (showNotification = true).
         *
         * Android will not let a foreground service run with no notification at all,
         * so this is as far out of the way as it can be pushed: no status bar icon,
         * no sound, collapsed at the bottom of the shade. A channel's importance
         * cannot be changed after it is created, which is why this is a separate
         * channel rather than an edit of the one above.
         */
        const val CHANNEL_ID_MINIMISED = "location_service_channel_min"

        const val NOTIFICATION_ID = 1001

        /**
         * Take exactly one reading and stop again. Used only in alarm mode
         * (showNotification = false), where TrackingAlarmReceiver owns the schedule.
         */
        const val ACTION_SINGLE_FIX = "net.inspirehub.hr.TRACKING_SINGLE_FIX"
    }


    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

        Log.d("SERVICE_TEST", "onStartCommand called | startId=$startId | intent=$intent")

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

        /*
         * Alarm mode owns the schedule, so the only thing we may do there is one
         * reading. Anything else - most likely Android restarting us from an old
         * sticky start made while the backend still wanted the long-lived service -
         * has to go away again, before it ever puts a notification on screen.
         */
        if (
            !SharedPrefManager(this).getShowNotification() &&
            intent?.action != ACTION_SINGLE_FIX
        ) {

            Log.d(
                "TEST LOCATION_SERVICE",
                "Alarm mode -> the alarm books the readings, stopping"
            )

            stopSelf()

            return START_NOT_STICKY
        }

        goForeground()

        val shouldTrack = LocationFixHandler.shouldTrack(this)

        Log.d(
            "TEST LOCATION_SERVICE",
            "shouldTrack=$shouldTrack | action=${intent?.action}"
        )

        if (!shouldTrack) {

            Log.d(
                "TEST LOCATION_SERVICE",
                "❌ Tracking should not run → stopping service"
            )

            finishSingleFix()

            return START_NOT_STICKY
        }

        Log.d("TEST LOCATION_SERVICE", "✅ Permissions + tracking conditions OK")

        /*
         * Alarm mode: one reading, then take the notification down and die. The alarm
         * is the schedule here, so there is nothing to keep alive and nothing sticky
         * to restart - a sticky restart would only cost a pointless notification.
         */
        if (intent?.action == ACTION_SINGLE_FIX) {

            takeSingleFix()

            return START_NOT_STICKY
        }

        Log.d("SERVICE_TEST", "isLocationUpdatesStarted=$isLocationUpdatesStarted")

        startLocationUpdates()

        return START_STICKY
    }

    private fun goForeground() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(
                NOTIFICATION_ID,
                createNotification()
            )
        }
    }

    /** One reading for one alarm tick, then finish. */
    private fun takeSingleFix() {

        Log.d("TEST_TRACKING_ALARM", "Service up for one reading")

        SingleLocationFix.request(this) { location ->

            if (location == null) {

                finishSingleFix()

                return@request
            }

            CoroutineScope(Dispatchers.IO).launch {

                runCatching {
                    LocationFixHandler.handle(this@LocationForegroundService, location)
                }.onFailure {
                    Log.e("TEST_TRACKING_ALARM", "Reading failed", it)
                }

                finishSingleFix()
            }
        }
    }

    /**
     * Take the notification down and stop. REMOVE, not DETACH: in alarm mode the
     * whole point is that nothing of ours is on screen between two readings.
     *
     * Wake lock first, then stop: releasing after stopSelf() would run against a
     * half torn-down service.
     */
    private fun finishSingleFix() {

        TrackingWakelock.release()

        ServiceCompat.stopForeground(
            this,
            ServiceCompat.STOP_FOREGROUND_REMOVE
        )

        stopSelf()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        Log.d("SERVICE_TEST", "startLocationUpdates called")

        if (isLocationUpdatesStarted) {
            Log.d("SERVICE_TEST", "⚠️ Location updates already started → skipping")
            return
        }

        if (!hasLocationPermission()) {
            Log.e("TEST LOCATION_SERVICE", "❌ Location permission missing" )

            stopSelf()
            return
        }

        val sharedPref = SharedPrefManager(this)
        val intervalMinutes = sharedPref.getTrackingIntervalMinutes()
        val intervalMillis = (intervalMinutes * 60_000L).toLong()
        Log.d("TEST_LOCATION_CONFIG", "Tracking interval = $intervalMinutes minutes ($intervalMillis ms)")

        val request = LocationRequest.Builder( Priority.PRIORITY_HIGH_ACCURACY, intervalMillis )
                .setMinUpdateIntervalMillis(intervalMillis)
                .build()

        locationCallback = object : LocationCallback() {

                override fun onLocationResult( result: LocationResult ) {

                    Log.d("SERVICE_TEST", "Location callback fired")

                    if (!LocationFixHandler.shouldTrack(this@LocationForegroundService)) {

                        Log.d(
                            "TEST LOCATION_SERVICE",
                            "🛑 Tracking should stop -> removing location updates"
                        )

                        fusedLocationClient.removeLocationUpdates(locationCallback)

                        stopSelf()

                        return
                    }

                    val location = result.lastLocation ?: return

                    val time = SimpleDateFormat(
                        "HH:mm:ss",
                        Locale.getDefault()
                    ).format(Date())

                    Log.d("TEST_LOCATION", "Callback location at $time")

                    // Same decision as the alarm path takes, so both modes report
                    // the same readings.
                    CoroutineScope(Dispatchers.IO).launch {

                        runCatching {
                            LocationFixHandler.handle(
                                this@LocationForegroundService,
                                location
                            )
                        }.onFailure {
                            Log.e("TEST LOCATION_SERVICE", "Reading failed", it)
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

        isLocationUpdatesStarted = true

        Log.d("SERVICE_TEST", "✅ Location updates registered")

    }

    override fun onDestroy() {

        Log.d("SERVICE_TEST", "onDestroy")

        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            Log.d("SERVICE_TEST", "Location updates removed")
        }

        isLocationUpdatesStarted = false

        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            Log.e("SERVICE_TEST", "Error unregistering network callback: ${e.message}")
        }

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * The same notification either way, on a different channel.
     *
     * The hide-auto-notification switch in Settings is what picks between them, so
     * one switch now governs both this and the attendance reminder. It cannot make
     * this one disappear - Android owns a foreground service's notification for as
     * long as the service lives - so on this path it minimises it instead: no status
     * bar icon, no alert, collapsed at the bottom of the shade, and dismissible.
     * Employees who want it gone entirely need the backend to send
     * show_notification = false, which switches the whole feature to alarm mode.
     */
    private fun createNotification(): Notification {

        val minimised = SharedPrefManager(this).isHideAutoNotification()

        return NotificationCompat.Builder(
            this,
            if (minimised) CHANNEL_ID_MINIMISED else CHANNEL_ID
        )
            .setContentTitle(getString(R.string.location_service))
            .setContentText(getString(R.string.getting_your_location))
            .setSmallIcon(R.drawable.inspire_hub_logo)
            .setOngoing(!minimised)
            .setSilent(true)
            .setPriority(
                if (minimised) {
                    NotificationCompat.PRIORITY_MIN
                } else {
                    NotificationCompat.PRIORITY_LOW
                }
            )
            .setForegroundServiceBehavior(
                if (minimised) {
                    // Android 12+ holds it back for ~10s. A single alarm-mode fix is
                    // often finished before that, so nothing is ever shown.
                    NotificationCompat.FOREGROUND_SERVICE_DEFERRED
                } else {
                    NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
                }
            )
            .build()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = getSystemService(NotificationManager::class.java) ?: return

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "Location Service",
                NotificationManager.IMPORTANCE_LOW
            )
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID_MINIMISED,
                "Location Service (minimised)",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Used while the notification is set to stay out of the way"
                setShowBadge(false)
            }
        )
    }
}