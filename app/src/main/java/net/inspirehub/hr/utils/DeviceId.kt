package net.inspirehub.hr.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

/**
 * Gives the phone's own number for this app (ANDROID_ID).
 *
 * The server counts how many phones one account uses, and it tells them apart
 * by this number. It is sent with the sign in as `mobile_id`.
 *
 * Note: the number changes after a factory reset, so the same phone can look
 * like a new one to the server.
 */
@SuppressLint("HardwareIds")
fun getDeviceId(context: Context): String =
    Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    ).orEmpty()
