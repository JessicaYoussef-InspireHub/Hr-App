package net.inspirehub.hr.attendance.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.getDayStatus
import net.inspirehub.hr.utils.getLocalizedWorkedTime
import net.inspirehub.hr.utils.toUi

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListCard(
    day: AttendanceDay,
    onClick: (AttendanceDay) -> Unit
) {

    val status = getDayStatus(day)
    val statusUi = status.toUi()
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    val currentLanguage = sharedPref.getLanguage()
    val workedMinutes = (day.states.sumOf { it.workedHours } * 60).toInt()
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
            modifier = Modifier.padding(12.dp)
        ) {
            DateHeader(
                date = day.date,
                hasPermission = day.hasPermission
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusBadge(
                    roundedCorner = 20,
                    text = statusUi.text,
                    color = statusUi.color,
                    fontSize = 12,
                    Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 4.dp
                    )
                )

                WorkedHours(
                    size = 12,
                    workedText = workedText
                )
            }

            Spacer(modifier = Modifier.height(10.dp))


            Progress(
                status = status,
                attendanceStates = day.states
            )
        }
    }
}