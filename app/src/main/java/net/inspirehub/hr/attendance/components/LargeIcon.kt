package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.DayStatus
import net.inspirehub.hr.utils.toUi


@Composable
fun AttendanceStatusIcon(
    status: DayStatus
) {

    val statusUi = status.toUi()
    val colors = appColors()

    Box(
        modifier = Modifier
            .size(90.dp)
            .background(
                statusUi.color.copy(alpha = 0.12f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    statusUi.color,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = statusUi.icon,
                contentDescription = statusUi.text,
                tint = colors.onBackgroundColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}