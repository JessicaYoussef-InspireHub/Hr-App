package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.Stroke
import net.inspirehub.hr.attendance.data.WorkEntryTypeSummary
import net.inspirehub.hr.utils.toComposeColor

@Composable
fun AttendanceRing(
    summary: Map<String, WorkEntryTypeSummary>,
) {

    val colors = appColors()

    val items = summary.entries
        .filter { it.value.count > 0 }
        .mapNotNull { entry ->

            val color = entry.value.work_entry_type_color_hex
                ?.takeIf { it.isNotBlank() }
                ?.toComposeColor()

            if (color != null) {
                AttendanceRingSegment(
                    name = entry.key,
                    count = entry.value.count,
                    color = color
                )
            } else {
                null
            }
        }

    val totalSummaryCount = items.sumOf { it.count }

  Canvas(
        modifier = Modifier.size(120.dp)
    ) {

        val strokeWidth = 12.dp.toPx()

        if (totalSummaryCount > 0) {

            var startAngle = -90f

            items.forEach { item ->

                val sweepAngle =
                    360f * item.count / totalSummaryCount

                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth
                    )
                )

                startAngle += sweepAngle
            }

        } else {

            drawArc(
                color = colors.onBackgroundColor.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth
                )
            )
        }
    }
}

private data class AttendanceRingSegment(
    val name: String,
    val count: Int,
    val color: androidx.compose.ui.graphics.Color
)