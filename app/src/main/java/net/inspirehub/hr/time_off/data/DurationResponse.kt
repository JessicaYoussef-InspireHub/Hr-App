package net.inspirehub.hr.time_off.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import kotlinx.serialization.Serializable
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Serializable
data class LeaveDurationRequest(
    val employee_token: String,
    val request_date_from: String,
    val request_date_to: String,
    val leave_type_id: Int,
    val request_unit_half: Boolean? = null,
    val request_unit_hours: Boolean? = null,
    val request_date_from_period: String? = null,
    val request_hour_from: Double? = null,
    val request_hour_to: Double? = null
)

@Serializable
data class LeaveDurationResponse(
    val jsonrpc: String,
    val id: String? = null,
    val result: LeaveDurationResultCommon
)

@Serializable
data class LeaveDurationResultCommon(
    val status: String? = null,
    val message: String? = null,
    val error_code: String? = null,
    val success: Boolean? = null,
    val data: LeaveDurationData? = null
)




@Serializable
data class LeaveDurationData(
    val leave_type_unit: String,
    val request_date_from: String,
    val request_date_to: String,
    val date_from: String,
    val date_to: String,
    val days: Double? = null,
    val hours: Double? = null,
)



val client = HttpClient{
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}


suspend fun getLeaveDuration(
    employeeToken: String,
    requestDateFrom: String,
    requestDateTo: String,
    leaveTypeId: Int,
    requestUnitHalf: Boolean? = null,
    requestUnitHours: Boolean? = null,
    requestDateFromPeriod: String? = null,
    requestHourFrom: Double? = null,
    requestHourTo: Double? = null
): LeaveDurationResponse {
    val request = LeaveDurationRequest(
        employee_token = employeeToken,
        request_date_from = requestDateFrom,
        request_date_to = requestDateTo,
        leave_type_id = leaveTypeId,
        request_unit_half = requestUnitHalf,
        request_unit_hours = requestUnitHours,
        request_date_from_period = requestDateFromPeriod,
        request_hour_from = requestHourFrom,
        request_hour_to = requestHourTo
    )

  Log.d(
        "LEAVE_REQUEST",
        "Request Body: $request"
    )

    val response = client.post("https://ahmedelzupeir-androidapp21.odoo.com/api/leave/duration") {
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    Log.d("LEAVE_REQUEST", "Status: ${response.status}")

    val raw = response.bodyAsText()
    Log.d("LEAVE_REQUEST", "Raw response: $raw")

    return try {
        response.body()
    } catch (e: Exception) {
        Log.e("LEAVE_REQUEST", "Parse error: ${e.message}")
        throw e
    }
}

