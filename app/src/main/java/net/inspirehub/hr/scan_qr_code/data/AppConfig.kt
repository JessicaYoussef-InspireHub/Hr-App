package net.inspirehub.hr.scan_qr_code.data

import android.content.Context
import net.inspirehub.hr.SharedPrefManager

object AppConfig {
    var baseUrl: String = ""

    fun init(context: Context) {
        val prefs = SharedPrefManager(context)
        baseUrl = prefs.getBaseUrl() ?: ""
    }

    fun setBaseUrl(url: String, context: Context) {
        baseUrl = url
        SharedPrefManager(context).saveBaseUrl(url)
    }
}

