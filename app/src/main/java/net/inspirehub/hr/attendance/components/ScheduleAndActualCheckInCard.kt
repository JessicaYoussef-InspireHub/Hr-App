package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.attendance.presentation.DayStatus
import net.inspirehub.hr.utils.toUi


@Composable
fun ScheduleAndActualCheckInCard(
    scheduledCheckIn: String,
    actualCheckIn: String,
    status: DayStatus
) {
    val statusUi = status.toUi()


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statusUi.color.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
    Column (
            modifier = Modifier.fillMaxWidth()
                .padding(12.dp)
        ) {

            CheckInItem(
                title = stringResource(R.string.scheduled_check_in),
                value = scheduledCheckIn,
                statusColor = statusUi.color
            )

            CheckInItem(
                title = stringResource(R.string.actual_check_in),
                value = actualCheckIn,
                statusColor = statusUi.color
            )
        }
    }
}