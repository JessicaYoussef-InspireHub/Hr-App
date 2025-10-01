package com.example.myapplicationnewtest.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.time_off.data.LeaveType

@Composable
fun DropDown(
    leaveTypes: List<LeaveType>,
    selectedLeaveType: LeaveType?,
    onLeaveTypeSelected: (LeaveType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage() // "ar" or "en"

    fun convertToArabicNumbers(input: String): String {
        val arabicNumbers = mapOf(
            '0' to '٠', '1' to '١', '2' to '٢', '3' to '٣', '4' to '٤',
            '5' to '٥', '6' to '٦', '7' to '٧', '8' to '٨', '9' to '٩'
        )
        return input.map { arabicNumbers[it] ?: it }.joinToString("")
    }

    fun translateLeaveType(typeKey: String, language: String): String {
        return when (language) {
            "ar" -> when (typeKey) {
                "Annual Leave" -> "اجازة سنوية"
                "Sick Time Off" -> "أجازة مرضية"
                "Unpaid" -> "بدون راتب"
                "Permission" -> "إذن"
                "Customer Meeting" -> "اجتماع عميل"
                "Overtime" -> "عمل إضافي"
                else -> typeKey
            }
            else -> when (typeKey) {
                "annual leave" -> "Annual Leave"
                "Sick Time Off" -> "Sick Time Off"
                "Unpaid" -> "Unpaid"
                "Customer Meeting" -> "Customer Meeting"
                "Overtime" -> "Overtime"
                else -> typeKey
            }
        }
    }




    Column {
        Row (
            modifier = modifier.clickable { expanded = true }
        ){
            Text(
                text = selectedLeaveType?.let {
                    val translatedName = translateLeaveType(it.name, currentLanguage)
                    val remaining = it.remaining_balance?.toString() ?: ""
                    val original = it.original_balance?.toString() ?: ""

                    if (it.remaining_balance != null)
                        "$translatedName (${
                            if (currentLanguage == "ar") convertToArabicNumbers(
                                remaining
                            ) else remaining
                        } ${stringResource(R.string.remaining_out_of)} ${
                            if (currentLanguage == "ar") convertToArabicNumbers(
                                original
                            ) else original
                        })"
                    else
                        translatedName
                } ?: stringResource(R.string.select_leave_type),
                color = MaterialTheme.colorScheme.surface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)

            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Dropdown arrow",
                tint = MaterialTheme.colorScheme.surface
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.onPrimary,
                )
        ) {
            leaveTypes
                .filter { it.remaining_balance != null && it.remaining_balance > 0 }
                .forEach { item ->
                    val translatedName = translateLeaveType(item.name, currentLanguage)
                    val remaining = item.remaining_balance ?: 0
                    val original = item.original_balance ?: 0

                DropdownMenuItem(
                    text = {
                        Text(
                            if (item.remaining_balance != null)
                                "$translatedName (${if (currentLanguage == "ar") convertToArabicNumbers(remaining.toString()) else remaining} ${stringResource(R.string.remaining_out_of)} ${if (currentLanguage == "ar") convertToArabicNumbers(original.toString()) else original})"
                            else
                                translatedName,
                            color = MaterialTheme.colorScheme.surface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    onClick = {
                        onLeaveTypeSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}