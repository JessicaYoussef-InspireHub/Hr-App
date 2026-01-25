package net.inspirehub.hr.lunch.data

import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import net.inspirehub.hr.SharedPrefManager
import org.json.JSONObject
import kotlinx.serialization.Serializable


@Serializable
data class LunchProduct(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String?,
    val currency: String,
    val currency_symbol: String,
    val category_name: String,
    val supplier_name: String,
    val is_favorite: Boolean,
    @SerialName("category_id")
    val categoryId: Int? = null,
    @SerialName("supplier_id")
    val supplierId: Int? = null,
    @SerialName("image_base64")
    val imageBase64: String? = null,
    @SerialName("is_new")
    val isNew: Boolean

    )


@Serializable
data class LunchResponse(
    val success: Boolean,
    val products: List<LunchProduct>,
    val count: Int
)

suspend fun fetchLunchProducts(
    context: Context,
    token: String,
    categoryId: Int? = null,
    supplierId: Int? = null,
    search: String? = null,
    onlyAvailableToday: Boolean = true
): List<LunchProduct> {
    return try {
        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()
        val body = JSONObject().apply {
            put("token", token)
            categoryId?.let { put("category_id", it) }
            supplierId?.let { put("supplier_id", it) }
            search?.let { put("search", it) }
            put("only_available_today", onlyAvailableToday)
        }

        val response = ApiClient.httpClient.post("$baseUrl/api/lunch/products") {
         contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("Lunch Products Response: $responseText")

        val json = Json.parseToJsonElement(responseText)
        val success = json.jsonObject["success"]?.toString() == "true"
        if (!success) return emptyList()

        val productsJson = json.jsonObject["products"]
        Json.decodeFromJsonElement(productsJson!!)
 } catch (e: Exception) {
        println("Error fetching lunch products: ${e.message}")
        emptyList()
    }
}
