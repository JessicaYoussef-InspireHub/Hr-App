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
import io.ktor.client.engine.okhttp.*
import com.google.firebase.crashlytics.FirebaseCrashlytics

object SignInApiService {
    private val json = Json { ignoreUnknownKeys = true
        isLenient = true
    }
    private val httpClient = HttpClient(OkHttp) {
        install(Logging) {
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(json)
        }

        followRedirects = true
    }

    suspend fun sendDeviceToken(employeeToken: String, mobileToken: String) {
        val payload = mapOf(
            "employee_token" to employeeToken,
            "mobile_token" to mobileToken,
            "mobile_type" to "android"
        )

        try {
            Log.d("BASE_URL", AppConfig.baseUrl)
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

            json.decodeFromString<RenewTokenResponse>(responseBody)

        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception in renewToken: ${e.message}", e)
            throw e
        }
    }


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

        val jsonBody = json.encodeToString(
            SignInRequest.serializer(),
            payload
        )
        println("📤 SignIn Request:")
        println("URL: ${AppConfig.baseUrl}/api/validate_company")
        println("Headers: Content-Type=application/json, Accept=application/json")
        println("Body: $jsonBody")

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
            println("📥 Response Body: $responseBody")

            val jsonElement = json.parseToJsonElement(responseBody).jsonObject
            val resultElement = jsonElement["result"]?.jsonObject ?: throw Exception("Missing result in response")
            val status = resultElement["status"]?.jsonPrimitive?.content ?: throw Exception("Missing status in response")

           if (status == "error") {
                val error = json.decodeFromJsonElement<ErrorResult>(resultElement)
                throw Exception(error.message)
            } else {
               json.decodeFromString<SignInResponseWrapper>(responseBody)

            }

        } catch (e: Exception) {

            var t: Throwable? = e
            while (t != null) {

                val message = "${t.javaClass.name}: ${t.message}"

                Log.e("SSL_DEBUG", message, t)

                FirebaseCrashlytics.getInstance().log(message)

                t = t.cause
            }

            FirebaseCrashlytics.getInstance().recordException(e)

            throw e
        }
    }
}