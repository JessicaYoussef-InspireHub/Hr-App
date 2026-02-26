package net.inspirehub.hr.lunch.data

import android.content.Context
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.inspirehub.hr.SharedPrefManager


@Serializable
data class OrderRequest(
    val token: String,
    val orders: List<OrderItemRequest>
)

@Serializable
data class OrderItemRequest(
    @SerialName("product_id")
    val productId: Int,
    val quantity: Int,
    @SerialName("total_price")
    val totalPrice: Double
)

suspend fun submitLunchOrder(
    context: Context,
    cartItems: List<CartItem>
): Boolean {
    return try {
        val sharedPref = SharedPrefManager(context)
        val baseUrl = sharedPref.getCompanyUrl() ?: return false
        val token = sharedPref.getToken() ?: return false

        val orders = cartItems
            .map {
                OrderItemRequest(
                    productId = it.productId,
                    quantity = it.quantity,
                    totalPrice = it.price * it.quantity
                )
            }

        val requestBody = OrderRequest(
            token = token,
            orders = orders
        )

        val response = ApiClient.httpClient.post("$baseUrl/api/lunch/orders") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        val responseText = response.bodyAsText()
        println("Order Response: $responseText")

        true
    } catch (e: Exception) {
        println("Error submitting order: ${e.message}")
        false
    }
}