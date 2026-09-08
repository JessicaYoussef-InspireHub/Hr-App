package net.inspirehub.hr.check_in_out.data

import android.content.Context
import io.ktor.client.HttpClient
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
import kotlinx.serialization.json.jsonPrimitive
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.sign_in.data.SignInApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

private var hasPrintedServerResponse = false

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
    val status: String? = null,
    val message: String? = null,
    val attendance_status: String? = null,
    val worked_hours: Double? = null,

    @SerialName("check_in_time")
    val checkInTime: String? = null,

    @SerialName("last_check_in")
    val lastCheckIn: String? = null,

    @SerialName("check_out_time")
    val checkOutTime: String? = null,

    @SerialName("last_check_out")
    val lastCheckOut: String? = null,

    @SerialName("today_scheduled_hours")
    val todayScheduledHours: Double? = null
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
    context: Context,
    token: String,
    logs: List<Map<String, Any>>
): Boolean {

    return try {
        println("🟡 [1] ENTERED sendOfflineAttendanceAction()")
        println("🟡 [2] Token received: $token")
        println("🟡 [3] Logs count: ${logs.size}")

        if (logs.isEmpty()) {
            println("⚠️ No logs to send")
            return true
        }

        val jsonLogs = JSONArray()

        logs.forEachIndexed { index, log ->

            println("🟢 [4.$index] Log = $log")

            val obj = JSONObject()

            obj.put("action", log["action"])
            obj.put("lat", log["lat"])
            obj.put("lng", log["lng"])
            obj.put("action_time", log["action_time"])
            obj.put("action_tz", log["action_tz"])

            jsonLogs.put(obj)
        }

        val payload = JSONObject().apply {

            put("jsonrpc", "2.0")
            put("method", "call")

            put(
                "params",
                JSONObject().apply {
                    put("employee_token", token)
                    put("attendance_logs", jsonLogs)
                }
            )

            put("id", 0)
        }

        println("📦 Offline payload:")
        println(payload)

        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()

        val client = OkHttpClient()

        val mediaType =
            "application/json; charset=utf-8".toMediaType()

        val body =
            payload.toString().toRequestBody(mediaType)

        val request =
            Request.Builder()
                .url("$companyUrl/api/offline_attendance")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()

        println("🚀 Sending offline attendance...")

        client.newCall(request).execute().use { response ->

            val responseBody = response.body?.string()

            println("🟢 HTTP Code = ${response.code}")
            println("🟢 Response = $responseBody")

            if (!response.isSuccessful) {

                println(
                    "❌ HTTP request failed: ${response.code}"
                )

                return false
            }

            if (responseBody.isNullOrBlank()) {

                println("❌ Empty server response")

                return false
            }

            return try {

                val json =
                    JSONObject(responseBody)

                val result =
                    json.optJSONObject("result")

                val status =
                    result?.optString("status")

                println(
                    "📦 Offline server status = $status"
                )

                if (
                    status.equals(
                        "Error",
                        ignoreCase = true
                    )
                ) {

                    println(
                        "❌ Server rejected offline attendance: " +
                                result?.optString("message")
                    )

                    false

                } else {

                    println(
                        "✅ Offline attendance accepted by server"
                    )

                    true
                }

            } catch (e: Exception) {

                println(
                    "❌ Failed parsing offline response: ${e.message}"
                )

                false
            }
        }

    } catch (e: Exception) {

        println(
            "🔴 Exception in sendOfflineAttendanceAction: " +
                    e.message
        )

        e.printStackTrace()

        false
    }
}



