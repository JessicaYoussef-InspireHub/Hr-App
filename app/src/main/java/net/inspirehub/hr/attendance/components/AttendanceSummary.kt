package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.DayStatus
import net.inspirehub.hr.attendance.presentation.getDayStatus

@Composable
fun AttendanceSummary(
    days: List<AttendanceDay>,
    totalWorkedHours: Double,
    totalExpectedHours: Double
) {

    val colors = appColors()
    val present = days.count { getDayStatus(it) == DayStatus.PRESENT }
    val late = days.count { getDayStatus(it) == DayStatus.LATE }
    val absent = days.count { getDayStatus(it) == DayStatus.ABSENT }
    val totalSessions = days.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.inverseSurface
        )
    ) {

        Column(
            Modifier.padding(16.dp)
        ) {

            Text(
                text = stringResource(R.string.attendance_summary),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colors.onBackgroundColor
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AttendanceRing(
                    present = present,
                    late = late,
                    absent = absent,
                    totalWorkedHours = totalWorkedHours,
                    totalExpectedHours = totalExpectedHours
                )

                Spacer(Modifier.width(40.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    AttendanceInfoItem(
                        title = stringResource(R.string.total_days),
                        value = totalSessions.toString()
                    )

                    AttendanceInfoItem(
                        status = DayStatus.PRESENT,
                        title = stringResource(R.string.present),
                        value = present.toString()
                    )

                    AttendanceInfoItem(
                        status = DayStatus.LATE,
                        title = stringResource(R.string.late),
                        value = late.toString()
                    )

                    AttendanceInfoItem(
                        status = DayStatus.ABSENT,
                        title = stringResource(R.string.absent),
                        value = absent.toString()
                    )
                }
            }
        }
    }
}