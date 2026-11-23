package net.inspirehub.hr

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.Locale
import androidx.core.content.edit
import net.inspirehub.hr.check_in_out.data.CompanyLocation
import net.inspirehub.hr.sign_in.data.Company
import net.inspirehub.hr.sign_in.data.EmployeeData
import java.util.Date

class SharedPrefManager(context: Context) {

    private val keyHasSeenDeductionIntro = "has_seen_deduction_intro"
    private val keyHasSeenProfileIntro = "has_seen_profile_intro"
    private val keyHasSeenSaudiLanguageIntro = "has_seen_saudi_language_intro"
    private val keyHasSeenSoundIntro = "has_seen_sound_intro"

    fun hasSeenSoundIntro(): Boolean {
        return prefs.getBoolean(keyHasSeenSoundIntro, false)
    }

    fun setSoundIntroSeen() {
        prefs.edit {
            putBoolean(keyHasSeenSoundIntro, true)
        }
    }
    fun hasSeenSaudiLanguageIntro(): Boolean {
        return prefs.getBoolean(keyHasSeenSaudiLanguageIntro, false)
    }

    fun setSaudiLanguageIntroSeen() {
        prefs.edit {
            putBoolean(keyHasSeenSaudiLanguageIntro, true)
        }
    }

    fun hasSeenProfileIntro(): Boolean {
        return prefs.getBoolean(keyHasSeenProfileIntro, false)
    }

    fun setProfileIntroSeen() {
        prefs.edit {
            putBoolean(keyHasSeenProfileIntro, true)
        }
    }
    fun hasSeenDeductionIntro(): Boolean {
        return prefs.getBoolean(keyHasSeenDeductionIntro, false)
    }

    fun setDeductionIntroSeen() {
        prefs.edit {
            putBoolean(keyHasSeenDeductionIntro, true)
        }
    }

    private val hideAutoNotification = "hide_auto_notification"

    fun setHideAutoNotification(hide: Boolean) {
        prefs.edit {
            putBoolean(hideAutoNotification, hide)
        }
    }

    fun isHideAutoNotification(): Boolean {
        return prefs.getBoolean(hideAutoNotification, true)
    }

    fun saveLoginEmail(email: String) {
        prefs.edit {
            putString("login_email", email)
        }
    }

    fun getLoginEmail(): String? {
        return prefs.getString("login_email", null)
    }

    fun saveLoginPassword(password: String) {
        prefs.edit {
            putString("login_password", password)
        }
    }

    fun getLoginPassword(): String? {
        return prefs.getString("login_password", null)
    }

    fun saveAttendanceStatus(status: String) {
        prefs.edit {
            putString(KEY_ATTENDANCE_STATUS, status)
        }
    }

    fun getAttendanceStatus(): String {
        return prefs.getString(
            KEY_ATTENDANCE_STATUS,
            "checked_out"
        ) ?: "checked_out"
    }

    /**
     * The punch did not come from this device: the backend pushes
     * attendance_mode_alert when the employee is checked in or out from the web, a
     * kiosk or by HR, so whatever the check in/out screen is showing is already
     * stale.
     *
     * Stored as "<status>|<written at>" because SharedPreferences only notifies its
     * listeners when the value really changes - two manual check-outs in a row carry
     * the same status, and the second one has to reach the screen as well.
     */
    fun saveAttendanceAlert(status: String) {
        prefs.edit {
            putString(KEY_ATTENDANCE_STATUS, status)
            putString(
                KEY_ATTENDANCE_ALERT,
                "$status|${System.currentTimeMillis()}"
            )
        }
    }

    fun getAttendanceAlertStatus(): String? {
        return prefs.getString(KEY_ATTENDANCE_ALERT, null)
            ?.substringBefore('|')
            ?.takeIf { it.isNotBlank() }
    }

    private val checkInReminderEnabled = "check_in_reminder_enabled"
    private val checkOutReminderEnabled = "check_out_reminder_enabled"

