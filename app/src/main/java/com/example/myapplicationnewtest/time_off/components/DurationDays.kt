package com.example.myapplicationnewtest.time_off.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DurationDays(
    days: Int,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val currentLocale = Locale.getDefault()
    val currentLanguage = currentLocale.language

    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
        return input.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
    }

    fun getLocalizedDayText(count: Int, language: String): String {
        return if (language == "ar") {
            when (count) {
                1 -> "يوم"
                2 -> "يومين"
                in 3..10 -> "أيام"
                else -> "يومًا"
            }
        } else {
            if (count == 1) context.getString(R.string.day)
            else context.getString(R.string.days)
        }
    }

    val formattedNumber = if (currentLanguage == "ar") {
        convertToArabicDigits(days.toString())
    } else {
        NumberFormat.getInstance(currentLocale).format(days)
    }

    val dayLabel = getLocalizedDayText(days, currentLanguage)

    Text(
        text = "$formattedNumber $dayLabel",
        modifier = modifier,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
    )
}