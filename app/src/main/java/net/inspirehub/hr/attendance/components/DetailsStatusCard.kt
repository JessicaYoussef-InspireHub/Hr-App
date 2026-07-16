package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.DayStatus
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import net.inspirehub.hr.utils.toUi


data class StatusUi(
    val icon: ImageVector,
    val title: String,
    val message: AnnotatedString,
    val background: Color
)

@Composable
fun DetailsStatusCard(
    status: DayStatus,
    lateMinutes: Int
) {
    val colors = appColors()
    val statusUi = status.toUi()
    val tint = statusUi.color

    val statusCard = when (status) {

        DayStatus.PRESENT ->
            StatusUi(
                statusUi.icon,
                statusUi.text,
                AnnotatedString(stringResource(R.string.your_attendance_was_recorded)),
                statusUi.color.copy(alpha = 0.12f)
            )

        DayStatus.LATE ->
            StatusUi(
                statusUi.icon,
                statusUi.text,
                buildAnnotatedString {
                    append(stringResource(R.string.you_were))

                    withStyle(
                        SpanStyle(
                            color = colors.surfaceContainerLow,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(" $lateMinutes ")
                    }

                    append(
                        stringResource(
                            when (lateMinutes) {
                                1 -> R.string.minute_late
                                2 -> R.string.two_minutes_late
                                else -> R.string.minutes_late
                            }
                        )
                    )
                },
                statusUi.color.copy(alpha = 0.12f)
            )

        DayStatus.ABSENT ->
            StatusUi(
                statusUi.icon,
                statusUi.text,
                AnnotatedString(stringResource(R.string.you_were_absent_on_this_day)),
                statusUi.color.copy(alpha = 0.12f)
            )

        DayStatus.IN_PROGRESS ->
            StatusUi(
                statusUi.icon,
                statusUi.text,
                AnnotatedString(stringResource(R.string.you_are_currently_checked_in_your_attendance_will_be_completed_after_check_out)),
                statusUi.color.copy(alpha = 0.12f)
            )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statusCard.background
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(tint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusCard.icon,
                    contentDescription = statusCard.title,
                    tint = colors.onBackgroundColor
                )
            }
            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = statusCard.title,
                    color = tint,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = statusCard.message,
                    color = colors.onBackgroundColor.copy(alpha = 0.75f)
                )
            }
        }
    }
}