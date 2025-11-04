package net.inspirehub.hr.protection.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State


class FingerprintViewModel : ViewModel() {
    private val _authStatus = mutableStateOf("")
    val authStatus: State<String> = _authStatus

    private val _authSuccess = mutableStateOf(false)
    val authSuccess: State<Boolean> = _authSuccess

    fun onAuthenticationResult(success: Boolean,
    ) {
        if (success) {
            _authStatus.value = "auth_success"
            _authSuccess.value = true

        } else {
            _authStatus.value = "auth_failed"
            _authSuccess.value = false
        }
    }

    fun setError(message: String) {
        _authStatus.value = message
    }

}

