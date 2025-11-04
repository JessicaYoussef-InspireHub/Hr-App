package net.inspirehub.hr.protection.data


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class ConfirmPinViewModel : ViewModel() {
    val pinLength = 4
    val pinDigits = mutableStateListOf("", "", "", "")
    var errorMessage by mutableStateOf("")

    fun updateDigit(index: Int, value: String) {
        if (index in 0 until pinLength) {
            pinDigits[index] = value
        }
    }

    fun clearDigit(index: Int) {
        if (index in 0 until pinLength) {
            pinDigits[index] = ""
        }
    }

    fun getPin(): String {
        return pinDigits.joinToString("")
    }

    fun validatePin(enteredPin: String): Boolean {
        val currentPin = getPin()
        return if (currentPin == enteredPin) {
                errorMessage = ""
                true
            } else {
                errorMessage = "error_re_enter_pin"
                false

        }
    }
}
