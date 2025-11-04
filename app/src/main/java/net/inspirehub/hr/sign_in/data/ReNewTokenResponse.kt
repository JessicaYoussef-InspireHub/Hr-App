package net.inspirehub.hr.sign_in.data


import kotlinx.serialization.Serializable


@Serializable
data class RenewTokenRequest(
    val api_key: String,
    val company_id: String,
    val employee_token: String
)

@Serializable
data class RenewTokenResponse(
    val status: String,
    val new_employee_token: String,
    val token_expiry: String
)

