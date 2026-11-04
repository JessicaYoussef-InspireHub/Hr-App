package net.inspirehub.hr.deduction.data

import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.lunch.data.ApiClient
import org.json.JSONObject

@Serializable
data class EmployeesDeductionResponse(
    val status: String,
    val data: EmployeesDeductionData
)

@Serializable
data class EmployeesDeductionData(
    val requested_period: CurrentMonthDeduction,
    val last_month: PreviousMonthDeduction
)

@Serializable
data class CurrentMonthDeduction(
    val from_date: String,
    val to_date: String,
    val allowed_duration_per_month: Int,
    val total_deduction_minutes: Int,
    val remaining_deduction_time: Int,
    val excess_deduction_minutes: Int,
    val deduction_percentage: Double,
    val deduction_color: String,
    val deduction_message: String
)



@Serializable
data class PreviousMonthDeduction(
    val from_date: String,
    val to_date: String,
    val allowed_duration_per_month: Int,
    val total_deduction_minutes: Int,
    val remaining_deduction_time: Int,
    val excess_deduction_minutes: Int,
    val deduction_percentage: Double,
    val deduction_color: String,
    val deduction_message: String
)

suspend fun fetchEmployeesDeduction(
    token: String?,
    context: Context,
    fromDate: String,
    toDate: String
): EmployeesDeductionResponse? {
    return try {
        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put(
                "params",
                JSONObject().apply {
                    put("token", token)
                    put("from_date", fromDate)
                    put("to_date", toDate)
                }
            )
        }

        println("test EmployeesDeduction Request: $body")

        val response = ApiClient.httpClient.post(
            "$baseUrl/api/employees_deduction"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("test EmployeesDeduction Response: $responseText")

        val json = Json.parseToJsonElement(responseText)
        val resultJson = json.jsonObject["result"]?.jsonObject

        if (
            resultJson?.get("status")?.jsonPrimitive?.contentOrNull != "success"
        ) {
            println(
                "test EmployeesDeduction Error: " +
                        (resultJson?.get("message")?.jsonPrimitive?.contentOrNull
                            ?: "Unknown error")
            )
            return null
        }

        val dataJson = resultJson["data"] ?: return null

        Json.decodeFromJsonElement<EmployeesDeductionResponse>(
            Json.parseToJsonElement(
                JSONObject()
                    .put("status", "success")
                    .put("data", JSONObject(dataJson.toString()))
                    .toString()
            )
        )
    } catch (e: Exception) {
        println("test Error fetching employees deduction: ${e.message}")
        null
    }
}