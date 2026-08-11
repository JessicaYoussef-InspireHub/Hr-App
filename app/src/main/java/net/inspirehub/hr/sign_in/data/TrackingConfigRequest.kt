package net.inspirehub.hr.sign_in.data

import android.content.Context
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import android.util.Log
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.check_in_out.data.httpClient

@Serializable
data class TrackingConfigRequest(
    @SerialName("employee_token")
    val employeeToken: String
)

@Serializable
data class TrackingConfigResponse(
    val jsonrpc: String? = null,
    val id: String? = null,
    val result: TrackingConfigResult
)

 val json = Json { ignoreUnknownKeys = true
    isLenient = true
}

@Serializable
data class TrackingConfigResult(
    val is_tracked: Boolean,
    val working_hours_only: Boolean,
    val tracking_interval_minutes: Float,
    val min_distance_meters: Float
)

suspend fun getTrackingConfig(
    context: Context,
    employeeToken: String
): TrackingConfigResponse {

    val payload = TrackingConfigRequest(
        employeeToken = employeeToken
    )

    val sharedPref = SharedPrefManager(context)
    val companyUrl = sharedPref.getCompanyUrl()
    val url = "$companyUrl/api/location/tracking_config"

    Log.d("test TRACKING_API", "URL = $url")
    Log.d("testTRACKING_API", "TOKEN = $employeeToken")

    val response = httpClient.post(url) {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
        setBody(payload)
    }

    val responseBody: String = response.body()

    Log.d("test TRACKING_API", "STATUS = ${response.status}")
    Log.d("test TRACKING_API", "CONTENT_TYPE = ${response.headers[HttpHeaders.ContentType]}")
    Log.d("test TRACKING_API", "BODY = $responseBody")

    if (!response.status.isSuccess()) {
        throw Exception(
            "test Tracking config API failed: ${response.status} - $responseBody"
        )
    }

    return json.decodeFromString(responseBody)
}