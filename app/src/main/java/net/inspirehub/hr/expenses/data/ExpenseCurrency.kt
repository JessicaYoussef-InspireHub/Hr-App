package net.inspirehub.hr.expenses.data

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonPrimitive
import net.inspirehub.hr.sign_in.data.SignInApiService

@Serializable
data class ExpenseCurrency(
    val id: Int,
    val name: String,
    val symbol: String,
    val currency_code: String,
    val position: String,
    val rounding: Double,
    val decimal_places: Int,
    val rate: Double,
    val conversion_rate: Double,
    val is_company_currency: Boolean,
    val active: Boolean
)

suspend fun fetchExpenseCurrencies(
    context: Context,
    token: String,
    retry: Boolean = true
): List<ExpenseCurrency> {

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
            "$baseUrl/api/expenses/currencies"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("Currencies Response: $responseText")

        val json = Json.parseToJsonElement(responseText).jsonObject
        val resultObj = json["result"]?.jsonObject

        val errorCode =
            resultObj?.get("error_code")?.jsonPrimitive?.content

        // ✅ TOKEN EXPIRED HANDLING
        if ((errorCode == "INVALID_TOKEN" || errorCode == "TOKEN_EXPIRED") && retry) {

            println("🔄 Token expired → renewing")

            val newTokenResponse = SignInApiService.renewToken(
                apiKey = sharedPref.getApiKey().orEmpty(),
                companyId = sharedPref.getCompanyId().orEmpty(),
                employeeToken = token
            )

            val newToken = newTokenResponse.result.new_token
            sharedPref.saveToken(newToken)

            println("✅ New token saved")

            // 🔁 retry request
            return fetchExpenseCurrencies(
                context,
                newToken,
                retry = false
            )
        }

        val currenciesJson = resultObj?.get("data")
            ?: return emptyList()

        Json.decodeFromJsonElement(currenciesJson)

    } catch (e: Exception) {
        println("Error fetching currencies: ${e.message}")
        emptyList()
    }
}