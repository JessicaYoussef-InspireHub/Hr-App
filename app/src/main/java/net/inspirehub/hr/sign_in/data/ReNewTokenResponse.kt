package net.inspirehub.hr.sign_in.data


import kotlinx.serialization.Serializable


@Serializable
data class RenewTokenRequest(
    val api_key: String,
    val company_id: String,
    val employee_token: String
)

@Serializable
data class RenewTokenResult(
    val status: String,
    val message: String,
    val employee_id: Int,
    val email: String,
    val employee_name: String,
    val new_token: String,
    val creation_date: String,
    val expiry_date: String
)


@Serializable
data class RenewTokenResponse(
    val jsonrpc: String,
    val id: String? = null,
    val result: RenewTokenResult
)