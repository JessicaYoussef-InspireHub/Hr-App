package net.inspirehub.hr.check_in_out.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

/**
 * One position, once, then done.
 *
 * The continuous mode never needs this - it keeps a LocationCallback registered and
 * the readings arrive on their own. Alarm mode has no such callback: the process is
 * gone between two ticks, so every tick has to go and fetch a position itself.
 *
 * getCurrentLocation() rather than requestLocationUpdates() because it is exactly
 * this: ask the fused provider for a fresh fix and hand it back once. The timeout is
 * ours, not the provider's - a tick that never returns would hold the wake lock and
 * keep a foreground service alive for nothing.
 */
object SingleLocationFix {

    private const val FIX_TIMEOUT_MS = 45_000L

    @SuppressLint("MissingPermission")
    fun request(
        context: Context,
        onResult: (Location?) -> Unit
    ) {

        val appContext = context.applicationContext

        val fineGranted =
            ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {

            Log.e("TEST_TRACKING_FIX", "❌ Location permission missing")

            onResult(null)

            return
        }

        val cancellation = CancellationTokenSource()

        val handler = Handler(Looper.getMainLooper())

        var finished = false

        fun finish(location: Location?) {

            if (finished) return

            finished = true

            handler.removeCallbacksAndMessages(null)

            runCatching { cancellation.cancel() }

            onResult(location)
        }

        handler.postDelayed(
            {
                Log.d("TEST_TRACKING_FIX", "⌛ Fix timed out after ${FIX_TIMEOUT_MS}ms")
                finish(null)
            },
            FIX_TIMEOUT_MS
        )

        try {

            LocationServices
                .getFusedLocationProviderClient(appContext)
                .getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellation.token
                )
                .addOnSuccessListener { location ->

                    Log.d(
                        "TEST_TRACKING_FIX",
                        "Fix -> lat=${location?.latitude} , lng=${location?.longitude} , " +
                                "accuracy=${location?.accuracy}"
                    )

                    finish(location)
                }
                .addOnFailureListener { error ->

                    Log.e("TEST_TRACKING_FIX", "Fix failed: ${error.message}")

                    finish(null)
                }

        } catch (e: Exception) {

            Log.e("TEST_TRACKING_FIX", "Could not ask for a fix: ${e.message}")

            finish(null)
        }
    }
}
