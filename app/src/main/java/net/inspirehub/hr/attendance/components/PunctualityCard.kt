package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.ScheduleIcon
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.DayStatus
import net.inspirehub.hr.attendance.presentation.getDayStatus
import net.inspirehub.hr.utils.formatLocalizedTime
import net.inspirehub.hr.utils.formatNumber
import net.inspirehub.hr.utils.toUi

@Composable
fun PunctualityCard(
    day: AttendanceDay,
    lateMinutes: Int
) {
    val colors = appColors()
    val status = getDayStatus(day)
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPref.getLanguage()
    val startMinutes = day.states.firstOrNull()?.startMinutes ?: 0
    val statusUi = status.toUi()


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.inverseSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    ScheduleIcon(size = 38)

                    Spacer(Modifier.width(15.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.punctuality),
                            color = colors.onBackgroundColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        Spacer(Modifier.height(6.dp))

                        when (status) {
                            DayStatus.PRESENT -> {
                                Text(
                                    text = stringResource(R.string.you_arrived_on_time),
                                    color = colors.onBackgroundColor.copy(alpha = 0.75f)
                                )
                            }

                            DayStatus.LATE -> {
                                Text(
                                    text = buildAnnotatedString {
                                        append(stringResource(R.string.you_were))
                                        append(" ")

                                        withStyle(
                                            SpanStyle(
                                                color = colors.surfaceContainerLow,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append(formatNumber(lateMinutes.toString(), currentLanguage))
                                            append(" ")
                                            append(
                                                stringResource(
                                                    if (lateMinutes == 1)
                                                        R.string.minute
                                                    else
                                                        R.string.minutes
                                                )
                                            )
                                        }

                                        append(" ")
                                        append(stringResource(R.string.late_small))
                                    },
                                    color = colors.onBackgroundColor.copy(alpha = 0.75f)
                                )
                            }

                            DayStatus.ABSENT -> {
                                Text(
                                    text = stringResource(R.string.no_attendance_record_found),
                                    color = colors.onBackgroundColor.copy(alpha = 0.75f)
                                )
                            }

                            DayStatus.IN_PROGRESS -> {
                                Text(
                                    text = stringResource(R.string.you_are_currently_checked_in_your_attendance_will_be_completed_after_check_out),
                                    color = colors.onBackgroundColor.copy(alpha = 0.75f)
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    StatusBadge(
                        roundedCorner = 20,
                        text = statusUi.text,
                        color = statusUi.color,
                        fontSize = 12,
                        Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            ScheduleAndActualCheckInCard(
                scheduledCheckIn = formatLocalizedTime(
                    hour = 8,
                    minute = 0,
                    language = currentLanguage
                ),
                actualCheckIn = if (status == DayStatus.ABSENT) {
                    "--:--"
                } else {
                    formatLocalizedTime(
                        hour = startMinutes / 60,
                        minute = startMinutes % 60,
                        language = currentLanguage
                    )
                },
                status = status
            )
        }
    }
}