package net.inspirehub.hr.settings.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.RequiresPermission
import android.util.Log
object AttendanceLocationFix {

    private const val FIX_TIMEOUT_MS = 60_000L
    private const val GOOD_ENOUGH_ACCURACY_METERS = 25f

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun request(
        context: Context,
        onResult: (Location?) -> Unit
    ) {

        val appContext = context.applicationContext

        if (
            appContext.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onResult(null)
            return
        }

        val locationManager = appContext.getSystemService(
                LocationManager::class.java
            )

        if (locationManager == null) {
            onResult(null)
            return
        }

        if (
            !locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
        ) {
            Log.d("Alarm", "GPS provider is disabled")
            onResult(null)
            return
        }

        val handler = Handler(Looper.getMainLooper())
        val startedAt = SystemClock.elapsedRealtime()

        var finished = false
        var bestLocation: Location? = null

        lateinit var locationListener: LocationListener
        lateinit var gnssCallback: GnssStatus.Callback
        lateinit var timeoutRunnable: Runnable

        fun cleanup() {
            runCatching { locationManager.removeUpdates(locationListener) }

            runCatching { locationManager.unregisterGnssStatusCallback(gnssCallback) }

            handler.removeCallbacks(timeoutRunnable)
        }

        fun finish(location: Location?) {

            if (finished) return

            finished = true

            cleanup()

            onResult(location)
        }

        locationListener = object : LocationListener {

            override fun onLocationChanged(
                location: Location
            ) {

                val elapsed = SystemClock.elapsedRealtime() - startedAt

                Log.d(
                    "Alarm",
                    "GPS update -> " +
                            "lat=${location.latitude}, " +
                            "lng=${location.longitude}, " +
                            "accuracy=${location.accuracy}, " +
                            "after=${elapsed}ms"
                )

                if (
                    bestLocation == null ||
                    !bestLocation!!.hasAccuracy() ||
                    (
                            location.hasAccuracy() && location.accuracy < bestLocation!!.accuracy
                            )
                ) {
                    bestLocation = location
                }

                if (
                    location.hasAccuracy() &&
                    location.accuracy <=
                    GOOD_ENOUGH_ACCURACY_METERS
                ) {

                    finish(location)
                }
            }

            override fun onProviderDisabled(
                provider: String
            ) {
                finish(null)
            }

            override fun onProviderEnabled(
                provider: String
            ) = Unit

            @Deprecated("Required by LocationListener")
            override fun onStatusChanged(
                provider: String?,
                status: Int,
                extras: Bundle?
            ) = Unit
        }

        gnssCallback =
            object : GnssStatus.Callback() {

                override fun onSatelliteStatusChanged(
                    status: GnssStatus
                ) {

                    var used = 0

                    for (
                    i in 0 until status.satelliteCount
                    ) {

                        if (status.usedInFix(i)) {
                            used++
                        }
                    }

                    Log.d(
                        "Alarm", "GNSS satellites -> " + "visible=${status.satelliteCount}, " + "used=$used"
                    )
                }
            }

        timeoutRunnable = Runnable {

            val elapsed = SystemClock.elapsedRealtime() - startedAt

            Log.d("Alarm", "GPS timeout after ${elapsed}ms")

            finish(bestLocation)
        }

        try {

            @Suppress("DEPRECATION")
            locationManager.registerGnssStatusCallback(
                gnssCallback,
                handler
            )

            @Suppress("MissingPermission")
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener,
                Looper.getMainLooper()
            )

            handler.postDelayed(
                timeoutRunnable,
                FIX_TIMEOUT_MS
            )

            Log.d("Alarm", "GPS fix started")

        } catch (e: Exception) {

            Log.d("Alarm", "Failed to start GPS", e)

            cleanup()

            onResult(null)
        }
    }
}