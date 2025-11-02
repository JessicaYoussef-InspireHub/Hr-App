package com.example.myapplicationnewtest

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale
import androidx.core.content.edit

class SharedPrefManager(context: Context) {
    fun saveServerExitTime(time: String) {
        prefs.edit { putString("server_exit_time", time) }
    }

    fun getServerExitTime(): String? {
        return prefs.getString("server_exit_time", null)
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("dark_mode", enabled) }
    }

    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean("dark_mode", false)
    }

    fun saveTimeDifference(diffMinutes: Long) {
        prefs.edit { putLong("time_difference", diffMinutes) }
    }

    fun getTimeDifference(): Long {
        return prefs.getLong("time_difference", -1L)
    }

    fun saveWasOfflineDuringTimeChange(value: Boolean) {
        prefs.edit {
            putBoolean("was_offline_during_time_change", value)
        }
    }

    fun wasOfflineDuringTimeChange(): Boolean {
        return prefs.getBoolean("was_offline_during_time_change", false)
    }

    fun setWasOfflineDuringTimeChange(value: Boolean) {
        prefs.edit {
            putBoolean("was_offline_during_time_change", value)
        }
    }

    fun saveLanguage(lang: String) {
        prefs.edit { putString("app_language", lang) }
    }

    fun getLanguage(): String {
        return prefs.getString("app_language", null) ?: Locale.getDefault().language
    }

    fun savePin(pin: String) {
        prefs.edit { putString("user_pin", pin) }
    }

    fun getPin(): String? {
        return prefs.getString("user_pin", null)
    }

    fun saveToken(token: String) {
        prefs.edit { putString("token", token) }
    }

    fun getToken(): String? = prefs.getString("token", null)

    fun saveTokenExpiry(expiry: String) {
        prefs.edit { putString("token_expiry", expiry) }
    }

    fun saveCompanyId(companyId: String) {
        prefs.edit { putString("companyId", companyId) }
    }

    fun getCompanyId(): String? = prefs.getString("companyId", null)

    fun saveApiKey(apiKey: String) {
        prefs.edit { putString("apiKey", apiKey) }
    }

    fun getApiKey(): String? = prefs.getString("apiKey", null)

    fun saveLatitude(lat: Double) {
        prefs.edit { putString("latitude", lat.toString()) }
    }

//    fun saveLatitude(lat: Double) {
//        prefs.edit().putString("latitude", "27.190936").apply()
//    }

    fun getLatitude(): Double = prefs.getString("latitude", "0.0")?.toDoubleOrNull() ?: 0.0

    fun saveLongitude(lng: Double) {
        prefs.edit { putString("longitude", lng.toString()) }
    }

//    fun saveLongitude(lng: Double) {
//        prefs.edit().putString("longitude", "31.187951").apply()
//    }

    fun getLongitude(): Double = prefs.getString("longitude", "0.0")?.toDoubleOrNull() ?: 0.0

    fun saveAllowedDistance(distance: Double) {
        prefs.edit { putString("allowedDistance", distance.toString()) }
    }

    fun getAllowedDistance(): Double = prefs.getString("allowedDistance", "0.0")?.toDoubleOrNull() ?: 0.0

    fun setFingerprintAuthSuccess(success: Boolean) {
        prefs.edit { putBoolean("fingerprint_success", success) }
    }

    fun isFingerprintAuthSuccess(): Boolean {
        return prefs.getBoolean("fingerprint_success", false)
    }

    fun setProtectionSkipped(value: Boolean) {
        prefs.edit { putBoolean("protection_skipped", value) }
    }

    fun isProtectionSkipped(): Boolean {
        return prefs.getBoolean("protection_skipped", false)
    }

    fun saveProtectionMethod(method: Int) {
        prefs.edit { putInt("protection_method", method) }
    }

    fun getProtectionMethod(): Int {
        return prefs.getInt("protection_method", 0)
    }

    fun clearAll() {
        prefs.edit { clear() }
    }
}
