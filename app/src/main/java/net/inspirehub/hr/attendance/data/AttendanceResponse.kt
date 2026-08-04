package net.inspirehub.hr.attendance.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AttendanceResponse(
    val status: String,
    val count: Int,
    val expected_worked: Double,
    val total_worked_hours: Double,
    val summary: Map<String, AttendanceSummary>,
    val data: List<AttendanceDayResponse>
)

@Serializable
data class AttendanceSummary(
    val count: Int,
    val total_hours: Double,
    val total_minutes: Double
)

@Serializable
data class AttendanceDayResponse(
    val date: String,
    val entries: List<AttendanceItem>
)

@Serializable
data class AttendanceItem(
    val entry_name: String,
    val work_entry_type: String,
    val duration: Double,
    val duration_time: String,
    val from_date: JsonElement? = null,
    val to_date: JsonElement? = null
)