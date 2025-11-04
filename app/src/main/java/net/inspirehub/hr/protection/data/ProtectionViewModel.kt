package net.inspirehub.hr.protection.data


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ProtectionViewModel : ViewModel() {

    private val _navigateTo = MutableSharedFlow<String>()
    val navigateTo: SharedFlow<String> = _navigateTo

    fun onFingerprintSelected(
    ) {
        viewModelScope.launch {
            _navigateTo.emit("FingerPrintScreen")
        }
    }

    fun onPinCodeSelected(
    ) {
        viewModelScope.launch {
            _navigateTo.emit("PinCodeScreen")
        }
    }

    fun onSkipSelected(
    ) {
        viewModelScope.launch {
            _navigateTo.emit("CheckInOutScreen")
        }
    }

}