suspend fun sendAttendanceAction(
    context: Context,
    token: String,
    action: String,
    latitude: String,
    longitude: String,
    actionTime: String? = null,
    retry: Boolean = true
): AttendanceStatusResult? {
    return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val currentTime = actionTime ?: utcFormat.format(Date())

        val sharedPref = SharedPrefManager(context)
        val companyUrl = sharedPref.getCompanyUrl()

        val response: HttpResponse = httpClient.post("$companyUrl/api/employee_attendance") {
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

        val responseText = response.bodyAsText()

        if (!hasPrintedServerResponse) {
            println("🟢 Server Response: $responseText")
            hasPrintedServerResponse = true
        }

        val json = Json.parseToJsonElement(responseText).jsonObject
        val resultObj = json["result"]?.jsonObject
        val status = resultObj?.get("status")?.jsonPrimitive?.content
        val errorCode = resultObj?.get("error_code")?.jsonPrimitive?.content

        // ✅ تحقق من انتهاء التوكن
        if ((errorCode == "INVALID_TOKEN" || errorCode == "TOKEN_EXPIRED") && retry) {
            println("🔄 Token expired, renewing…")

            val apiKey = sharedPref.getApiKey()
            val companyId = sharedPref.getCompanyId()

            val newTokenResponse = SignInApiService.renewToken(
                apiKey = apiKey.orEmpty(),
                companyId = companyId.orEmpty(),
                employeeToken = token
            )

            val newToken = newTokenResponse.result.new_token
            sharedPref.saveToken(newToken)
            println("✅ New token generated: $newToken")

            // إعادة إرسال نفس الطلب بالتوكن الجديد
            return sendAttendanceAction(
                context,
                newToken,
                action,
                latitude,
                longitude,
                actionTime,
                retry = false
            )
        }

        // ❌ لو السيرفر رجع Error → نوقف هنا
        if (status.equals("Error", ignoreCase = true)) {
            println("❌ Server Error: ${resultObj?.get("message")?.jsonPrimitive?.content}")
            val serverMessage = resultObj?.get("message")?.jsonPrimitive?.content

            return AttendanceStatusResult(

            status = status,
            message = serverMessage ?: "Unknown error",
                attendance_status = null,
                checkInTime = null,
                lastCheckIn = null,
                checkOutTime = null,
                lastCheckOut = null,
                worked_hours = null
            )
        }

        val jsonParser = Json { ignoreUnknownKeys = true }

        // ✅ تحويل النص إلى الـ data class
        val responseBody = jsonParser.decodeFromString(
            AttendanceStatusResponseWrapper.serializer(),
            responseText
        )
        responseBody.result

    } catch (e: Exception) {
        println("🔴 Exception in sendAttendanceAction: ${e.message}")
        AttendanceStatusResult(
            status = "error",
            message = e.message ?: "Unknown exception",
            attendance_status = null,
            checkInTime = null,
            lastCheckIn = null,
            checkOutTime = null,
            lastCheckOut = null,
            worked_hours = null
        )
    }
}


suspend fun fetchAttendanceStatus(
    context: Context,
    token: String,
    retry: Boolean = true
): AttendanceStatusResult? {
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

        val responseText = response.bodyAsText()
        if (!hasPrintedServerResponse) {
            println("🟢 Server Response: $responseText")
            hasPrintedServerResponse = true
        }

        val json = Json.parseToJsonElement(responseText).jsonObject
        val resultObj = json["result"]?.jsonObject
        val status = resultObj?.get("status")?.jsonPrimitive?.content
        val errorCode = resultObj?.get("error_code")?.jsonPrimitive?.content

        if ((errorCode == "INVALID_TOKEN" || errorCode == "TOKEN_EXPIRED") && retry) {
            println("🔄 Token expired, renewing…")

            val apiKey = sharedPref.getApiKey()
            val companyId = sharedPref.getCompanyId()

            val newTokenResponse = SignInApiService.renewToken(
                apiKey = apiKey.orEmpty(),
                companyId = companyId.orEmpty(),
                employeeToken = token
            )

            val newToken = newTokenResponse.result.new_token
            sharedPref.saveToken(newToken)
            println("✅ New token generated: $newToken")

            return fetchAttendanceStatus(context, newToken, retry = false)
        }

        if (status.equals("Error", ignoreCase = true)) {
            println("❌ Server Error: ${resultObj?.get("message")?.jsonPrimitive?.content}")
            return null
        }

        val jsonParser = Json {
            ignoreUnknownKeys = true
        }

        val responseBody = jsonParser.decodeFromString(
            AttendanceStatusResponseWrapper.serializer(),
            responseText
        )
        responseBody.result


    } catch (e: Exception) {
        println("🔴 Exception fetching status: ${e.message}")
        null
    }
}