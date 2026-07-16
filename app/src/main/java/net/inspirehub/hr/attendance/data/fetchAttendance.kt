package net.inspirehub.hr.attendance.data

import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.lunch.data.ApiClient
import org.json.JSONObject

suspend fun fetchAttendance(
    context: Context,
    token: String?,
    fromDate: String,
    toDate: String
): AttendanceResponse? {

    return try {

        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("params", JSONObject().apply {
                put("token", token)
                put("from_date", fromDate)
                put("to_date", toDate)
            })
        }

        val response = ApiClient.httpClient.post(
            "$baseUrl/api/attendance"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()

        println("All Attendance Response : $responseText")
        val json = Json { ignoreUnknownKeys = true }

        val result = json
            .parseToJsonElement(responseText)
            .jsonObject["result"] ?: return null

        return json.decodeFromJsonElement(result)


    } catch (e: Exception) {
        e.printStackTrace()
        println("Attendance Error: ${e.message}")
        null
    }
}