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

data class DeletedItem(
    val id: Int,
    val name: String
)
data class DeleteReportResult(
    val success: Boolean,
    val message: String? = null,
    val deleted: List<DeletedItem>? = null,
    val failed: List<FailedItem>? = null
)

data class FailedItem(
    val id: Int,
    val reason: String
)

suspend fun deleteReport(
    context: Context,
    token: String,
    sheetIds: List<Int>
): DeleteReportResult {

    return try {
        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()
        val jsonArray = org.json.JSONArray()
        sheetIds.forEach { id ->
            jsonArray.put(id)
        }

        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", "call")
            put("params", JSONObject().apply {
                put("sheet_ids", jsonArray) // 👈 list
                put("token", token)
            })
        }

        val response = ApiClient.httpClient.post(
            "$baseUrl/api/expenses/delete_report"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("📦 DELETE REPORT RESPONSE = $responseText")

        val json = JSONObject(responseText)
        val result = json.getJSONObject("result")

        val status = result.getString("status")
        val message = result.optString("message")

        val deletedList = mutableListOf<DeletedItem>()
        val failedList = mutableListOf<FailedItem>()

        if (result.has("deleted")) {
            val deletedArray = result.getJSONArray("deleted")
            for (i in 0 until deletedArray.length()) {
                val obj = deletedArray.getJSONObject(i)
                deletedList.add(
                    DeletedItem(
                        id = obj.getInt("id"),
                        name = obj.optString("name")
                    )
                )
            }
        }

        if (result.has("failed")) {
            val failedArray = result.getJSONArray("failed")
            for (i in 0 until failedArray.length()) {
                val obj = failedArray.getJSONObject(i)
                failedList.add(
                    FailedItem(
                        id = obj.getInt("id"),
                        reason = obj.getString("reason")
                    )
                )
            }
        }

        if (status == "success") {
            DeleteReportResult(
                success = true,
                message = message,
                deleted = deletedList,
                failed = failedList
            )
        } else {
            DeleteReportResult(
                success = false,
                message = message,
                deleted = deletedList,
                failed = failedList
            )
        }

    } catch (e: Exception) {
        DeleteReportResult(false, e.message)
    }
}