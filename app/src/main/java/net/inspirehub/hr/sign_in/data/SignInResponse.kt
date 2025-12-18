package net.inspirehub.hr.sign_in.data

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeDataWrapper(
    val success: Boolean,
    val employee_data: EmployeeData
)


@Serializable
data class SignInRequest(
    val email: String,
    val password: String,
    val company_id: String,
    val api_key: String
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
    val employee_data: EmployeeDataWrapper,
    val company: List<Company>
)


@Serializable
data class EmployeeData(
    val id: Int,
    val name: String,
    val email: String,
    val department: String,
    val allowed_locations_ids: List<Int> = emptyList(),
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
    val id: Int? = null,
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
    val message: MessageContent? = null,   // nullable
    val company_name: String? = null,
    val license_expiry_date: String? = null,
    val company_url: String? = null
)

@Serializable
data class ErrorResult(
    val status: String,
    val message: String,
    val error_code: String? = null
)



