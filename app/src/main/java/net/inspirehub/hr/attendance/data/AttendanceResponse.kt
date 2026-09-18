package net.inspirehub.hr.attendance.data

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import net.inspirehub.hr.scan_qr_code.data.AppConfig.baseUrl
import net.inspirehub.hr.time_off.data.client

@Serializable
data class WorkEntryType(
    val id: Int,
    val name: String,
    val icon_image: String? = null
)

@Serializable
data class WorkEntryTypesResult(
    val status: String,
    val count: Int,
    val data: Map<String, WorkEntryType>
)

@Serializable
data class WorkEntryTypesResponse(
    val jsonrpc: String,
    val id: String? = null,
    val result: WorkEntryTypesResult
)


suspend fun getWorkEntryTypes(): WorkEntryTypesResult {

    val response = client
        .post("$baseUrl/api/work-entry-types") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }
        .body<WorkEntryTypesResponse>()

    return response.result
}



@Serializable
data class WorkEntryTypeSummary(
    val count: Int,
    val total_hours: Double,
    val total_minutes: Double,
    val work_entry_type_color_hex: String?,
    val id_icon: Int?
)

@Serializable
data class AttendanceResponse(
    val status: String,
    val count: Int,
    val expected_worked: Double,
    val total_worked_hours: Double,
    val summary: Map<String, WorkEntryTypeSummary>,
    val data: List<AttendanceDayResponse>
)

@Serializable
data class AttendanceDayResponse(
    val date: String,
    val entries: List<AttendanceItem>
)

@Serializable
data class AttendanceItem(
    val work_entry_type: String,
    val duration: Double,
    val duration_time: String,
    val from_date: String? = null,
    val to_date: String? = null
)