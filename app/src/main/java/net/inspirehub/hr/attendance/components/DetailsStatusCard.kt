package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.ScheduleIcon
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceState
import net.inspirehub.hr.utils.formatLocalizedTime
import net.inspirehub.hr.utils.toComposeColor

@Composable
fun DetailsStatusCard(
    states: List<AttendanceState>,
    language: String
) {
    val colors = appColors()

    val sortedStates = states.sortedBy { it.startMinutes }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.inverseSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(
            modifier = Modifier.padding(vertical = 20.dp , horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                ScheduleIcon(size = 38)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.work_entry_details),
                    color = colors.onBackgroundColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            sortedStates.forEachIndexed { index, state ->

                val stateColor = state.workEntryTypeColorHex
                    ?.takeIf { it.isNotBlank() }
                    ?.toComposeColor()
                    ?: colors.tertiaryColor

                val startTime = formatLocalizedTime(
                    hour = state.startMinutes / 60,
                    minute = state.startMinutes % 60,
                    language = language
                )

                val endTime = state.endMinutes?.let {
                    formatLocalizedTime(
                        hour = it / 60,
                        minute = it % 60,
                        language = language
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        if (!state.iconImage.isNullOrBlank()) {
                            WorkEntryTypeImage(
                                base64Image = state.iconImage,
                                backgroundColor = stateColor,
                                size = 20
                            )
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    // Name
                    Text(
                        text = state.workEntryType,
                        modifier = Modifier.weight(1f),
                        color = stateColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    // From - To
                    // From - To
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = startTime,
                            color = colors.onBackgroundColor,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )

                        Text(
                            text = " - ",
                            color = colors.onBackgroundColor.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )

                        Text(
                            text = endTime ?: stringResource(R.string.in_progress),
                            color = colors.onBackgroundColor,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }

                if (index != sortedStates.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            vertical = 12.dp
                        ),
                        color = colors.onBackgroundColor.copy(alpha = 0.10f)
                    )
                }
            }
        }
    }
}