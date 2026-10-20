package net.inspirehub.hr.attendance.data

import android.content.Context
import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import net.inspirehub.hr.resolveCompanyUrl
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


/**
 * Reads the work entry types the attendance rows are coloured and labelled with.
 *
 * Returns null when the call fails. The screen already draws without them, so a
 * server that is unreachable must not take the screen - or the app - down.
 */
suspend fun getWorkEntryTypes(context: Context): WorkEntryTypesResult? {

    return try {

        val baseUrl = resolveCompanyUrl(context)

        val response = client
            .post("$baseUrl/api/work-entry-types") {
                contentType(ContentType.Application.Json)
                setBody("{}")
            }
            .body<WorkEntryTypesResponse>()

        response.result

    } catch (e: CancellationException) {

        throw e

    } catch (e: Exception) {

        Log.e("WORK_ENTRY_TYPES", "Failed to load work entry types", e)

        null
    }
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