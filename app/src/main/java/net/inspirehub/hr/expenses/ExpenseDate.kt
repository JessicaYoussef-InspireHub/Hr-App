package net.inspirehub.hr.expenses

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseDate(){
    val colors = appColors()

    val locale = Locale.getDefault()
    val todayDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d-M-yyyy" , locale)
    var formattedDate = todayDate.format(formatter)


    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return input.map {
            if (it.isDigit()) arabicDigits[it.digitToInt()] else it
        }.joinToString("")
    }

    if (locale.language == "ar") {
        formattedDate = convertToArabicDigits(formattedDate)
    }


    Text(formattedDate ,
        color = colors.onBackgroundColor,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
    )

}