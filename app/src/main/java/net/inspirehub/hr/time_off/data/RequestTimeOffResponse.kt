package net.inspirehub.hr.time_off.data

import android.content.Context
import android.os.Build
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
import net.inspirehub.hr.scan_qr_code.data.AppConfig
import java.time.LocalDate


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
    val request_hour_from: String? = null,
    val request_hour_to: String? = null,
    val request_unit_hours: Boolean? = null

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
    val requires_allocation: String,
    val remaining_balance: Float? = null,
    val original_balance: Float? = null,
    val color: String? = null
)



private val httpClient = HttpClient {
    // Configure Ktor client here
    install(Logging) {
        level = LogLevel.ALL
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}




suspend fun fetchEmployeeLeaveTypes(
    context: Context,
    token: String): LeaveTypeResponse? {
    return try {
        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()
        val response: HttpResponse = httpClient.post("$companyUrl/api/request_time_off") {
            contentType(ContentType.Application.Json)
            setBody(
                LeaveTypeRequest(
                    employee_token = token,
                    action = "get_employee_leave_type"
                )
            )
        }

        val bodyText = response.bodyAsText()
        println("✅ Response get_employee_leave_type : $bodyText")

        Json.decodeFromString<LeaveTypeResponse>(bodyText)
    } catch (e: Exception) {
        println("❌ Error fetching leave types: ${e.message}")
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
        println("Sending SendApiForRequestTimeOff request...")
        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()

        val response: HttpResponse = httpClient.post("$companyUrl/api/request_time_off") {
            contentType(ContentType.Application.Json)
            setBody(timeOffRequestForRequestEmployee)
        }

        val rawResponse = response.bodyAsText()
        println("Raw API response: $rawResponse")

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        json.decodeFromString<RequestTimeOffResponse>(rawResponse)
    } catch (e: Exception) {
        println("Exception during API call: ${e.message}")
        null
    }
}