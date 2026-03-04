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
data class ExpenseCategory(
    val id: Int,
    val name: String,
    val category: String,
    val category_id: Int,
    val description: String?,
    val default_code: String?,
    val uom: String?
)

suspend fun fetchExpenseCategories(
    context: Context, token: String): List<ExpenseCategory> {
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
            "$baseUrl/api/expenses/categories"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("Categories Response: $responseText")

        val json = Json.parseToJsonElement(responseText)
        val categoriesJson = json.jsonObject["result"]?.jsonObject?.get("data")
        if (categoriesJson == null) return emptyList()

        Json.decodeFromJsonElement(categoriesJson)

    } catch (e: Exception) {
        println("Error fetching categories: ${e.message}")
        emptyList()
    }
}