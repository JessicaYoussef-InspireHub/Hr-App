package net.inspirehub.hr.sign_in.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import net.inspirehub.hr.scan_qr_code.data.AppConfig


object SignInApiService {

    private val httpClient = HttpClient {
        install(Logging) { level = LogLevel.ALL }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        followRedirects = true
    }

    suspend fun sendDeviceToken(employeeToken: String, mobileToken: String) {
        val payload = mapOf(
            "employee_token" to employeeToken,
            "mobile_token" to mobileToken
        )

        try {
            val response: HttpResponse = httpClient.post(AppConfig.baseUrl + "/api/mobile_token") {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }

            val responseBody: String = response.body()
            Log.d("DEVICE_TOKEN", "Response: $responseBody")
        } catch (e: Exception) {
            Log.e("DEVICE_TOKEN", "Failed to send device token: ${e.message}")
        }
    }




    suspend fun renewToken(
        apiKey: String,
        companyId: String,
        employeeToken: String
    ): RenewTokenResponse {
        val payload = RenewTokenRequest(
            api_key = apiKey,
            company_id = companyId,
            employee_token = employeeToken
        )

        return try {
            val response: HttpResponse = httpClient.post(
                AppConfig.baseUrl + "/api/employee/renew_token"
            ) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(payload)
            }

            val responseBody: String = response.body()
            Log.d("HTTP", "Raw RenewToken Response: $responseBody")

            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }.decodeFromString(responseBody)

//            Json.decodeFromString(responseBody)
        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception in renewToken: ${e.message}", e)
            throw e
        }
    }

//    suspend fun signIn(
//        email: String,
//        password: String,
//        companyId: String,
//        apiKey: String
//    ): SignInResponseWrapper {
//        val payload = SignInRequest(
//            email = email,
//            password = password,
//            company_id = companyId,
//            api_key = apiKey
//        )
//
//        return try {
//            val response: HttpResponse = httpClient.post(AppConfig.baseUrl + "/api/validate_company") {
//                contentType(ContentType.Application.Json)
//                accept(ContentType.Application.Json)
//                setBody(payload)
//            }
//
//            val responseBody: String = response.body()
//            Log.d("HTTP", "Raw Response: $responseBody")
//
//            // نفحص status قبل ال decode الكامل
//            val jsonElement = Json.parseToJsonElement(responseBody).jsonObject
//            val resultElement = jsonElement["result"]!!.jsonObject
//            val status = resultElement["status"]!!.jsonPrimitive.content
//
//            return if (status == "error") {
//                // لو خطأ، نرجع SignInResponseWrapper مع رسالة الخطأ
//                val errorMsg = resultElement["message"]!!.jsonPrimitive.content
//                SignInResponseWrapper(
//                    jsonrpc = jsonElement["jsonrpc"]!!.jsonPrimitive.content,
//                    id = jsonElement["id"]?.jsonPrimitive?.content,
//                    result = SignInResult(
//                        status = "error",
//                        message = null, // message هنا مش هتستخدم لأنها error
//                        company_name = null,
//                        license_expiry_date = null,
//                        company_url = null
//                    )
//                )
//            } else {
//                // لو success نعمل decode كامل
//                Json {
//                    ignoreUnknownKeys = true
//                    isLenient = true
//                }.decodeFromString<SignInResponseWrapper>(responseBody)
//            }
//
//        } catch (e: Exception) {
//            Log.e("API_ERROR", "Exception: ${e.message}", e)
//            throw e
//        }
//    }

    suspend fun signIn(
        email: String,
        password: String,
        companyId: String,
        apiKey: String
    ): SignInResponseWrapper {
        val payload = SignInRequest(
            email = email,
            password = password,
            company_id = companyId,
            api_key = apiKey
        )

        return try {
            val response: HttpResponse = httpClient.post(
                AppConfig.baseUrl + "/api/validate_company"
            ){
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(payload)
            }

            val responseBody: String = response.body()
            Log.d("HTTP", "Raw Response: $responseBody")

            // Parse JSON manually للتحقق من status قبل Serialization
            val jsonElement = Json.parseToJsonElement(responseBody).jsonObject
            val resultElement = jsonElement["result"]!!.jsonObject
            val status = resultElement["status"]!!.jsonPrimitive.content

            return if (status == "error") {
                val message = resultElement["message"]!!.jsonPrimitive.content
                // نرجع كـ SignInResponseWrapper جزئي مع رسالة الخطأ
                SignInResponseWrapper(
                    jsonrpc = jsonElement["jsonrpc"]!!.jsonPrimitive.content,
                    id = jsonElement["id"]?.jsonPrimitive?.content,
                    result = SignInResult(
                        status = "error",
                        message = Json.decodeFromJsonElement(resultElement) // message ممكن نعمله JsonElement
                    )
                )
            } else {
                // لو success ممكن نعمل decode كامل
//                Json.decodeFromString<SignInResponseWrapper>(responseBody)
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }.decodeFromString<SignInResponseWrapper>(responseBody)

            }

        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception: ${e.message}", e)
            throw e
        }
    }

}
