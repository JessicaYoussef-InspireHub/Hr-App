package net.inspirehub.hr.sign_in.data

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import net.inspirehub.hr.SharedPrefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

sealed class SignInUiState {
    data object Idle : SignInUiState()
    data object Loading : SignInUiState()
    data class Success(val response: SignInResponseWrapper) : SignInUiState()
    data class Error(val message: String) : SignInUiState()
}

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Idle)
    val uiState: StateFlow<SignInUiState> = _uiState

    fun signIn(email: String, password: String, companyId: String, apiKey: String) {
        viewModelScope.launch {
            _uiState.value = SignInUiState.Loading
            try {
                val response = SignInApiService.signIn(email, password, companyId, apiKey)
                val result = response.result

                if (response.result.status == "error") {
                    // لو في خطأ، نحوله لحالة Error
                    val errorMsg = if (response.result.message is JsonElement) {
                        response.result.message.jsonPrimitive.content
                    } else {
                        response.result.message.toString()
                    }
                    _uiState.value = SignInUiState.Error(errorMsg)
                    return@launch
                }

                // Success
                val employeeData = result.message
                    ?.employee_data
                    ?.employee_data

                val companies = response.result.message?.company ?: emptyList()

                val companyAddress = response.result.message?.company
                    ?.firstOrNull { it.name == response.result.company_name }
                    ?.address


                saveEmployeeData(
                    token = employeeData?.employee_token ?: "",
                    tokenExpiry = employeeData?.token_expiry ?: "",
                    companyId = companyId,
                    apiKey = apiKey,
                    latitude = companyAddress?.latitude ?: 0.0,
                    longitude = companyAddress?.longitude ?: 0.0,
                    allowedDistance = companyAddress?.allowed_distance ?: 0.0,
                    allowedLocationsIds = employeeData?.allowed_locations_ids ?: emptyList(),
                    companies = companies,
                    companyUrl = response.result.company_url ?: ""
                )

                _uiState.value = SignInUiState.Success(response)

                // إرسال FCM token
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val fcmToken = task.result
                        viewModelScope.launch {
                            SignInApiService.sendDeviceToken(
                                employeeData?.employee_token ?: "unknown",
                                fcmToken
                            )
                        }
                    } else {
                        Log.e("FCM", "Failed to get FCM token")
                    }
                }

                checkAndRenewToken(
                    currentToken = employeeData?.employee_token ?: "unknown",
                    expiry = employeeData?.token_expiry ?: "unknown",
                    apiKey = apiKey,
                    companyId = companyId
                )

            } catch (e: Exception) {
                Log.e("ViewModel", "Sign-in failed", e)
                _uiState.value = SignInUiState.Error(e.message ?: "Unknown error")
            }
        }
    }


    private fun saveEmployeeData(
        token: String,
        tokenExpiry: String,
        companyId: String,
        apiKey: String,
        latitude: Double,
        longitude: Double,
        allowedDistance: Double,
        allowedLocationsIds: List<Int>,
        companies: List<Company>,
        companyUrl: String
    ) {
        val sharedPref = SharedPrefManager(getApplication())

        sharedPref.saveToken(token)
        sharedPref.saveTokenExpiry(tokenExpiry)
        sharedPref.saveCompanyId(companyId)
        sharedPref.saveApiKey(apiKey)
        sharedPref.saveLatitude(latitude)
        sharedPref.saveLongitude(longitude)
        sharedPref.saveAllowedDistance(allowedDistance)
        sharedPref.saveAllowedLocationsIds(allowedLocationsIds)
        sharedPref.saveCompaniesLatLng(companies)
        sharedPref.saveCompanyUrl(companyUrl)

    }


//    fun signIn(email: String, password: String, companyId: String, apiKey: String) {
//        viewModelScope.launch {
//
//            val response = SignInApiService.signIn(email, password, companyId, apiKey)
//
//
//            if (response.result.status != "error") {
//                Log.d("STATUS", "done jessica done")
//
//                // Send the dynamic device tokenي
//                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val fcmToken = task.result
//                        viewModelScope.launch {
//                            SignInApiService.sendDeviceToken(
//                                response.result.message.employee_data.employee_token,
//                                fcmToken
//                            )
//                        }
//                        Log.d("FCM", "FCM Token: $fcmToken")
//                    } else {
//                        Log.e("FCM", "Failed to get FCM token")
//                    }
//                }
//            }
//
//
//
//            _uiState.value = SignInUiState.Loading
//            try {
//                val response = SignInApiService.signIn(email, password, companyId, apiKey)
//                _uiState.value = SignInUiState.Success(response)
//
//                val employeeData = response.result.message.employee_data
//                checkAndRenewToken(
//                    currentToken = employeeData.employee_token,
//                    expiry = employeeData.token_expiry,
//                    apiKey = apiKey,
//                    companyId = companyId
//                )
//
//            } catch (e: Exception) {
//                Log.e("ViewModel", "Sign-in failed", e)
//
//                val message = e.message ?: "Unknown error"
//                _uiState.value = SignInUiState.Error(message)
//            }
//
//        }
//    }

    fun resetState() {
        _uiState.value = SignInUiState.Idle
    }

    @SuppressLint("NewApi")
    fun checkAndRenewToken(
        currentToken: String,
        expiry: String,
        apiKey: String,
        companyId: String
    ) {
        viewModelScope.launch {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val expiryDate = LocalDateTime.parse(expiry, formatter)
                    .atZone(ZoneId.of("UTC"))
                    .toInstant()

                val now = Instant.now()

                if (now.isAfter(expiryDate)) {
                    val renewResponse = SignInApiService.renewToken(apiKey, companyId, currentToken)
                    Log.d("TOKEN", "New token: ${renewResponse.result.new_token}")

                    val sharedPref = SharedPrefManager(getApplication())
                    sharedPref.saveToken(renewResponse.result.new_token)
                    sharedPref.saveTokenExpiry(renewResponse.result.expiry_date)
                }
            } catch (e: Exception) {
                Log.e("TOKEN", "Failed to renew token", e)
            }
        }
    }
}