package net.inspirehub.hr.time_off.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.ArrowRightAlt
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.convertToArabicDigits
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Dates(
    startDate: LocalDate,
    lastDate: LocalDate,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    isHalfDay: Boolean,
    halfDayOption: String,
    onHalfDayOptionChange: (String) -> Unit,
    hideEndDate: Boolean = false
){
    val colors = appColors()
    val locale = java.util.Locale.getDefault()
    val formatter = DateTimeFormatter.ofPattern("d-M-yyyy", locale)

    var formattedStart = startDate.format(formatter)
    var formattedEnd = lastDate.format(formatter)


    if (locale.language == "ar") {
        formattedStart = convertToArabicDigits(formattedStart)
        formattedEnd = convertToArabicDigits(formattedEnd)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = formattedStart,
            color = colors.onBackgroundColor,
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
                ArrowRightAlt()

                Text(
                    text = formattedEnd,
                    color = colors.onBackgroundColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onEndDateClick() }
                )
            }
        }
    }
}