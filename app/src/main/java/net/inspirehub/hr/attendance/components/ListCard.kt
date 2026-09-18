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
import net.inspirehub.hr.utils.getLocalizedWorkedTime
import net.inspirehub.hr.utils.toUi
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.getDayStatus
import net.inspirehub.hr.utils.toComposeColor
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.width

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListCard(
    day: AttendanceDay,
    onClick: (AttendanceDay) -> Unit
) {

    val status = getDayStatus(day)
    val statusUi = status.toUi()
    val displayedWorkEntryTypes = day.states.distinctBy { it.workEntryType }
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    val currentLanguage = sharedPref.getLanguage()

    val attendanceTotalSeconds = day.states
        .filter { it.workEntryType == "Attendance" }
        .sumOf { state ->

            val parts = state.workedHoursAndMinutes.split(":")

            val hours = parts.getOrNull(0)?.toLongOrNull() ?: 0L
            val minutes = parts.getOrNull(1)?.toLongOrNull() ?: 0L
            val seconds = parts.getOrNull(2)?.toLongOrNull() ?: 0L

            (hours * 3600) +
                    (minutes * 60) +
                    seconds
        }

    val workedHours = (attendanceTotalSeconds / 3600).toInt()
    val remainingMinutes = ((attendanceTotalSeconds % 3600) / 60).toInt()

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
            DateHeader(date = day.date )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    maxItemsInEachRow = 2
                ) {
                    displayedWorkEntryTypes.forEach { state ->

                        StatusBadge(
                            roundedCorner = 20,
                            text = state.workEntryType,
                            color = state.workEntryTypeColorHex?.toComposeColor()
                                ?: statusUi.color,
                            fontSize = 12,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                WorkedHours(
                    size = 12,
                    workedText = workedText
                )
            }

            Spacer(modifier = Modifier.height(10.dp))


            Progress(
                attendanceStates = day.states
            )
        }
    }
}