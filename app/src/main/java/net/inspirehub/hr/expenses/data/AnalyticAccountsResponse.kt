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
data class AnalyticAccount(
    val id: Int,
    val name: String,
    val code: String,
    val plan_id: Int,
    val plan_name: String,
    val company_id: Int? = null,
    val company_name: String? = null
)


suspend fun fetchAnalyticAccounts(token: String, context: Context): List<AnalyticAccount> {
    return try {
        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("params", JSONObject().apply { put("token", token) })
        }

        val response = ApiClient.httpClient.post("$baseUrl/api/expenses/analytic_accounts") {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("test AnalyticAccounts Response: $responseText")

        val json = Json.parseToJsonElement(responseText)
        val dataJson = json.jsonObject["result"]?.jsonObject?.get("data")
        if (dataJson == null) return emptyList()

        Json.decodeFromJsonElement(dataJson)
    } catch (e: Exception) {
        println("test Error fetching analytic accounts: ${e.message}")
        emptyList()
    }
}