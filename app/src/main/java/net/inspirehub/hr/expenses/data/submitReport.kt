package net.inspirehub.hr.expenses.data


import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.lunch.data.ApiClient
import org.json.JSONObject

suspend fun submitReport(
    context: Context,
    token: String,
    expenseId: Int,
): Boolean {

    return try {

        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("params", JSONObject().apply {
                put("token", token)
                put("expense_id", expenseId)
            })
        }

        val response = ApiClient.httpClient.post(
            "$baseUrl/api/expenses/submit"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("📤 Submit Report Response: $responseText")

        val json = Json.parseToJsonElement(responseText)
        val resultObject = json.jsonObject["result"]?.jsonObject

        val status = resultObject?.get("status")?.jsonPrimitive?.content

        val message = resultObject?.get("message")?.jsonPrimitive?.content
        println("Message: $message")

        status == "success"

    } catch (e: Exception) {
        println("❌ Submit Report error: ${e.message}")
        false
    }
}