package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import net.inspirehub.hr.SharedPrefManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class PingRequest(
    @SerialName("employee_token")
    val employeeToken: String,

    val pings: List<PingItem>
)


@Serializable
data class PingItem(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val battery: Int? = null
)


@Serializable
data class BatchResponse(
    val jsonrpc: String? = null,
    val id: String? = null,
    val result: BatchResult? = null
)

@Serializable
data class BatchResult(
    val status: String? = null,
    val message: String? = null
)

@Serializable
data class LocationPingRequest(
    val employee_token: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val accuracy: Float,
    val speed: Float,
    val battery: Int
)

@Serializable
data class LocationPingResponse(
    val jsonrpc: String? = null,
    val id: String? = null,
    val result: LocationPingResult? = null
)

@Serializable
data class LocationPingResult(
    val status: String? = null,
    val message: String? = null,
    val success: Boolean? = null
)
object LocationApiService {

    suspend fun sendLocation(
        context: Context,
        employeeToken: String,
        latitude: Double,
        longitude: Double,
        accuracy: Float,
        speed: Float,
        battery: Int
    ): LocationPingResponse {

        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()

        if (!sharedPref.getIsTracked()) {

            Log.d("LOCATION_API", "Tracking disabled")

            return LocationPingResponse(
                result = LocationPingResult(
                    status = "disabled",
                    message = "Tracking disabled"
                )
            )
        }

        val request = LocationPingRequest(
            employee_token = employeeToken,
            latitude = latitude,
            longitude = longitude,
            timestamp = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.US
            ).format(Date()),

            accuracy = accuracy,
            speed = speed,
            battery = battery
        )

        Log.d("Test LOCATION_REQUEST", "Request Body: $request")

        val response = httpClient.post("$companyUrl/api/location/ping") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val raw = response.bodyAsText()

        Log.d("Test LOCATION_REQUEST", "Response: $raw")

        return try {
            response.body()
        } catch (e: Exception) {

            Log.e("Test LOCATION_REQUEST", "Parse Error: ${e.message}")

            throw e
        }
    }


    suspend fun sendBatchLocations(
        context: Context,
        employeeToken: String,
        pings: List<PingItem>
    ): BatchResponse {

        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()

        if (!sharedPref.getIsTracked()) {

            Log.d("TEST_BATCH", "Tracking disabled")

            return BatchResponse(
                result = BatchResult(
                    status = "disabled",
                    message = "Tracking disabled"
                )
            )
        }

        val request = PingRequest(
            employeeToken = employeeToken,
            pings = pings
        )

        Log.d("TEST_BATCH", "Sending ${pings.size} locations")

        Log.d("TEST_BATCH", request.toString())

        val response = httpClient.post("$companyUrl/api/location/ping/batch") {

            contentType(ContentType.Application.Json)

            setBody(request)
        }

        val raw = response.bodyAsText()

        Log.d("TEST_BATCH_RESPONSE", raw)
        val json = Json { ignoreUnknownKeys = true }

        return try {

            json.decodeFromString<BatchResponse>(raw)

        } catch (e: Exception) {
            Log.e("TEST_BATCH", "Parse Error = ${e.message}")

            throw e
        }
    }
}