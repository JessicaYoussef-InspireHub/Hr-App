package com.example.myapplicationnewtest.protection.data


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PinCodeViewModel : ViewModel() {
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

    fun isPinComplete(): Boolean {
        return getPin().length == pinLength
    }

}
