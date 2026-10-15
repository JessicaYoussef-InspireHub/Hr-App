package net.inspirehub.hr.time_off.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.convertToArabicDigits
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R


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
) {
    val colors = appColors()
    val locale = LocalLocale.current.platformLocale
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
    ) {
        OutlinedCard(
            modifier = Modifier
                .weight(1f)
                .clickable { onStartDateClick() },
            border = BorderStroke(
                2.dp,
                colors.surfaceColor
            ),
            colors = CardDefaults.cardColors(
                containerColor = colors.transparent
            ),
            shape = RoundedCornerShape(12.dp)

        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.from),
                    color = colors.onBackgroundColor.copy(.6f),
                    fontSize = 15.sp
                )

                Text(
                    text = formattedStart,
                    color = colors.onBackgroundColor,
                    fontSize = 15.sp
                )
            }
        }



        if (isHalfDay) {
            Spacer(modifier = Modifier.width(12.dp))
            HalfDayDropdown(
                selectedOption = halfDayOption,
                onOptionSelected = onHalfDayOptionChange,
                modifier = Modifier.weight(1f)
            )
        } else if (!hideEndDate) {

            Spacer(modifier = Modifier.width(12.dp))
            OutlinedCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onEndDateClick() },
                border = BorderStroke(
                    2.dp,
                    colors.surfaceColor
                ),
                colors = CardDefaults.cardColors(
                    containerColor = colors.transparent
                ),
                shape = RoundedCornerShape(12.dp)

            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.to),
                        color = colors.onBackgroundColor.copy(.6f),
                        fontSize = 15.sp
                    )

                    Text(
                        text = formattedEnd,
                        color = colors.onBackgroundColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}