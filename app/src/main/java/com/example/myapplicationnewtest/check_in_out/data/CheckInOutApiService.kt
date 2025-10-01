package com.example.myapplicationnewtest.check_in_out.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.http.contentType


@Serializable
data class AttendanceStatusResponseWrapper(
    val result: AttendanceStatusResult
)

@Serializable
data class AttendanceStatusResult(
    val status: String,
    val message: String,
    val attendance_status: String? = null,
    val worked_hours: Double? = null,
    val last_check_in: String? = null,
    val last_check_out: String? = null
)


val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

suspend fun sendAttendanceAction(
    token: String,
    action: String,
    latitude: String,
    longitude: String ): AttendanceStatusResult? {
    return try {
        val response: HttpResponse = httpClient.post("https://ahmedelzupeir-androidapp21.odoo.com/api/employee_attendance") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "params" to mapOf(
                        "employee_token" to token,
                        "action" to action,
                        "lat" to latitude,
                        "lng" to longitude
                    )
                )
            )
        }

        println("Status: ${response.status}")
        println("Headers: ${response.headers}")
        println("Body: ${response.bodyAsText()}")

        val responseBody = response.body<AttendanceStatusResponseWrapper>()
        println("⚪ Server response: $responseBody")
        responseBody.result

    } catch (e: Exception) {
        println("🔴 Exception: ${e.message}")
        null
    }
}



suspend fun fetchAttendanceStatus(token: String): AttendanceStatusResult? {
    return try {
        val response: HttpResponse = httpClient.post("https://ahmedelzupeir-androidapp21.odoo.com/api/employee_attendance") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "params" to mapOf(
                        "employee_token" to token,
                        "action" to "status"
                    )
                )
            )
        }

        println("Status: ${response.status}")
        println("Headers: ${response.headers}")
        println("Body: ${response.bodyAsText()}")

        val responseBody = response.body<AttendanceStatusResponseWrapper>()
        println("⚪ Server response (status): $responseBody")
        responseBody.result

    } catch (e: Exception) {
        println("🔴 Exception fetching status: ${e.message}")
        null
    }
}