    fun setCheckInReminderEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(checkInReminderEnabled, enabled)
        }
    }

    fun isCheckInReminderEnabled(): Boolean {
        return prefs.getBoolean(
            checkInReminderEnabled,
            false
        )
    }

    fun setCheckOutReminderEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(checkOutReminderEnabled, enabled)
        }
    }

    fun isCheckOutReminderEnabled(): Boolean {
        return prefs.getBoolean(
            checkOutReminderEnabled,
            false
        )
    }


    fun saveLastAttendanceReminderType(type: String?) {
        prefs.edit {
            if (type == null) {
                remove("last_attendance_reminder_type")
            } else {
                putString("last_attendance_reminder_type", type)
            }
        }
    }

    fun getLastAttendanceReminderType(): String? {
        return prefs.getString(
            "last_attendance_reminder_type",
            null
        )
    }


    fun saveIsTracked(value: Boolean) {
        prefs.edit { putBoolean(KEY_IS_TRACKED, value) }
    }

    fun getIsTracked(): Boolean {
        return prefs.getBoolean(KEY_IS_TRACKED, false)
    }

    /**
     * For the one screen that has to react to a value changing underneath it: an FCM
     * config update writes is_tracked from a background thread while the check in/out
     * screen is already on display, and nothing else would notice until the employee
     * left the screen and came back.
     *
     * The listener is called on the thread that wrote the value, so anything touching
     * UI state has to hop to the main thread itself.
     */
    fun registerChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {

        const val KEY_IS_TRACKED = "is_tracked"

        const val KEY_ATTENDANCE_STATUS = "attendance_status"

        const val KEY_ATTENDANCE_ALERT = "attendance_alert"

        /** The work sites: where each one is and how close counts as inside. */
        const val KEY_COMPANIES_LAT_LNG = "companies_lat_lng"

        /** Which of the work sites this employee may check in at. */
        const val KEY_ALLOWED_LOCATIONS_IDS = "allowed_locations_ids"

        /** How many minutes between two tracking readings. */
        const val KEY_TRACKING_INTERVAL_MINUTES = "tracking_interval_minutes"

        const val KEY_COMPANY_ADDRESSES = "company_addresses"

        const val KEY_EMPLOYEE_EMAIL = "employee_email"
        const val KEY_EMPLOYEE_JOB_TITLE = "employee_job_title"
        const val KEY_EMPLOYEE_DEPARTMENT = "employee_department"
        const val KEY_EMPLOYEE_BIRTHDAY = "employee_birthday"
        const val KEY_EMPLOYEE_COMPANY = "employee_company"
    }

    /**
     * Backend switch, sent next to is_tracked, that picks HOW the home screen
     * location tracking runs.
     *
     *  true  - a long-lived foreground service. Android owns its notification for as
     *          long as the service lives, so the notification is always on screen and
     *          the hide-auto-notification toggle can only minimize it.
     *  false - an AlarmManager chain that starts the service for one reading and lets
     *          it die again, the same shape as the attendance reminder. Nothing of
     *          ours runs between two readings, so the notification really disappears.
     *
     * Defaults to true so a backend that does not send the field yet keeps the
     * behavior it has today.
     */
    fun saveShowNotification(value: Boolean) {
        prefs.edit { putBoolean("show_notification", value) }
    }

    fun getShowNotification(): Boolean {
        return prefs.getBoolean("show_notification", true)
    }

    /**
     * Where the last reported reading was taken, used to decide whether the next one
     * moved far enough to be worth sending.
     *
     * In prefs rather than in a field because in alarm mode the process dies between
     * two readings, and a baseline that resets every time would report every fix.
     */
    fun saveLastTrackedLocation(
        latitude: Double,
        longitude: Double
    ) {
        prefs.edit {
            putString("last_tracked_latitude", latitude.toString())
            putString("last_tracked_longitude", longitude.toString())
        }
    }

    fun getLastTrackedLatitude(): Double? {
        return prefs.getString("last_tracked_latitude", null)?.toDoubleOrNull()
    }

    fun getLastTrackedLongitude(): Double? {
        return prefs.getString("last_tracked_longitude", null)?.toDoubleOrNull()
    }

    fun clearLastTrackedLocation() {
        prefs.edit {
            remove("last_tracked_latitude")
            remove("last_tracked_longitude")
        }
    }

    fun saveWorkingHoursOnly(value: Boolean) {
        prefs.edit { putBoolean("working_hours_only", value) }
    }

    fun getWorkingHoursOnly(): Boolean {
        return prefs.getBoolean("working_hours_only", false)
    }

    fun saveTrackingIntervalMinutes(value: Float) {
        prefs.edit { putFloat(KEY_TRACKING_INTERVAL_MINUTES, value) }
    }

    fun getTrackingIntervalMinutes(): Float {
        return prefs.getFloat(KEY_TRACKING_INTERVAL_MINUTES, 0f)
    }

    fun saveMinDistanceMeters(value: Float) {
        prefs.edit { putFloat("min_distance_meters", value) }
    }

    fun getMinDistanceMeters(): Float {
        return prefs.getFloat("min_distance_meters", 0f)
    }

    fun saveEmployeeName(name: String?) {
        if (!name.isNullOrBlank()) {
            prefs.edit { putString("employee_name", name) }
        }
    }

    fun getEmployeeName(): String? {
        val name = prefs.getString("employee_name", null)
        return if (name.isNullOrBlank()) null else name
    }

    /** The rest of the employee data the sign in answer carries, for the profile screen. */
    fun saveEmployeeProfile(
        employee: EmployeeData?,
        companyName: String?
    ) {
        if (employee == null) return
        prefs.edit {
            putString(KEY_EMPLOYEE_EMAIL, employee.email)
            putString(KEY_EMPLOYEE_JOB_TITLE, employee.job_title)
            putString(KEY_EMPLOYEE_DEPARTMENT, employee.department)
            putString(KEY_EMPLOYEE_BIRTHDAY, employee.birthday)
            putString(KEY_EMPLOYEE_COMPANY, companyName.orEmpty())
        }
    }

    fun getEmployeeEmail(): String? =
        prefs.getString(KEY_EMPLOYEE_EMAIL, null)?.takeIf { it.isNotBlank() }
            ?: getLoginEmail()

    fun getEmployeeJobTitle(): String? =
        prefs.getString(KEY_EMPLOYEE_JOB_TITLE, null)?.takeIf { it.isNotBlank() }

    fun getEmployeeDepartment(): String? =
        prefs.getString(KEY_EMPLOYEE_DEPARTMENT, null)?.takeIf { it.isNotBlank() }

    fun getEmployeeBirthday(): String? =
        prefs.getString(KEY_EMPLOYEE_BIRTHDAY, null)?.takeIf { it.isNotBlank() }

    fun getEmployeeCompany(): String? =
        prefs.getString(KEY_EMPLOYEE_COMPANY, null)?.takeIf { it.isNotBlank() }

    /** Readable address of each work site, keyed by address id. */
    fun getCompanyAddresses(): Map<Int, String> {
        val listString = prefs.getString(KEY_COMPANY_ADDRESSES, "") ?: ""
        if (listString.isEmpty()) return emptyMap()
        return listString.split(";").mapNotNull { item ->
            val id = item.substringBefore('|').toIntOrNull() ?: return@mapNotNull null
            id to item.substringAfter('|')
        }.toMap()
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
        prefs.edit { putString("companyUrl", url) }
    }

    fun getCompanyUrl(): String? {
        return prefs.getString("companyUrl", null)
    }

    fun saveAllowedLocationsIds(ids: List<Int>) {
        val idsString = ids.joinToString(separator = ",")
        prefs.edit { putString(KEY_ALLOWED_LOCATIONS_IDS, idsString) }
    }

    fun getAllowedLocationsIds(): List<Int> {
        val idsString = prefs.getString(KEY_ALLOWED_LOCATIONS_IDS, "") ?: ""
        return if (idsString.isEmpty()) emptyList()
        else idsString.split(",").mapNotNull { it.toIntOrNull() }
    }

    fun saveCompaniesLatLng(companies: List<Company>) {
        // addressId|name|lat,lng
        val listString = companies.joinToString(";") {
            "${it.address.id}|${it.name}|${it.address.latitude},${it.address.longitude}|${it.address.allowed_distance}"
        }
        // addressId|street, city, country - the separators are stripped from the text
        val addressesString = companies
            .filter { it.address.id != null }
            .joinToString(";") { company ->
                val text = listOf(
                    company.address.street,
                    company.address.city,
                    company.address.country
                )
                    .map { it.replace("|", " ").replace(";", ",").trim() }
                    .filter { it.isNotBlank() }
                    .joinToString(", ")
                "${company.address.id}|$text"
            }
        prefs.edit {
            putString(KEY_COMPANIES_LAT_LNG, listString)
            putString(KEY_COMPANY_ADDRESSES, addressesString)
        }
    }

    fun getCompaniesLatLng(): List<CompanyLocation> {
        val listString = prefs.getString(KEY_COMPANIES_LAT_LNG, "") ?: ""
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

    /**
     * Saves the full BCP-47 language tag: "en", "ar" or "ar-SA".
     */
    fun saveLanguage(languageTag: String) {
        prefs.edit { putString("app_language", languageTag) }
    }

    /**
     * Full language tag including region ("en", "ar", "ar-SA").
     * Use this when applying the app locale.
     */
    fun getLanguageTag(): String {
        return prefs.getString("app_language", null)?.takeIf { it.isNotBlank() }
            ?: Locale.getDefault().language
    }

    /**
     * Language code only ("en" or "ar"), so Saudi Arabic still returns "ar".
     * Used across the app for RTL / Arabic-digit formatting checks.
     */
    fun getLanguage(): String {
        return Locale.forLanguageTag(getLanguageTag()).language
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
        val hasSeenDeductionIntro = hasSeenDeductionIntro()
        val hasSeenProfileIntro = hasSeenProfileIntro()

        prefs.edit {
            clear()
            putBoolean(
                keyHasSeenDeductionIntro,
                hasSeenDeductionIntro
            )

            putBoolean(
                keyHasSeenProfileIntro,
                hasSeenProfileIntro
            )


            putBoolean(
                keyHasSeenSaudiLanguageIntro,
                hasSeenSaudiLanguageIntro()
            )

            putBoolean(
                keyHasSeenSoundIntro,
                hasSeenSoundIntro()
            )
        }
    }
}
