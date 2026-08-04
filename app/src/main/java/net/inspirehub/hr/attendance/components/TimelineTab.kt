package net.inspirehub.hr.attendance.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.DescriptionIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.getDayStatus
import net.inspirehub.hr.utils.formatLocalizedDate
import net.inspirehub.hr.utils.formatLocalizedTime
import net.inspirehub.hr.utils.getLocalizedWorkedTime
import net.inspirehub.hr.utils.toUi

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimelineTab(
    days: List<AttendanceDay>,
    onClick: (AttendanceDay) -> Unit
) {
    val colors = appColors()
    val timelineListState = rememberLazyListState()
    val context = LocalContext.current
    val currentLanguage = SharedPrefManager(context).getLanguage()

    val startDayText = formatLocalizedTime(
        hour = 0,
        minute = 0,
        language = currentLanguage
    )

    val endDayText = formatLocalizedTime(
        hour = 23,
        minute = 0,
        language = currentLanguage
    )

    LazyColumn(
        state = timelineListState,
        modifier = Modifier
            .fillMaxSize()
            .background(colors.onSecondaryColor)
    ) {

        items(days.size) { index ->

            val day = days[index]
            val status = getDayStatus(day)
            val statusUi = status.toUi()
            val workedMinutes = (day.states.sumOf { it.workedHoursPercentage } * 60).toInt()
            val workedHours = workedMinutes / 60
            val remainingMinutes = workedMinutes % 60

            val workedText = getLocalizedWorkedTime(
                workedHours,
                remainingMinutes,
                currentLanguage
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(IntrinsicSize.Min)
            ) {

                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(statusUi.color)
                )

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onClick(day)
                        },
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceVariant
                    ),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 12.dp,
                        topEnd = 12.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatLocalizedDate(day.date, currentLanguage),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = colors.onBackgroundColor
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                WorkedHours(
                                    size = 12,
                                    workedText = workedText
                                )

                                Spacer(Modifier.width(4.dp))

                                Icon(
                                    imageVector = statusUi.icon,
                                    contentDescription = statusUi.text,
                                    tint = statusUi.color,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        if (day.hasPermission) {

                            Spacer(Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                DescriptionIcon()

                                Spacer(Modifier.width(4.dp))

                                Text(
                                    text = stringResource(R.string.permission_request),
                                    color = colors.outline,
                                    fontSize = 14.sp
                                )
                            }
                        }


                        Spacer(Modifier.height(12.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = startDayText,
                                color = colors.onBackgroundColor,
                                fontSize = 12.sp
                            )

                            Text(
                                text = endDayText,
                                color = colors.onBackgroundColor,
                                fontSize = 12.sp
                            )
                        }

                        TimeLineSection(day = day)
                    }
                }
            }
        }
    }
}