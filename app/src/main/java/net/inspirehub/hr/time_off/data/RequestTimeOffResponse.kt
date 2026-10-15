package net.inspirehub.hr.time_off.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.http.contentType
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.inspirehub.hr.SharedPrefManager
import java.time.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.coroutines.CancellationException

@Serializable
data class TimeOffAttachment(
    val name: String,
    val data: String
)

@Serializable
data class TimeOffRequestForRequestEmployee(
    val leave_id: Int? = null,
    val employee_token: String,
    val action: String,
    val leave_type_id: Int? = null,
    val description: String? = null,
    val request_date_from: String? = null,
    val request_date_to: String? = null,
    val request_date_from_period: String? = null,
    val request_unit_half: Boolean? = null,
    val request_hour_from: Double? = null,
    val request_hour_to: Double? = null,
    val request_unit_hours: Boolean? = null,
    val permission_period: String? = null,
    val permission_duration: Double? = null,
    val attachments: List<TimeOffAttachment>? = null

)

@Serializable
data class RequestTimeOffResponse(
    val jsonrpc: String?,
    val id: String?,
    val result: ResultData?
)

@Serializable
data class ResultData(
    val status: String? = null,
    val leave_id: Int? = null,
    val message: String? = null,
    val leave_type: String? = null,
    val duration: Duration? = null,
    val allocation: Allocation? = null,
    val error_code: String? = null
)


@Serializable
data class Duration(
    val value: Double,
    val unit: String
)

@Serializable
data class Allocation(
    val allocated: Double,
    val used: Double,
    val remaining: Double,
    val unit: String
)

data class HolidaysResult(
    val weekendText: String,
    val weekendDays: Set<String>,
    val holidaysText: String,
    val publicHolidayDates: Set<LocalDate>
)

@Serializable
data class LeaveTypeRequest(
    val employee_token: String,
    val action: String
)

@Serializable
data class LeaveTypeResponse(
    val jsonrpc: String? = null,
    val id: String? = null,
    val result: LeaveTypeResult
)

@Serializable
data class LeaveTypeResult(
    val status: String,
    val leave_types: List<LeaveType>
)

@Serializable
data class LeaveType(
    val id: Int,
    val name: String,
    val request_unit: String,
    val enable_leave_time_slot: Boolean = false,
    val requires_allocation: String,
    val remaining_balance: Float? = null,
    val original_balance: Float? = null,
    val color: String? = null,
    val support_document: Boolean = false
)


private val apiJson = Json {
    prettyPrint = false
    isLenient = true
    ignoreUnknownKeys = true
    explicitNulls = false
}


private val httpClient = HttpClient {
    // Configure Ktor client here
    install(Logging) {
        level = LogLevel.ALL
    }

    install(ContentNegotiation) {
        json(apiJson)
    }
}







suspend fun fetchEmployeeLeaveTypes(
    context: Context,
    token: String
): LeaveTypeResponse? {
    return try {
        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()

        val response: HttpResponse = httpClient.post(
            "$companyUrl/api/request_time_off"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                LeaveTypeRequest(
                    employee_token = token,
                    action = "get_employee_leave_type"
                )
            )
        }

        val bodyText = response.bodyAsText()
        Log.d("LEAVE_TYPES", "HTTP status: ${response.status.value}")

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        json.decodeFromString<LeaveTypeResponse>(bodyText)

    } catch (e: CancellationException) {
        // Preserve coroutine cancellation when the composable leaves composition.
        throw e
    } catch (e: Exception) {
        Log.e("LEAVE_TYPES", "Failed to fetch leave types", e)
        null
    }
}





@RequiresApi(Build.VERSION_CODES.O)
suspend fun fetchAndPrintHolidays(
    token: String ,
    context: Context): HolidaysResult {
    return try {
        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()
        val response: HttpResponse = httpClient.post("$companyUrl/api/request_time_off") {
            contentType(ContentType.Application.Json)
            setBody(
                TimeOffRequestForRequestEmployee(
                    employee_token = token,
                    action = "weekend_request"
                )
            )
        }

        val rawResponse = response.bodyAsText()
        val json = Json { ignoreUnknownKeys = true }
        val root = json.parseToJsonElement(rawResponse).jsonObject
        val result = root["result"]?.jsonObject

        // 1. Get weekly off days
        val weeklyOffs = result?.get("weekly_offs")?.jsonObject
        val dayNames = weeklyOffs?.values?.map { it.jsonPrimitive.content }?.toSet() ?: emptySet()
        val weekendText = if (dayNames.isNotEmpty()) "Weekends: ${dayNames.joinToString("، ")}" else "There are no weekly holidays."

        // 2. Get public holidays
        val holidaysArray = result?.get("public_holidays")?.jsonArray
        val holidaysText = holidaysArray?.joinToString("\n") { element ->
            val obj = element.jsonObject
            val name = obj["name"]?.jsonPrimitive?.content ?: "Without a name"
            val start = obj["start_date"]?.jsonPrimitive?.content ?: "?"
            val end = obj["end_date"]?.jsonPrimitive?.content ?: "?"
            "$name\n from $start to $end"
        } ?: "There are no official holidays."

        val publicHolidayDates = holidaysArray?.flatMap { element ->
            val obj = element.jsonObject
            val startStr = obj["start_date"]?.jsonPrimitive?.content
            val endStr = obj["end_date"]?.jsonPrimitive?.content

            if (startStr != null && endStr != null) {
                val startDate = LocalDate.parse(startStr)
                val endDate = LocalDate.parse(endStr)
                generateSequence(startDate) { date ->
                    if (date < endDate) date.plusDays(1) else if (date == endDate) null else null
                }.plus(endDate).toList()
            } else {
                emptyList()
            }
        }?.toSet() ?: emptySet()

        HolidaysResult(
            weekendText = weekendText,
            weekendDays = dayNames,
            holidaysText = holidaysText,
            publicHolidayDates = publicHolidayDates
        )

    } catch (e: Exception) {
        println("Failed: ${e.message}")
        HolidaysResult(
            weekendText = "Failed to load weekends",
            weekendDays = setOf(),
            holidaysText = "Failed to load official holidays",
            publicHolidayDates = emptySet()
        )
    }
}







suspend fun sendApiForRequestTimeOff(
    context: Context,
    timeOffRequestForRequestEmployee: TimeOffRequestForRequestEmployee
): RequestTimeOffResponse? {
    return try {
        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()

        val requestJson = apiJson.encodeToString(
            timeOffRequestForRequestEmployee
        )

        val jsonObject = apiJson.parseToJsonElement(requestJson).jsonObject

        println("Sending SendApiForRequestTimeOff request...")
        println("Request fields: ${jsonObject.keys}")
        println("Attachment name: ${timeOffRequestForRequestEmployee.attachments}")

        val response: HttpResponse = httpClient.post(
            "$companyUrl/api/request_time_off"
        ) {
            contentType(ContentType.Application.Json)
            setBody(requestJson)
        }

        val rawResponse = response.bodyAsText()
        println("Request time-off API HTTP status: ${response.status.value}")
        println("Request time-off API response: $rawResponse")

        apiJson.decodeFromString<RequestTimeOffResponse>(rawResponse)

    } catch (e: Exception) {
        println("Exception during API call: ${e.message}")
        null
    }
}