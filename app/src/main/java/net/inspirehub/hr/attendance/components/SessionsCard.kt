package net.inspirehub.hr.attendance.components


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import net.inspirehub.hr.ScheduleIcon
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.utils.formatNumber
import net.inspirehub.hr.utils.toComposeColor


@SuppressLint("DefaultLocale")
@Composable
fun SessionsCard(
    day: AttendanceDay
) {
    val colors = appColors()
    val totalSessions = day.states.size
    val attendanceState = day.states.firstOrNull { it.workEntryType.equals("Attendance", ignoreCase = true)}
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPref.getLanguage()

    val attendanceColor = attendanceState
        ?.workEntryTypeColorHex
        ?.takeIf { it.isNotBlank() }
        ?.toComposeColor()
        ?: colors.onBackgroundColor


    val totalDurationMinutes = day.states.sumOf { state ->
        val end = state.endMinutes
        if (end != null && end > state.startMinutes) {
            end - state.startMinutes
        } else {
            0
        }
    }

    val attendanceDurationMinutes = attendanceState?.let { state ->
        val end = state.endMinutes
        if (end != null && end > state.startMinutes) {
            end - state.startMinutes
        } else {
            0
        }
    } ?: 0

    val attendancePercentage =
        if (totalDurationMinutes > 0) {
            (attendanceDurationMinutes.toDouble() / totalDurationMinutes) * 100
        } else {
            0.0
        }


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.inverseSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp , horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Row (
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                ScheduleIcon(size = 38)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.attendance_sessions),
                    color = colors.onBackgroundColor,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colors.onBackgroundColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .border(
                            2.dp,
                            color = colors.onBackgroundColor,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(top = 16.dp, bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = formatNumber(totalSessions.toString(), currentLanguage),
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackgroundColor,
                        )

                        Text(
                            text = stringResource(R.string.total_sessions),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = colors.onBackgroundColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(attendanceColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .border(
                            2.dp,
                            color = attendanceColor,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(top = 16.dp, bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = attendanceState?.workEntryType ?: "Attendance",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = attendanceColor,
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = if (currentLanguage == "ar") {
                                "٪${formatNumber(
                                    String.format("%.0f", attendancePercentage),
                                    currentLanguage
                                )}"
                            } else {
                                "${formatNumber(
                                    String.format("%.0f", attendancePercentage),
                                    currentLanguage
                                )}%"
                            },
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = attendanceColor
                        )
                    }
                }
            }
        }
    }
}