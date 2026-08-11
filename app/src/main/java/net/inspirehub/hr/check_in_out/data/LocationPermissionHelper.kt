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