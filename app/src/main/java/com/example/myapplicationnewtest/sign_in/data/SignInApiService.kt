package com.example.myapplicationnewtest.sign_in.data

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

object SignInApiService {

    private val httpClient = HttpClient {
        install(Logging) { level = LogLevel.ALL }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        followRedirects = true
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
            val response: HttpResponse = com.example.myapplicationnewtest.check_in_out.data.httpClient.post("https://ahmedelzupeir-androidapp2.odoo.com/api/employee/renew_token") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(payload)
            }

            val responseBody: String = response.body()
            Log.d("HTTP", "Raw RenewToken Response: $responseBody")

            Json.decodeFromString(responseBody)
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
        val payload =
//            SignInResponse(
//            jsonrpc = "2.0",
//            method = "call",
//            params =
            SignInRequest(
                email = email,
                password = password,
                company_id = companyId,
                api_key = apiKey
//            )
        )

        return try {
            val response: HttpResponse = httpClient.post("https://ahmedelzupeir-androidapp2.odoo.com/api/validate_company") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(payload)
            }

            val responseBody: String = response.body()
            Log.d("HTTP", "Raw Response: $responseBody")

            val parsed = Json.decodeFromString<SignInResponseWrapper>(responseBody)

            if (parsed.result.status == "error") {
                Log.d("STATUS", "Sign-in error")
            } else {
                Log.d("STATUS", "Sign-in success")
            }

            parsed
        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception: ${e.message}", e)
            throw e
        }
    }
}
