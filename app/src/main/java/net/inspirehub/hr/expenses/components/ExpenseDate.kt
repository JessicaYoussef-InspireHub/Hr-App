package net.inspirehub.hr.expenses.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
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


    TextField(
        value = formattedDate,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.transparent,
            unfocusedContainerColor = colors.transparent,
            disabledContainerColor = colors.transparent,

            focusedIndicatorColor = colors.tertiaryColor,
            unfocusedIndicatorColor = colors.tertiaryColor,

            focusedTextColor = colors.onBackgroundColor,
            unfocusedTextColor = colors.onBackgroundColor,

            cursorColor = colors.tertiaryColor
        ),
        textStyle = TextStyle(
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        ),
        singleLine = true
    )
}