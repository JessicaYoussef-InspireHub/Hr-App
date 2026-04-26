package net.inspirehub.hr.expenses.data

import kotlinx.serialization.Serializable
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


@Serializable
data class ExpenseReport(
    val sheet_id: Int,
    val name: String,
    val employee: String,
    val state: String,
    val total_amount: Double,
    val expenses: List<ReportExpense>
)

@Serializable
data class ReportExpense(
    val id: Int,
    val name: String,
    val amount: Double,
    val date: String
)

suspend fun fetchReports(
    context: Context,
    token: String
): List<ExpenseReport> {

    return try {

        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()
        val finalToken = sharedPref.getToken() ?: token

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("params", JSONObject().apply {
                put("token", finalToken)
            })
        }

        val response = ApiClient.httpClient.post(
            "$baseUrl/api/expenses/report"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("📦 Get Reports Response: $responseText")

        val json = Json.parseToJsonElement(responseText)
        val resultObject = json.jsonObject["result"]?.jsonObject

        val dataJson = resultObject?.get("data")

        if (dataJson == null) {
            println("❌ Get reports data is null")
            return emptyList()
        }

        Json { ignoreUnknownKeys = true }
            .decodeFromJsonElement<List<ExpenseReport>>(dataJson)

    } catch (e: Exception) {
        println("❌ Error fetching reports: ${e.message}")
        emptyList()
    }
}