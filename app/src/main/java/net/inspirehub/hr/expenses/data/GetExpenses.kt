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
import kotlinx.serialization.json.jsonPrimitive
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.lunch.data.ApiClient
import net.inspirehub.hr.sign_in.data.SignInApiService
import org.json.JSONObject

@Serializable
data class ExpenseTax(
    val id: Int,
    val name: String,
    val amount: Double,
    val amount_type: String
)
@Serializable
data class Expense(
    val id: Int,
    val name: String,
    val employee: String,
    val employee_id: Int,
    val company: String,
    val company_id: Int,
    val product: String,
    val product_id: Int,
    val total_amount: Double,
    val currency_id: Int?,
    val currency: String,
    val currency_symbol: String? = null,
    val currency_position: String? = null,
    val date: String,
    val state: String,
    val sheet_id: Int? = null,
    val sheet_name: String? = null,
    val description: String,
    val payment_mode: String? = null,
    val taxes: List<ExpenseTax> = emptyList(),
    val tax_total_percentage: Double? = null,
    val total_with_tax: Double? = null,
    val tax_amount: Double? = null,
    val tax_id: Int? = null,
    val draft_total_amount: Double? = null,
    val analytic_distribution: Map<String, Double> = emptyMap()
)


suspend fun fetchExpenses(
    context: Context,
    token: String,
    isRetry: Boolean = false
): List<Expense> {

    return try {

        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()
        val pref = SharedPrefManager(context)
        val savedToken = pref.getToken()
        val finalToken = if (savedToken.isNullOrEmpty()) token else savedToken

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("params", JSONObject().apply {
                put("token", finalToken)
            })
        }

        val response = ApiClient.httpClient.post(
            "$baseUrl/api/expenses/get"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("📦 RAW Expenses Response: $responseText")

        val json = Json.parseToJsonElement(responseText)
        val resultObject = json.jsonObject["result"]?.jsonObject

        // ✅ CHECK TOKEN ERROR
        val errorCode =
            resultObject?.get("error_code")?.jsonPrimitive?.content

        if (errorCode == "INVALID_TOKEN" && !isRetry) {

            println("🔄 Token expired → renewing...")

            val newToken = renewTokenAndSave(context)

            if (newToken != null) {
                return fetchExpenses(
                    context,
                    newToken,
                    isRetry = true
                )
            } else {
                println("❌ Unable to renew token")
                return emptyList()
            }
        }

        val dataJson = resultObject?.get("data")

        if (dataJson == null) {
            println("❌ data is null")
            return emptyList()
        }

        Json { ignoreUnknownKeys = true }
            .decodeFromJsonElement<List<Expense>>(dataJson)

    } catch (e: Exception) {
        println("❌ Error fetching expenses: ${e.message}")
        emptyList()
    }
}

suspend fun renewTokenAndSave(context: Context): String? {

    return try {

        val pref = SharedPrefManager(context)

        val apiKey = pref.getApiKey()
        val companyId = pref.getCompanyId()
        val employeeToken = pref.getToken()

        // ✅ safety check
        if (apiKey.isNullOrEmpty()
            || companyId.isNullOrEmpty()
            || employeeToken.isNullOrEmpty()
        ) {
            println("❌ Missing renew token data")
            return null
        }

        val response = SignInApiService.renewToken(
            apiKey = apiKey,
            companyId = companyId,
            employeeToken = employeeToken
        )

        val newToken = response.result.new_token

        pref.saveToken(newToken)

        println("✅ Token renewed successfully")

        newToken

    } catch (e: Exception) {
        println("❌ Renew token failed: ${e.message}")
        null
    }
}