package net.inspirehub.hr.expenses.data

import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.lunch.data.ApiClient
import org.json.JSONObject

@Serializable
data class Tax(
    val id: Int,
    val name: String,
    val amount: Double,
    val amount_type: String,
    val description: String,
    val company_id: Int,
    val company_name: String
)


suspend fun fetchTaxes(
    context: Context,
    token: String
): List<Tax> {
    return try {
        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("params", JSONObject().apply {
                put("token", token)
            })
        }

        val response = ApiClient.httpClient.post(
            "$baseUrl/api/expenses/taxes"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("Taxes Response: $responseText")

        val json = Json.parseToJsonElement(responseText)

        val taxesJson = json.jsonObject["result"]?.jsonObject?.get("data")
        if (taxesJson == null) return emptyList()

        Json.decodeFromJsonElement(taxesJson)

    } catch (e: Exception) {
        println("Error fetching taxes: ${e.message}")
        emptyList()
    }
}