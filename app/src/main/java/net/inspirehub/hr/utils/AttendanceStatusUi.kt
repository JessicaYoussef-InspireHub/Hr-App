package net.inspirehub.hr.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceState
import net.inspirehub.hr.attendance.presentation.DayStatus

data class AttendanceStatusUi(
    val color: Color,
    val text: String,
    val icon: ImageVector
)

@Composable
fun DayStatus.toUi(): AttendanceStatusUi {

    val colors = appColors()

    return when (this) {

        DayStatus.PRESENT -> AttendanceStatusUi(
            color = colors.surfaceContainer,
            text = stringResource(R.string.present),
            icon = Icons.Default.Person
        )

        DayStatus.LATE -> AttendanceStatusUi(
            color = colors.surfaceContainerLow,
            text = stringResource(R.string.late),
            icon = Icons.Default.AccessTime
        )

        DayStatus.ABSENT -> AttendanceStatusUi(
            color = colors.surfaceContainerHighest,
            text = stringResource(R.string.absent),
            icon = Icons.Default.PersonOff
        )

        DayStatus.IN_PROGRESS -> AttendanceStatusUi(
            color = colors.onError,
            text = stringResource(R.string.in_progress),
            icon = Icons.Default.WorkHistory
        )
    }
}

fun DayStatus.getPercentage(
    attendanceStates: List<AttendanceState>
): Int {

    val totalSessions = attendanceStates.size

    return when (this) {

        DayStatus.PRESENT -> 100

        DayStatus.IN_PROGRESS -> {
            val expectedHours = attendanceStates.sumOf { it.expectedHours }
            val workedHours = attendanceStates.sumOf { it.workedHours }

            if (expectedHours > 0)
                ((workedHours / expectedHours) * 100)
                    .toInt()
                    .coerceIn(0, 100)
            else
                0
        }

        DayStatus.LATE ->
            if (totalSessions == 0) 0
            else (attendanceStates.count { it.endMinutes == null } * 100) / totalSessions

        DayStatus.ABSENT -> 0
    }
}

fun DayStatus.getProgress(
    attendanceStates: List<AttendanceState>
): Float {

    return when (this) {

        DayStatus.PRESENT -> 1f

        DayStatus.IN_PROGRESS -> {
            val expectedHours = attendanceStates.sumOf { it.expectedHours }
            val workedHours = attendanceStates.sumOf { it.workedHours }

            if (expectedHours > 0)
                (workedHours / expectedHours).toFloat().coerceIn(0f, 1f)
            else
                0f
        }

        DayStatus.LATE -> {
            val expectedHours = attendanceStates.sumOf { it.expectedHours }
            val workedHours = attendanceStates.sumOf { it.workedHours }

            if (expectedHours > 0)
                (workedHours / expectedHours).toFloat().coerceIn(0f, 1f)
            else
                0f
        }

        DayStatus.ABSENT -> 0f
    }
}