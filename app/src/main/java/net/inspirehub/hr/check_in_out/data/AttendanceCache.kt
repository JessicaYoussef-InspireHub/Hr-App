package net.inspirehub.hr.check_in_out.data

import android.content.Context


class AttendanceCache(context: Context) {
    private val prefs = context.getSharedPreferences("attendance_cache", Context.MODE_PRIVATE)

    fun saveStatus(status: String, checkIn: String?, checkOut: String?) {
        prefs.edit().apply {
            putString("status", status)
            putString("lastCheckIn", checkIn)
            putString("lastCheckOut", checkOut)
            apply()
        }
    }

    fun getStatus(): Triple<String, String?, String?> {
        val status = prefs.getString("status", "checked_out") ?: "checked_out"
        val checkIn = prefs.getString("lastCheckIn", null)
        val checkOut = prefs.getString("lastCheckOut", null)
        return Triple(status, checkIn, checkOut)
    }
}
