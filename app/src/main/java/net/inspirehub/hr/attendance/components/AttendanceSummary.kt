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
import net.inspirehub.hr.attendance.data.WorkEntryTypeSummary
import net.inspirehub.hr.utils.toComposeColor
import java.util.Locale

@Composable
fun AttendanceSummary(
    summary: Map<String, WorkEntryTypeSummary>,
    totalWorkedHours: Double,
    totalExpectedHours: Double,
    totalCount: Int
) {

    val colors = appColors()

    val summaryItems = summary.entries.sortedByDescending { it.value.count }

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

                AttendanceRing(summary = summary)

                Spacer(Modifier.width(40.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    AttendanceInfoItem(
                        title = stringResource(R.string.total_days),
                        value = totalCount.toString()
                    )

                    summaryItems.forEach { entry ->

                        val name = entry.key
                        val item = entry.value

                        AttendanceSummaryItemRow(
                            name = name,
                            count = item.count,
                            color = item.work_entry_type_color_hex?.toComposeColor()
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column (horizontalAlignment = Alignment.Start){

                    Text(
                        text = stringResource(R.string.expected_hours),
                        fontSize = 12.sp,
                        color = colors.onBackgroundColor.copy(alpha = 0.7f)
                    )

                    Text(
                        text = formatHours(totalExpectedHours),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackgroundColor
                    )
                }

                Column( horizontalAlignment = Alignment.Start ) {

                    Text(
                        text = stringResource(R.string.worked_hours),
                        fontSize = 12.sp,
                        color = colors.onBackgroundColor.copy(alpha = 0.7f)
                    )
                    Text(
                        text = formatHours(totalWorkedHours),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackgroundColor
                    )
                }
            }
        }
    }
}

private fun formatHours(hours: Double): String {
    return String.format(
        Locale.US,
        "%.2f h",
        hours
    )
}