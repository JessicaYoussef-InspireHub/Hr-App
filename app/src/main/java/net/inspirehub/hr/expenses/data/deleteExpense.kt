package net.inspirehub.hr.expenses.data

import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.lunch.data.ApiClient
import org.json.JSONObject

data class DeleteExpenseResult(
    val success: Boolean,
    val reason: String? = null
)

suspend fun deleteExpense(
    context: Context,
    token: String,
    expenseId: Int
): DeleteExpenseResult {

    return try {

        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", "call")
            put("params", JSONObject().apply {
                put("expense_id", expenseId)
                put("token", token)
            })
        }

        val response = ApiClient.httpClient.post(
            "$baseUrl/api/expenses/delete"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("📦 DELETE RESPONSE = $responseText")

        val json = JSONObject(responseText)
        val result = json.getJSONObject("result")

        val failedArray = result.getJSONArray("failed")

        // ✅ If there is no "failed" message, then it has been deleted.
        if (failedArray.length() == 0) {
            DeleteExpenseResult(success = true)
        } else {
            val reason =
                failedArray.getJSONObject(0).getString("reason")

            DeleteExpenseResult(
                success = false,
                reason = reason
            )
        }

    } catch (e: Exception) {
        DeleteExpenseResult(
            success = false,
            reason = e.message
        )
    }
}