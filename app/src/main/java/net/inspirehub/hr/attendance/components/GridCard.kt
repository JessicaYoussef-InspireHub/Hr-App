package net.inspirehub.hr.attendance.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.StatusCircleIcon
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.getDayStatus
import net.inspirehub.hr.utils.getLocalizedWorkedTime
import net.inspirehub.hr.utils.toUi

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GridCard(
    day: AttendanceDay,
    onClick: (AttendanceDay) -> Unit
) {

    val status = getDayStatus(day)
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    val currentLanguage = sharedPref.getLanguage()
    val statusUi = status.toUi()
    val workedMinutes = (day.states.sumOf { it.workedHoursPercentage } * 60).toInt()
    val workedHours = workedMinutes / 60
    val remainingMinutes = workedMinutes % 60

    val workedText = getLocalizedWorkedTime(
        workedHours,
        remainingMinutes,
        currentLanguage
    )



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(day) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DateHeader(
                date = day.date,
                hasPermission = day.hasPermission
            )

            StatusCircleIcon(
                imageVector = statusUi.icon,
                tint = statusUi.color
            )

            StatusBadge(
                text = statusUi.text,
                color = statusUi.color,
                roundedCorner = 50,
                fontSize = 14,
                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 8.dp
                )
            )

            WorkedHours(
                size = 12,
                workedText = workedText
            )

            Progress(
                status = status,
                attendanceStates = day.states
            )
        }
    }
}