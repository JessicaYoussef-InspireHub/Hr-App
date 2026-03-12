package net.inspirehub.hr.expenses.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun ExpenseDate() {
    val colors = appColors()

    val locale = Locale.getDefault()
    val formatter = DateTimeFormatter.ofPattern("d-M-yyyy", locale)
    var showCalendarDialog by remember { mutableStateOf(false) }
    var selectedDateText by remember { mutableStateOf(LocalDate.now().format(formatter)) }

    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return input.map {
            if (it.isDigit()) arabicDigits[it.digitToInt()] else it
        }.joinToString("")
    }

    val displayText = if(locale.language=="ar") convertToArabicDigits(selectedDateText) else selectedDateText


    Box (
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showCalendarDialog = true
            }
    ){
        TextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.transparent,
                unfocusedContainerColor = colors.transparent,
                disabledContainerColor = colors.transparent,

                focusedIndicatorColor = colors.tertiaryColor,
                unfocusedIndicatorColor = colors.tertiaryColor,
                disabledIndicatorColor = colors.tertiaryColor,

                focusedTextColor = colors.tertiaryColor,
                unfocusedTextColor = colors.tertiaryColor,
                disabledTextColor = colors.tertiaryColor,

                cursorColor = colors.tertiaryColor
            ),
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            ),
            singleLine = true
        )
    }
    if (showCalendarDialog) {
        ExpenseCalendar(
            onDismiss = { showCalendarDialog = false },
            onDateSelected = { date ->
                selectedDateText = date.format(formatter)
            },
            initialDate = LocalDate.parse(selectedDateText, formatter)
        )
    }
}