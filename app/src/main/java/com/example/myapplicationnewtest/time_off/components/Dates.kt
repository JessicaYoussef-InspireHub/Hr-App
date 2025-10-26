package com.example.myapplicationnewtest.time_off.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Dates(
    startDate: LocalDate,
    lastDate: LocalDate,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHalfDay: Boolean,
    halfDayOption: String,
    onHalfDayOptionChange: (String) -> Unit,
    hideEndDate: Boolean = false
){
    val locale = java.util.Locale.getDefault()
    val formatter = DateTimeFormatter.ofPattern("d-M-yyyy", locale)

    var formattedStart = startDate.format(formatter)
    var formattedEnd = lastDate.format(formatter)

    fun String.replaceDigitsWithArabic(): String {
        val arabicDigits = listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
        return this.map { char ->
            if (char.isDigit()) arabicDigits[char.digitToInt()] else char
        }.joinToString("")
    }

    if (locale.language == "ar") {
        formattedStart = formattedStart.replaceDigitsWithArabic()
        formattedEnd = formattedEnd.replaceDigitsWithArabic()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = formattedStart,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onStartDateClick() }
        )

        if (isHalfDay) {
            HalfDayDropdown(
                selectedOption = halfDayOption,
                onOptionSelected = onHalfDayOptionChange
            )
        }  else if (!hideEndDate) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                    contentDescription = "",
                    modifier = Modifier
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = formattedEnd,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onEndDateClick() }
                )
            }
        }
    }
}