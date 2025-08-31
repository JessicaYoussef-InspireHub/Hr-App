package com.example.myapplicationnewtest.sign_in.data

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationnewtest.SharedPrefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
                _uiState.value = SignInUiState.Success(response)

                val employeeData = response.result.message.employee_data
                checkAndRenewToken(
                    currentToken = employeeData.employee_token,
                    expiry = employeeData.token_expiry,
                    apiKey = apiKey,
                    companyId = companyId
                )

            } catch (e: Exception) {
                Log.e("ViewModel", "Sign-in failed", e)
                _uiState.value = SignInUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

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
                    Log.d("TOKEN", "New token: ${renewResponse.new_employee_token}")

                    val sharedPref = SharedPrefManager(getApplication())
                    sharedPref.saveToken(renewResponse.new_employee_token)
                    sharedPref.saveTokenExpiry(renewResponse.token_expiry)
                }
            } catch (e: Exception) {
                Log.e("TOKEN", "Failed to renew token", e)
            }
        }
    }

}
