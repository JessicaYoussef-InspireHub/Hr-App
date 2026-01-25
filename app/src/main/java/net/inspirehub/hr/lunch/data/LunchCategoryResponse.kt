package net.inspirehub.hr.lunch.data

import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import net.inspirehub.hr.SharedPrefManager
import org.json.JSONObject

@Serializable
data class LunchCategory(
    val id: Int,
    val name: String,
    @SerialName("product_count")
    val productCount: Int
)

@Serializable
data class LunchCategoryResponse(
    val success: Boolean,
    val categories: List<LunchCategory>,
    val count: Int
)

suspend fun fetchLunchCategories(
    context: Context,
    token: String
): List<LunchCategory> {
    return try {
        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()

        val body = JSONObject().apply {
            put("token", token)
        }

        val response = ApiClient.httpClient.post("$baseUrl/api/lunch/categories") {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("Lunch Categories Response: $responseText")

        val json = Json.parseToJsonElement(responseText)
        val success = json.jsonObject["success"]?.toString() == "true"
        if (!success) return emptyList()

        val categoriesJson = json.jsonObject["categories"]
        Json.decodeFromJsonElement(categoriesJson!!)
    }
    catch (e: Exception) {
        println("Error fetching lunch categories: ${e.message}")
        emptyList()
    }
}