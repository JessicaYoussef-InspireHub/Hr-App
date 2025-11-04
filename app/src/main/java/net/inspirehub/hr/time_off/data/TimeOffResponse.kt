
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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


//RemainingLeavesResponse
@Serializable
data class RemainingLeavesResponse(
    val status: String,
    val year: Int,
    val leave_summary: List<LeaveSummary>,
    val permission_summary: List<PermissionSummary>,
    val total_allocated_days: Double,
    val total_used_days: Double,
    val total_remaining_days: Double,
    val total_allocated_hours: Double,
    val total_used_hours: Double,
    val total_remaining_hours: Double
)

@Serializable
data class LeaveSummary(
    val leave_type: String,
    val leave_type_id: Int,
    val allocated_days: Double,
    val used_days: Double,
    val remaining_days: Double
)

@Serializable
data class PermissionSummary(
    val leave_type: String,
    val leave_type_id: Int,
    val allocated_hours: Double,
    val used_hours: Double,
    val remaining_hours: Double
)

@Serializable
data class TimeOffYearResponse(
    val status: String,
    val year: Int,
    val records: TimeOffRecords
)



//MonthResponse
@Serializable
data class TimeOffResponse(
    val status: String,
    val month: String,
    val records: TimeOffRecords
)

@Serializable
data class TimeOffRecords(
    val daily_records: List<TimeOffRecord>,
    val hourly_records: List<HourlyTimeOffRecord>
)

@Serializable
data class HourlyTimeOffRecord(
    val leave_id: Int? = null,
    val leave_type: String,
    val leave_day: String? = null,
    val state: String,
    val duration_hours: Double,
    val request_hour_from: String? = null,
    val request_hour_to: String? = null
)

@Serializable
data class TimeOffRecord(
    val leave_id: Int,
    val leave_type: String,
    val start_date: String,
    val end_date: String,
    val state: String,
    val duration_days: Double
)




@Serializable
data class TimeOffRequest(
    val employee_token: String,
    val action: String,
)
@Serializable
data class JsonRpcResponse<T>(
    val jsonrpc: String,
    val id: String? = null,
    val result: T
)

@Serializable
data class TimeOffStatusResponse(
    val status: String,
    val records: TimeOffRecords
)

@Serializable
data class ErrorResponse(
    val status: String,
    val message: String? = null,
    val error_code: String? = null
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


suspend fun SendApiForTimeOff(
    timeOffRequest: TimeOffRequest
): Any? {
    return try {
        println("Sending TimeOff request...")

        val response: HttpResponse = httpClient.post("https://ahmedelzupeir-androidapp2.odoo.com/api/employee_time_off") {
            contentType(ContentType.Application.Json)
            setBody(timeOffRequest)
        }

        val rawResponse = response.bodyAsText()
        println("Raw API response: $rawResponse")

        val jsonElement = Json.parseToJsonElement(rawResponse)
        val resultObj = jsonElement.jsonObject["result"]?.jsonObject

        if (resultObj?.get("status")?.jsonPrimitive?.content == "error") {
            val error = Json.decodeFromString<JsonRpcResponse<ErrorResponse>>(rawResponse)
            println("⚠️ API Error: ${error.result.message} (code=${error.result.error_code})")
            return error.result
        }

        when (timeOffRequest.action) {
            "this_month_time_off" -> {
                val apiResponse = Json.decodeFromString<JsonRpcResponse<TimeOffResponse>>(rawResponse)
                println("✅ hourlyRecords size from API = ${apiResponse.result.records.hourly_records.size}")
                println("✅ dailyRecords size from API = ${apiResponse.result.records.daily_records.size}")

                apiResponse.result
            }

            "remaining_leaves" -> {
                val apiResponse = Json.decodeFromString<JsonRpcResponse<RemainingLeavesResponse>>(rawResponse)
                apiResponse.result
            }

            "this_year_time_off" -> {
                val apiResponse = Json.decodeFromString<JsonRpcResponse<TimeOffYearResponse>>(rawResponse)
                apiResponse.result
            }

            "time_off_status" -> {
                println("📨 Decoding time_off_status skipped, raw JSON:\n test$rawResponse")
                val apiResponse = Json.decodeFromString<JsonRpcResponse<TimeOffStatusResponse>>(rawResponse)
                apiResponse.result

            }

            else -> {
                println("⚠️ Unknown action: ${timeOffRequest.action}")
                null
            }
        }

    } catch (e: Exception) {
        println("Exception during API call: ${e.message}")
        null
    }
}





