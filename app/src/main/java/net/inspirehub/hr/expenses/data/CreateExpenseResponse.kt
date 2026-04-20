package net.inspirehub.hr.expenses.data

import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.lunch.data.ApiClient
import org.json.JSONObject

@Serializable
data class CreateExpenseResponse(
    val status: String,
    val message: String,
    val expense_id: Int? = null
)

suspend fun createExpense(
    context: Context,
    token: String,
    name: String,
    productId: Int,
    totalAmount: Double,
    date: String,
    description: String,
    analyticDistribution: Map<Int, Int>,
    taxIds: List<Int>,
    payment_mode: String,
    currencyId: Int,
): CreateExpenseResponse {
    return try {
        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("params", JSONObject().apply {
                put("token", token)
                put("name", name)
                put("product_id", productId)
                put("total_amount", totalAmount)
                put("currency_id", currencyId)
                put("date", date)
                put("description", description)
                put("analytic_distribution", JSONObject().apply {
                    analyticDistribution.forEach { (key, value) ->
                        put(key.toString(), value)
                    }
                })
                put("tax_ids", taxIds)
                put("payment_mode", payment_mode)
            })
        }

        println("Create Expense Request Body: $body")


        val response = ApiClient.httpClient.post("$baseUrl/api/expenses/create") {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("Create Expense Response: $responseText")

        val json = Json.parseToJsonElement(responseText).jsonObject
        val result = json["result"]?.jsonObject
        CreateExpenseResponse(
            status = result?.get("status")?.jsonPrimitive?.content ?: "error",
            message = result?.get("message")?.jsonPrimitive?.content ?: "Unknown error",
            expense_id = result?.get("expense_id")?.jsonPrimitive?.int
        ).also {
            println("Parsed CreateExpenseResponse: $it")
        }

    } catch (e: Exception) {
        println("Error creating expense: ${e.message}")
        CreateExpenseResponse(status = "error", message = e.message ?: "Unknown error")
    }
}

@Serializable
data class SubmitExpenseResponse(
    val status: String,
    val message: String
)

suspend fun submitExpense(
    context: Context,
    token: String,
    expenseId: Int
): SubmitExpenseResponse {
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

        println("Submit Expense Request Body: $body")

        val response = ApiClient.httpClient.post("$baseUrl/api/expenses/send") {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }
        println("FULL URL: $baseUrl/api/expenses/send")

        val responseText = response.bodyAsText()
        println("Submit Expense Response: $responseText")

        val json = Json.parseToJsonElement(responseText).jsonObject
        val result = json["result"]?.jsonObject
        val error = json["error"]?.jsonObject

        if (error != null) {
            val errorMessage =
                error["data"]?.jsonObject?.get("message")?.jsonPrimitive?.content
                    ?: error["message"]?.jsonPrimitive?.content
                    ?: "Unknown error"

            return SubmitExpenseResponse(
                status = "error",
                message = errorMessage
            )
        }


        SubmitExpenseResponse(
            status = result?.get("status")?.jsonPrimitive?.content ?: "error",
            message = result?.get("message")?.jsonPrimitive?.content ?: "Unknown error"
        )

    } catch (e: Exception) {
        println("Error submitting expense: ${e.message}")
        SubmitExpenseResponse("error", e.message ?: "Unknown error")
    }
}