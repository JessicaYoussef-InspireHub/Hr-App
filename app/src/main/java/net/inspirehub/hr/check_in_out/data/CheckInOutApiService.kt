package net.inspirehub.hr.check_in_out.data

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.serialization.json.jsonObject
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.scan_qr_code.data.AppConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

@Serializable
data class AttendanceStatusResponseWrapper(
    val result: AttendanceStatusResult
)

data class CompanyLocation(
    val id: Int,
    val name: String,
    val lat: Double,
    val lng: Double,
    val allowedDistance: Double
)

@Serializable
data class AttendanceStatusResult(
    val status: String,
    val message: String,
    val attendance_status: String? = null,
    val worked_hours: Double? = null,

    @SerialName("check_in_time")
    val checkInTime: String? = null,

    @SerialName("last_check_in")
    val lastCheckIn: String? = null,

    @SerialName("check_out_time")
    val checkOutTime: String? = null,

    @SerialName("last_check_out")
    val lastCheckOut: String? = null
)

val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

suspend fun fetchServerTime(
    token: String,
    context: Context
): String? {
    return try {
        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()
        val response: HttpResponse =
            httpClient.post("$companyUrl/api/employee_attendance") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "params" to mapOf(
                            "employee_token" to token,
                            "action" to "server_time"
                        )
                    )
                )
            }

        val bodyText = response.bodyAsText()
        println("🕒 Server Time Response: $bodyText")

        val json = Json.parseToJsonElement(bodyText).jsonObject
        val result = json["result"]?.jsonObject
        val serverTime = result?.get("server_time")?.toString()?.replace("\"", "")

        println("✅ Extracted server_time: $serverTime")

        serverTime
    } catch (e: Exception) {
        println("🔴 Error fetching server time: ${e.message}")
        null
    }
}

fun sendOfflineAttendanceAction(
    context:  Context,
    token: String, logs: List<Map<String, Any>>) {
    try {
        println("🟡 [1] ENTERED sendOfflineAttendanceAction()")
        println("🟡 [2] Token received: $token")
        println("🟡 [3] Logs count: ${logs.size}")

        logs.forEachIndexed { index, log ->
            println("🟢 [4.$index] Log #$index contents: $log")
        }

        // ✅ تحويل logs إلى JSONArray
        println("🟡 [5] Converting logs to JSONArray...")
        val jsonLogs = JSONArray()
        logs.forEachIndexed { i, log ->
            println("🔹 [5.$i] Converting log #$i ...")
            val obj = JSONObject()
            obj.put("action", log["action"])
            obj.put("lat", log["lat"])
            obj.put("lng", log["lng"])
            obj.put("action_time", log["action_time"])
            obj.put("action_tz", log["action_tz"])
            jsonLogs.put(obj)
        }
        println("✅ [6] JSONArray built successfully: $jsonLogs")

        // ✅ بناء الـ payload
        println("🟡 [7] Building final payload...")
        val payload = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", "call")
            put("params", JSONObject().apply {
                put("employee_token", token)
                put("attendance_logs", jsonLogs)
            })
            put("id", 0)
        }

        println("📦 [8] Final payload ready to send:\n$payload")

        // ✅ إعداد الريكويست باستخدام OkHttp
        println("🟡 [9] Preparing OkHttp client...")
        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = payload.toString().toRequestBody(mediaType)
        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()

        val request = Request.Builder()
            .url("$companyUrl/api/offline_attendance")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        println("🚀 [10] Sending request to server...")

        val response = client.newCall(request).execute()

        println("🟢 [11] Got response from server!")
        println("🟢 [12] Response Code: ${response.code}")
        val responseBody = response.body?.string()
        println("🟢 [13] Response Body: $responseBody")

        response.close()
        println("✅ [14] Request completed and response closed.")

    } catch (e: Exception) {
        println("🔴 [ERR] Exception in sendOfflineAttendanceAction: ${e.message}")
        e.printStackTrace()
    }
}


