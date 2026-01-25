package net.inspirehub.hr.lunch.data

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import org.json.JSONObject
import net.inspirehub.hr.SharedPrefManager

object ApiClient {
    val httpClient = HttpClient {
        install(ContentNegotiation){
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }
}


@Serializable
data class Supplier(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val city: String?,
    @SerialName("zip_code")
    val zipCode: String?

    )

@Serializable
data class SuppliersResponse(
    val success: Boolean,
    val suppliers: List<Supplier>,
    val count: Int
)


suspend fun fetchSuppliers(
    context: Context,
    token: String,
    locationId: Int? = null,
): List<Supplier> {
    return try {
        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl()
        val body = JSONObject().apply {
            put("token", token)
            locationId?.let { put("location_id", it) }
        }

        val response: HttpResponse = ApiClient.httpClient.post("$baseUrl/api/lunch/suppliers"){
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val responseText = response.bodyAsText()
        println("Suppliers Response: $responseText")

        val json = Json.parseToJsonElement(responseText)
        val success = json.jsonObject["success"]?.toString() == "true"
        if (!success) return emptyList()

        val suppliersJson = json.jsonObject["suppliers"]
        Json.decodeFromJsonElement(suppliersJson!!)
    } catch (e: Exception) {
        println("Error fetching suppliers: ${e.message}")
        emptyList()
    }
}