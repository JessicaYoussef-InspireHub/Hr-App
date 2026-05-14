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
import org.json.JSONArray
import org.json.JSONObject

suspend fun editReport(
    context: Context,
    token: String,
    sheetId: Int,
    expenseIds: List<Int>,
    removeExpenseIds: List<Int>,
    name: String
): Boolean {

    return try {

        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()

        val expenseArray = JSONArray()
        expenseIds.forEach { id ->
            expenseArray.put(id)
        }

        val removeExpenseArray = JSONArray()
        removeExpenseIds.forEach { id ->
            removeExpenseArray.put(id)
        }

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")

            put("method", "call")

            put("params", JSONObject().apply {
                put("token", token)
                put("sheet_id", sheetId)
                put("add_expense_ids", expenseArray)
                put("name", name)
                put("remove_expense_ids", removeExpenseArray)
            })
        }

        println("📦 Edit Report Body: $body")

        val response = ApiClient.httpClient.post(
            "$baseUrl/api/expenses/edit_report"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()

        println("📤 Edit Report Response: $responseText")

        val json = Json.parseToJsonElement(responseText)

        val resultObject = json
            .jsonObject["result"]
            ?.jsonObject

        val status = resultObject
            ?.get("status")
            ?.jsonPrimitive
            ?.content

        val message = resultObject
            ?.get("message")
            ?.jsonPrimitive
            ?.content

        println("Message: $message")

        status == "success"

    } catch (e: Exception) {

        println("❌ Edit Report error: ${e.message}")
        false
    }
}