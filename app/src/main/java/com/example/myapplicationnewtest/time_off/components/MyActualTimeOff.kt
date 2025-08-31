package com.example.myapplicationnewtest.time_off.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplicationnewtest.R
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyActualTimeOff(
    annualLeaveRemainingDays: String,
    permissionRemainingHours: String,
) {

    fun getLastDayOfYear(): String {
        val lastDay = java.time.LocalDate.of(java.time.Year.now().value, 12, 31)

        val currentLocale = Locale.getDefault()
        val formatter = if (currentLocale.language == "ar") {
            DateTimeFormatter.ofPattern("yyyy/MM/dd", currentLocale)
        } else {
            DateTimeFormatter.ofPattern("dd/MM/yyyy", currentLocale)
        }

        val formattedDate = lastDay.format(formatter)

        return if (currentLocale.language == "ar") {
            formattedDate.map { char ->
                if (char.isDigit()) listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')[char.digitToInt()] else char
            }.joinToString("")
        } else {
            formattedDate
        }
    }


    fun getLastDayOfCurrentMonth(): String {
        val yearMonth = YearMonth.now()
        val lastDay = yearMonth.atEndOfMonth()

        val currentLocale = Locale.getDefault()
        val formatter = if (currentLocale.language == "ar") {
            DateTimeFormatter.ofPattern("yyyy/MM/dd", currentLocale)
        } else {
            DateTimeFormatter.ofPattern("dd/MM/yyyy", currentLocale)
        }

        val formattedDate = lastDay.format(formatter)

        return if (currentLocale.language == "ar") {
            formattedDate.map { char ->
                if (char.isDigit()) listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')[char.digitToInt()] else char
            }.joinToString("")
        } else {
            formattedDate
        }
    }


    fun String.replaceDigitsWithArabic(): String {
        val arabicDigits = listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
        return this.map { char ->
            if (char.isDigit()) arabicDigits[char.digitToInt()] else char
        }.joinToString("")
    }



    val currentLocale = Locale.getDefault()
    val formattedAnnualLeave = if (currentLocale.language == "ar") {
        annualLeaveRemainingDays.replaceDigitsWithArabic()
    } else {
        annualLeaveRemainingDays
    }

    val formattedPermissionHours = if (currentLocale.language == "ar") {
        permissionRemainingHours.replaceDigitsWithArabic()
    } else {
        permissionRemainingHours
    }





    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onPrimary),
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MyActualTimeOffText(
                    stringResource(R.string.annual_leaves),
                    formattedAnnualLeave,
//                    annualLeaveRemainingDays,
                    stringResource(R.string.days_available),
                    stringResource(R.string.valid_until_end_of_month, getLastDayOfYear()),
                    true
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .height(150.dp)
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MyActualTimeOffText(
                    stringResource(R.string.permission),
                    formattedPermissionHours,
//                    permissionRemainingHours,
                    stringResource(R.string.hours_available),
                    stringResource(R.string.valid_until_end_of_month, getLastDayOfCurrentMonth()),
                    false )
            }


        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSecondaryContainer)
        )
    }
}