package net.inspirehub.hr.sign_in.data

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val email: String,
    val password: String,
    val company_id: String,
    val api_key: String
)

@Serializable
data class SignInResponse(
    val jsonrpc: String,
    val method: String,
    val params: SignInRequest
)

@Serializable
data class SignInResponseWrapper(
    val jsonrpc: String,
    val id: String? = null,
    val result: SignInResult
)


@Serializable
data class MessageContent(
    val status: String,
    val message: String,
    val employee_data: EmployeeData,
    val company: List<Company>
)

@Serializable
data class EmployeeData(
    val id: Int,
    val name: String,
    val email: String,
    val department: String,
    val job_title: String,
    val is_active: Boolean,
    val employee_token: String,
    val token_expiry: String
)

@Serializable
data class Company(
    val name: String,
    val address: Address
)

@Serializable
data class Address(
    val street: String,
    val city: String,
    val zip: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val allowed_distance: Double
)

@Serializable
data class SignInResult(
    val status: String,
    val message: MessageContent,
    val company_name: String,
    val license_expiry_date: String,
    val company_url: String
)


