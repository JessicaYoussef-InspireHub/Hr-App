package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceState
import net.inspirehub.hr.utils.formatNumber
import net.inspirehub.hr.utils.toComposeColor

@Composable
fun Progress(
    attendanceStates: List<AttendanceState>
) {
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    val currentLanguage = sharedPref.getLanguage()
    val colors = appColors()

    // Expected working hours
    val expectedWorkHours = 8.0

    /*
     * Group all entries by work entry type.
     *
     * Example:
     *
     * Attendance = 6.38
     * Early      = 0.50
     *
     * Attendance = 79.75%
     * Early      = 6.25%
     */
    val progressItems = attendanceStates
        .groupBy { it.workEntryType }
        .mapNotNull { (type, states) ->

            // Sum duration for the same work entry type
            val duration = states.sumOf {
                it.workedHoursPercentage
            }

            if (duration <= 0.0) {
                return@mapNotNull null
            }

            // Calculate percentage based on 8 working hours
            val percentage = (
                    duration / expectedWorkHours
                    ).toFloat().coerceIn(0f, 1f)

            // Get the color of this work entry type from API
            val color = states
                .firstOrNull()
                ?.workEntryTypeColorHex
                ?.toComposeColor()
                ?: colors.tertiaryColor

            ProgressItem(
                type = type,
                duration = duration,
                percentage = percentage,
                color = color
            )
        }

    /*
     * Attendance percentage only.
     *
     * The number displayed on the right side
     * must always represent Attendance only.
     */
    val attendanceItem = progressItems
        .firstOrNull {
            it.type == "Attendance"
        }

    val attendanceProgress = attendanceItem
        ?.percentage
        ?: 0f

    val attendanceColor = attendanceItem
        ?.color
        ?: colors.tertiaryColor

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        /*
         * More than color progress bar
         *
         * Each work entry type gets its own percentage
         * and its own color from the API.
         */
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceContainerLowest)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {

                progressItems.forEach { item ->

                    Box(
                        modifier = Modifier
                            .weight(item.percentage)
                            .height(8.dp)
                            .background(item.color)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        /*
         * Display Attendance percentage only.
         *
         * Example:
         * Attendance = 6.38 hours
         * 6.38 / 8 * 100 = 79.75%
         *
         * Display:
         * 79%
         */
        Text(
            text = "${formatNumber(
                "${(attendanceProgress * 100).toInt()}",
                currentLanguage
            )}%",
            color = attendanceColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

private data class ProgressItem(
    val type: String,
    val duration: Double,
    val percentage: Float,
    val color: Color
)