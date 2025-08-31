package com.example.myapplicationnewtest.time_off.data

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
    val leave_type_id: Int
)

@Serializable
data class LeaveDurationResponse(
    val jsonrpc: String,
    val id: String? = null,
    val result: LeaveDurationResult
)

@Serializable
data class LeaveDurationResult(
    val success: Boolean,
    val data: LeaveDurationData
)

@Serializable
data class LeaveDurationData(
    val leave_type_unit: String,
    val request_date_from: String,
    val request_date_to: String,
    val date_from: String,
    val date_to: String,
    val days: Double,
    val hours: Double
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
    leaveTypeId: Int
): LeaveDurationResponse {
    val request = LeaveDurationRequest(
        employee_token = employeeToken,
        request_date_from = requestDateFrom,
        request_date_to = requestDateTo,
        leave_type_id = leaveTypeId
    )

    android.util.Log.d(
        "LEAVE_REQUEST",
        "Request Body: $request"
    )

    val response = client.post("https://ahmedelzupeir-androidapp2.odoo.com/api/leave/duration") {
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    // اطبع الـ status code
    Log.d("LEAVE_REQUEST", "Status: ${response.status}")

    // اطبع الـ response raw string
    val raw = response.bodyAsText()
    Log.d("LEAVE_REQUEST", "Raw response: $raw")

    return try {
        response.body()
    } catch (e: Exception) {
        Log.e("LEAVE_REQUEST", "Parse error: ${e.message}")
        throw e
    }
}

