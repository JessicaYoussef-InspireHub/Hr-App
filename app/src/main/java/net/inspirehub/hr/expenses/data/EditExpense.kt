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

suspend fun editExpense(
    context: Context,
    token: String,
    expenseId: Int,
    name: String,
    totalAmount: Double,
    date: String,
    productId: Int,
    description: String,
    currencyId: Int,
    paymentMode: String,
    taxIds: List<Int>,
    analyticDistribution: Map<String, Int>,
    isRetry: Boolean = false
): Boolean {

    return try {

        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", "call")
            put("params", JSONObject().apply {
                put("token", token)
                put("expense_id", expenseId)
                put("name", name)
                put("total_amount", totalAmount)
                put("date", date)
                put("product_id", productId)
                put("description", description)
                put("currency_id", currencyId)
                put("payment_mode", paymentMode)
                put("tax_ids", taxIds)
                put("analytic_distribution", JSONObject(analyticDistribution))
            })
        }

        val response = ApiClient.httpClient.post(
            "$baseUrl/api/expenses/edit"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("✏️ Edit Expense Response: $responseText")

        val json = Json.parseToJsonElement(responseText)
        val resultObject = json.jsonObject["result"]?.jsonObject

        val errorCode = resultObject?.get("error_code")?.jsonPrimitive?.content

        // 🔁 Token expired
        if (errorCode == "INVALID_TOKEN" && !isRetry) {
            val newToken = renewTokenAndSave(context)
            return if (newToken != null) {
                editExpense(
                    context,
                    newToken,
                    expenseId,
                    name,
                    totalAmount,
                    date,
                    productId,
                    description,
                    currencyId,
                    paymentMode,
                    taxIds,
                    analyticDistribution,
                    true
                )
            } else false
        }

        // ✅ success
        errorCode == null

    } catch (e: Exception) {
        println("❌ Edit expense error: ${e.message}")
        false
    }
}