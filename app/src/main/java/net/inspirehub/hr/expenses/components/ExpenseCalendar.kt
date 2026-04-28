package net.inspirehub.hr.expenses.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.ceil
import java.time.YearMonth
import net.inspirehub.hr.utils.convertToArabicDigits

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseCalendar(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate? = null
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7
    val today = LocalDate.now()
    val colors = appColors()
    val yearText = currentMonth.year.toString()
    var selectedDate by remember { mutableStateOf(initialDate) }


    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            color = colors.surfaceVariant
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .background(colors.surfaceVariant)
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { currentMonth = currentMonth.minusMonths(1) },
                            tint = colors.tertiaryColor
                        )

                        Text(
                            text = currentMonth.month.getDisplayName(
                                TextStyle.FULL,
                                Locale.getDefault()
                            ) + " " + convertToArabicDigits(yearText),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.tertiaryColor
                        )

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { currentMonth = currentMonth.plusMonths(1) },
                            tint = colors.tertiaryColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        listOf(
                            stringResource(R.string.sun),
                            stringResource(R.string.mon),
                            stringResource(R.string.tue),
                            stringResource(R.string.wed),
                            stringResource(R.string.thu),
                            stringResource(R.string.fri),
                            stringResource(R.string.sat),
                        ).forEach {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = colors.tertiaryColor,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Calendar Grid
                    val totalCells = firstDayOfWeek + daysInMonth
                    val totalRows = ceil(totalCells / 7.0).toInt()
                    val cellHeight = 60.dp
                    val totalHeight = (totalRows * cellHeight.value).dp

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        userScrollEnabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(totalHeight)
                    ) {
                        items(totalCells) { index ->
                            if (index < firstDayOfWeek) {
                                Box(modifier = Modifier.size(40.dp)) // Empty space before the 1st
                            } else {
                                val day = index - firstDayOfWeek + 1
                                val date = currentMonth.atDay(day)
                                val isToday = date == today
                                val isSelected = date == selectedDate

                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(40.dp)
                                        .border(
                                            width = 2.dp,
                                            color = if (isSelected) colors.tertiaryColor else colors.transparent,
                                            shape = CircleShape
                                        )
                                        .background(
                                            color = when {
                                                isToday -> colors.tertiaryColor
                                                else -> colors.surfaceVariant
                                            },
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            selectedDate = date
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = convertToArabicDigits(day.toString()),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isToday) colors.onSecondaryColor else colors.onBackgroundColor,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                selectedDate?.let { onDateSelected(it) }
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = colors.onSecondaryColor,
                                containerColor = colors.tertiaryColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                stringResource(R.string.apply),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.inverseOnSurface,
                                contentColor = colors.onSecondaryColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                stringResource(R.string.discard),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}