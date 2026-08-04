package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.utils.formatNumber

@Composable
fun AttendanceRing(
    present: Int,
    late: Int,
    absent: Int,
    totalWorkedHours: Double,
    totalExpectedHours: Double
) {

    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val language = sharedPref.getLanguage()
    val totalDays = present + late + absent
    val total = totalDays.coerceAtLeast(1)
    val presentSweep = 360f * present / total
    val lateSweep = 360f * late / total
    val absentSweep = 360f * absent / total

    val attendancePercent =
        if (totalExpectedHours > 0) {
            ((totalWorkedHours / totalExpectedHours) * 100)
                .coerceIn(0.0, 100.0)
        } else {
            0.0
        }

    Box(contentAlignment = Alignment.Center) {

        Canvas(
            modifier = Modifier.size(140.dp)
        ) {

            if (totalDays == 0) {

                drawArc(
                    color = colors.onTertiaryContainer,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(
                        width = 20.dp.toPx(),
                        cap = StrokeCap.Butt
                    )
                )

            } else {

                var startAngle = -90f

                drawArc(
                    color = colors.surfaceContainer,
                    startAngle = startAngle,
                    sweepAngle = presentSweep,
                    useCenter = false,
                    style = Stroke(
                        width = 20.dp.toPx(),
                        cap = StrokeCap.Butt
                    )
                )

                startAngle += presentSweep

                drawArc(
                    color = colors.surfaceContainerLow,
                    startAngle = startAngle,
                    sweepAngle = lateSweep,
                    useCenter = false,
                    style = Stroke(
                        width = 20.dp.toPx(),
                        cap = StrokeCap.Butt
                    )
                )

                startAngle += lateSweep

                drawArc(
                    color = colors.surfaceContainerHighest,
                    startAngle = startAngle,
                    sweepAngle = absentSweep,
                    useCenter = false,
                    style = Stroke(
                        width = 20.dp.toPx(),
                        cap = StrokeCap.Butt
                    )
                )
            }
        }

        Text(
            text = "${formatNumber(attendancePercent.toInt().toString(), language)}%",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = colors.onBackgroundColor
        )
    }
}