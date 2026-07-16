package net.inspirehub.hr

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.Locale
import androidx.core.content.edit
import net.inspirehub.hr.check_in_out.data.CompanyLocation
import net.inspirehub.hr.sign_in.data.Company
import java.util.Date

class SharedPrefManager(context: Context) {

    fun saveEmployeeName(name: String?) {
        if (!name.isNullOrBlank()) {
            prefs.edit { putString("employee_name", name) }
        }
    }

    fun getEmployeeName(): String? {
        val name = prefs.getString("employee_name", null)
        return if (name.isNullOrBlank()) null else name
    }


    fun saveAllowTimeOffWithMinutes(value: Boolean) {
        prefs.edit { putBoolean("allow_time_off_With_minutes", value) }
    }

    fun getAllowTimeOffWithMinutes(): Boolean {
        return prefs.getBoolean("allow_time_off_With_minutes", false)
    }

    fun clearCheckOutScheduledTime() {
        prefs.edit { remove("check_out_scheduled_time") }
        Log.d("SharedPrefManager", "🗑️ Check-Out time cleared")
    }


    fun saveLastOfflineActionTime(date: Date) {
        prefs.edit { putLong("last_offline_action_time", date.time) }
    }

    fun getLastOfflineActionTime(): Date? {
        val time = prefs.getLong("last_offline_action_time", 0L)
        return if (time != 0L) Date(time) else null
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveCompanyUrl(url: String) {
        prefs.edit { putString("companyurl", url) }
    }

    fun getCompanyUrl(): String? {
        return prefs.getString("companyurl", null)
    }

    fun saveAllowedLocationsIds(ids: List<Int>) {
        val idsString = ids.joinToString(separator = ",")
        prefs.edit { putString("allowed_locations_ids", idsString) }
    }

    fun getAllowedLocationsIds(): List<Int> {
        val idsString = prefs.getString("allowed_locations_ids", "") ?: ""
        return if (idsString.isEmpty()) emptyList()
        else idsString.split(",").mapNotNull { it.toIntOrNull() }
    }

    fun saveCompaniesLatLng(companies: List<Company>) {
        // addressId|name|lat,lng
        val listString = companies.joinToString(";") {
            "${it.address.id}|${it.name}|${it.address.latitude},${it.address.longitude}|${it.address.allowed_distance}"
        }
        prefs.edit { putString("companies_lat_lng", listString) }
    }

    fun getCompaniesLatLng(): List<CompanyLocation> {
        val listString = prefs.getString("companies_lat_lng", "") ?: ""
        if (listString.isEmpty()) return emptyList()

        return listString.split(";").mapNotNull { item ->
            val parts = item.split("|")
            if (parts.size != 4) return@mapNotNull null

            val id = parts[0].toIntOrNull() ?: return@mapNotNull null
            val name = parts[1]
            val latLng = parts[2].split(",")
            val allowedDistance = parts[3].toDoubleOrNull() ?: return@mapNotNull null

            if (latLng.size != 2) return@mapNotNull null

            CompanyLocation(
                id = id,
                name = name,
                lat = latLng[0].toDouble(),
                lng = latLng[1].toDouble(),
                allowedDistance = allowedDistance
            )
        }
    }



//    fun getCompaniesLatLng(): List<CompanyLocation> {
//        val listString = prefs.getString("companies_lat_lng", "") ?: ""
//        if (listString.isEmpty()) return emptyList()
//
//        return listString.split(";").mapNotNull { item ->
//            val parts = item.split("|")
//            if (parts.size != 3) return@mapNotNull null
//
//            val id = parts[0].toIntOrNull() ?: return@mapNotNull null
//            val name = parts[1]
//            val latLng = parts[2].split(",")
//            if (latLng.size != 2) return@mapNotNull null
//
//            CompanyLocation(
//                id = id,
//                name = name,
//                lat = latLng[0].toDouble(),
//                lng = latLng[1].toDouble()
//            )
//        }
//    }



    fun saveBaseUrl(url: String) {
        prefs.edit { putString("base_url", url) }
    }

    fun getBaseUrl(): String? {
        return prefs.getString("base_url", null)
    }

    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean("dark_mode", false)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean("dark_mode", enabled)
        }
        Log.d("DARK_MODE", "Saved = $enabled")
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

    fun saveLongitude(lng: Double) {
        prefs.edit { putString("longitude", lng.toString()) }
    }

    fun saveAllowedDistance(distance: Double) {
        prefs.edit { putString("allowedDistance", distance.toString()) }
    }

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
