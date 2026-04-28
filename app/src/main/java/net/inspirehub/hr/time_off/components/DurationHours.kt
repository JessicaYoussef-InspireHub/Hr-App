package net.inspirehub.hr.time_off.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import java.text.NumberFormat
import java.util.Locale
import net.inspirehub.hr.utils.convertToArabicDigits

@Composable
fun DurationHours(
    hours: Double,
){

    val context = LocalContext.current
    val currentLocale = Locale.getDefault()
    val currentLanguage = currentLocale.language
    val colors = appColors()


    fun getLocalizedHourText(language: String): String {
        return if (language == "ar") {
            "ساعات"
        } else {
            context.getString(R.string.hours)
        }
    }

    val formattedNumber = if (currentLanguage == "ar") {
        convertToArabicDigits(hours.toString())
    } else {
        NumberFormat.getInstance(currentLocale).format(hours)
    }

    val hourLabel = getLocalizedHourText(currentLanguage)

    Text(
        text = "$formattedNumber $hourLabel",
        color = colors.onBackgroundColor,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
    )
}