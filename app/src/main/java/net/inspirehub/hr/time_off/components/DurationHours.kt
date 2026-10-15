package net.inspirehub.hr.time_off.components

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import java.text.NumberFormat
import net.inspirehub.hr.utils.convertToArabicDigits
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp

@Composable
fun DurationHours(
    hours: Double,
) {

    val context = LocalContext.current
    val currentLocale = LocalLocale.current.platformLocale
    val currentLanguage = currentLocale.language
    val colors = appColors()


    @SuppressLint("LocalContextGetResourceValueCall")
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


    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(
            2.dp,
            colors.surfaceColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = colors.transparent
        ),
        shape = RoundedCornerShape(12.dp)

    ) {
        Text(
            text = "$formattedNumber $hourLabel",
            color = colors.onBackgroundColor,
            fontSize = 15.sp,
            modifier = Modifier.padding(14.dp)
        )
    }
}