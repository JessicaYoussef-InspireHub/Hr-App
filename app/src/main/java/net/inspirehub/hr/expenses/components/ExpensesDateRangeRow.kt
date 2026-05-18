package net.inspirehub.hr.expenses.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.*
import java.time.LocalDate
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpensesDateRangeRow(
    fromDate: LocalDate?,
    toDate: LocalDate?,
    onFromDateClick: () -> Unit,
    onToDateClick: () -> Unit
) {

    val colors = appColors()
    val today = LocalDate.now()

    fun formatDate(date: LocalDate?): String {
        return date?.let {
            "%02d/%02d/%d".format(it.dayOfMonth, it.monthValue, it.year)
        } ?: ""
    }

    Column(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(R.string.from),
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = colors.onBackgroundColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = fromDate?.let { formatDate(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFromDateClick() },
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
                placeholder = {
                    Text(
                        text = formatDate(today),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.tertiaryColor
                    )
                },
                singleLine = true
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(R.string.to),
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = colors.onBackgroundColor
            )

            Spacer(modifier = Modifier.width(29.dp))

            TextField(
                value = toDate?.let { formatDate(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToDateClick() },

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
                placeholder = {
                    Text(
                        text = formatDate(today),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.tertiaryColor
                    )
                },

                singleLine = true
            )
        }
    }
}