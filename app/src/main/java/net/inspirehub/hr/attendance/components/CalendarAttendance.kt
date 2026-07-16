package net.inspirehub.hr.attendance.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.DayStatus
import net.inspirehub.hr.attendance.presentation.getDayStatus
import net.inspirehub.hr.utils.formatNumber
import net.inspirehub.hr.utils.toUi
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.ceil

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarAttendance(
    currentMonth: YearMonth,
    days: List<AttendanceDay>,
    onDayClick: (AttendanceDay) -> Unit
){
    val colors = appColors()
    val calendarMap = days.associateBy { it.date }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val yearText = currentMonth.year.toString()
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    val currentLanguage = sharedPref.getLanguage()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7
    val totalCells = firstDayOfWeek + daysInMonth
    val totalRows = ceil(totalCells / 7.0).toInt()
    val cellHeight = 48.dp
    val totalHeight = (totalRows * cellHeight.value).dp

    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

            Text(
                text = currentMonth.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.getDefault()
                ) + " " + formatNumber(yearText, currentLanguage),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.tertiaryColor
            )

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
                    val key = date.toString()
                    val dayData = calendarMap[key]
                    val workedHours = dayData?.states?.sumOf { it.workedHours } ?: 0.0
                    val expectedHours = dayData?.states?.sumOf { it.expectedHours } ?: 8.0
                    val progress = (workedHours / expectedHours).coerceIn(0.0, 1.0).toFloat()

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(40.dp)
                            .clickable(enabled = dayData != null) {
                                selectedDate = date
                                val key = date.toString()
                                val dayData = calendarMap[key] ?: AttendanceDay(
                                    date = date.toString(),
                                    states = emptyList()
                                )
                                onDayClick(dayData)
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        val isSelected = selectedDate == date

                        val status = if (dayData != null) {
                            getDayStatus(dayData)
                        } else {
                            DayStatus.ABSENT
                        }

                        val statusUi = status.toUi()



                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier.size(38.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (dayData != null) {
                                    CircularProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier.fillMaxSize(),
                                        strokeWidth = 2.dp,
                                        color = statusUi.color,
                                        trackColor = colors.onSurface.copy(alpha = 0.2f)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(
                                            if (isSelected && dayData != null)
                                                28.dp
                                            else if (isSelected)
                                                40.dp
                                            else
                                                28.dp
                                        )
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected)
                                                statusUi.color
                                            else
                                                colors.transparent
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = formatNumber(
                                            day.toString(),
                                            currentLanguage
                                        ),
                                        color = colors.onBackgroundColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}