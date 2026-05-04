package net.inspirehub.hr.check_in_out.data

import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.inspirehub.hr.SharedPrefManager

suspend fun checkLocationUpdatesRaw(
    context: Context,
    token: String
): String? {
    return try {
        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()

        val response: HttpResponse =
            httpClient.post("$companyUrl/api/check_location_updates") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "params" to mapOf(
                            "employee_token" to token
                        )
                    )
                )
            }

        val body = response.bodyAsText()
        println("📍 API RESPONSE Update location: $body")

        body

    } catch (e: Exception) {
        println("🔴 ERROR: ${e.message}")
        null
    }
}