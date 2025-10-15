package com.example.myapplicationnewtest.check_in_out.data

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("ObsoleteSdkInt")
fun isDeviceTimeAndTimeZoneAutomatic(context: Context): Boolean {
    return try {
        val autoTime = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            android.provider.Settings.Global.getInt(
                context.contentResolver,
                android.provider.Settings.Global.AUTO_TIME
            ) == 1
        } else {
            android.provider.Settings.System.getInt(
                context.contentResolver,
                android.provider.Settings.System.AUTO_TIME
            ) == 1
        }

        val autoTimeZone = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            android.provider.Settings.Global.getInt(
                context.contentResolver,
                android.provider.Settings.Global.AUTO_TIME_ZONE
            ) == 1
        } else {
            android.provider.Settings.System.getInt(
                context.contentResolver,
                android.provider.Settings.System.AUTO_TIME_ZONE
            ) == 1
        }

        autoTime && autoTimeZone
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

