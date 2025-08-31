package com.example.myapplicationnewtest.protection.data

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.myapplicationnewtest.SharedPrefManager

class EnterPinViewModel(private val sharedPrefManager: SharedPrefManager) : ViewModel() {
    val pinLength = 4

    var pinDigits = mutableStateListOf("", "", "", "")
        private set

    var error by mutableStateOf("")
        private set

    val isPinComplete: Boolean
        get() = pinDigits.all { it.isNotEmpty() }

    fun onPinDigitChanged(index: Int, value: String) {
        if (value.length <= 1 && value.all { it.isDigit() }) {
            pinDigits[index] = value
        }
    }

    fun clearDigit(index: Int) {
        if (index in pinDigits.indices) {
            pinDigits[index] = ""
        }
    }


    fun checkPin(onSuccess: () -> Unit, onFailure: () -> Unit) {
        val enteredPin = pinDigits.joinToString("")
        val savedPin = sharedPrefManager.getPin()

        if (enteredPin == savedPin) {
            error = ""
            onSuccess()
        } else {
            error = "error_incorrect_pin"
            onFailure()
        }
    }

    fun reset() {
        for (i in pinDigits.indices) pinDigits[i] = ""
        error = ""
    }
}
