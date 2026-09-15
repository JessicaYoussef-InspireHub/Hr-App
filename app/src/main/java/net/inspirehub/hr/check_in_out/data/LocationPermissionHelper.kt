package net.inspirehub.hr.check_in_out.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun hasBackgroundLocationPermission(context: Context): Boolean {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    } else {
        true
    }
}

fun hasForegroundLocationPermission(context: Context): Boolean {

    val fineGranted =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    val coarseGranted =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    return fineGranted || coarseGranted
}

/**
 * Everything tracking needs before the service may be started at all.
 *
 * Checked by the callers, not only inside the service: once
 * startForegroundService() has been called, Android gives the service a few
 * seconds to put a notification on screen and kills the process with
 * ForegroundServiceDidNotStartInTimeException if it does not - so a service that
 * discovers a missing permission and quietly stops itself is a crash, not a
 * graceful exit. The only safe answer is to never make the start in the first
 * place.
 */
fun hasTrackingLocationPermissions(context: Context): Boolean {

    return hasForegroundLocationPermission(context) &&
            hasBackgroundLocationPermission(context)
}