// fun sendOfflineAttendanceAction(token: String, logs: List<Map<String, Any>>) {
//    try {
//        println("test jessy 🟡 Preparing to send offline attendance...")
//        println("test jessy 🟡 Token: $token")
//        println("test jessy 🟡 Logs count: ${logs.size}")
//
//        logs.forEachIndexed { index, log ->
//            println("test jessy 🟢 Log #$index details: $log")
//        }
//
//        // ✅ تحويل logs إلى JSONArray
//        val jsonLogs = JSONArray()
//        logs.forEach { log ->
//            val obj = JSONObject()
//            obj.put("action", log["action"])
//            obj.put("lat", log["lat"])
//            obj.put("lng", log["lng"])
//            obj.put("action_time", log["action_time"])
//            obj.put("action_tz", log["action_tz"])
//            jsonLogs.put(obj)
//        }
//
//        // ✅ بناء الـ payload
//        val payload = JSONObject().apply {
//            put("jsonrpc", "2.0")
//            put("method", "call")
//            put("params", JSONObject().apply {
//                put("employee_token", token)
//                put("attendance_logs", jsonLogs)
//            })
//            put("id", 0)
//        }
//
//        println("test jessy 📦 Final payload before sending:\n$payload")
//
//        // ✅ إعداد الريكويست باستخدام OkHttp
//        val client = OkHttpClient()
//        val mediaType = "application/json; charset=utf-8".toMediaType()
//        val body = payload.toString().toRequestBody(mediaType)
//
//        val request = Request.Builder()
//            .url(AppConfig.baseUrl + "/api/offline_attendance")
//            .post(body)
//            .addHeader("Content-Type", "application/json")
//            .build()
//
//        println("test jessy 🚀 Sending request to server...")
//
//        val response = client.newCall(request).execute()
//        val responseBody = response.body?.string()
//
//        println("test jessy 🟢 Response Code: ${response.code}")
//        println("test jessy 🟢 Response Body: $responseBody")
//
//        response.close()
//    } catch (e: Exception) {
//        println("test jessy 🔴 Exception in sendOfflineAttendanceAction: ${e.message}")
//        e.printStackTrace()
//    }
//}


suspend fun sendAttendanceAction(
    context: Context,
    token: String,
    action: String,
    latitude: String,
    longitude: String,
    actionTime: String? = null
): AttendanceStatusResult? {
    return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val currentTime = actionTime ?: utcFormat.format(Date())
        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()

        val response: HttpResponse =
            httpClient.post("$companyUrl/api/employee_attendance") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "params" to mapOf(
                            "employee_token" to token,
                            "action" to action,
                            "lat" to latitude,
                            "lng" to longitude,
                            "action_time" to currentTime
                        )
                    )
                )
            }

        println("Status: ${response.status}")
        println("Headers: ${response.headers}")
        println("Body: ${response.bodyAsText()}")

        val responseBody = response.body<AttendanceStatusResponseWrapper>()
        println("⚪ Server response: $responseBody")
        responseBody.result

    } catch (e: Exception) {
        println("🔴 Exception: ${e.message}")
        null
    }
}


suspend fun fetchAttendanceStatus(
    context: Context,
    token: String): AttendanceStatusResult? {
    return try {
        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()
        val response: HttpResponse =
            httpClient.post("$companyUrl/api/employee_attendance") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "params" to mapOf(
                            "employee_token" to token,
                            "action" to "status"
                        )
                    )
                )
            }

        println("Status: ${response.status}")
        println("Headers: ${response.headers}")
        println("Body: ${response.bodyAsText()}")

        val responseBody = response.body<AttendanceStatusResponseWrapper>()
        println("⚪ Server response (status): $responseBody")

        val result = responseBody.result

        // ❌ لو السيرفر رجع Error → نوقف هنا
        if (result.status.equals("Error", ignoreCase = true)) {
            println("❌ Server Error: ${result.message}")
            return null
        }

        // ✅ نجاح
        result

    } catch (e: Exception) {
        println("🔴 Exception fetching status: ${e.message}")
        null
    }
}