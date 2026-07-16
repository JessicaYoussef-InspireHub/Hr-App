package net.inspirehub.hr.attendance.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AttendanceResponse(
    val status: String,
    val count: Int,
    val total_worked_hours: Double,
    val total_expected_hours: Double,
    val data: List<AttendanceItem>,
    val time_off_data: TimeOffData
)

@Serializable
data class AttendanceItem(
    val check_in: String? = null,
    val check_out: JsonElement? = null,
    val worked_hours: Double,
    val expected_hours: Double,
    val delay_minutes: Int,
    val delay: String,
    val is_late: Boolean
)

@Serializable
data class TimeOffData(
    val status: String
)