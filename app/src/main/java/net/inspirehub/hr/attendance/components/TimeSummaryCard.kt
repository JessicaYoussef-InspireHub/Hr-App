package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.ScheduleIcon
import net.inspirehub.hr.appColors


@Composable
fun TimeSummaryCard(
    checkIn: String,
    checkOut: String,
    workedHours: String,
    breakTime: String
) {
    val colors = appColors()
    data class TimeSummaryUi(
        val title: String,
        val value: String,
        val icon: ImageVector,
        val color: Color
    )

    val items = listOf(
        TimeSummaryUi(
            stringResource(R.string.check_in),
            checkIn,
            Icons.Default.HowToReg,
            colors.surfaceContainer
        ),
        TimeSummaryUi(
            stringResource(R.string.check_out),
            checkOut,
            Icons.AutoMirrored.Filled.Logout,
            colors.surfaceContainerHighest
        ),
        TimeSummaryUi(
            stringResource(R.string.work_hours),
            workedHours,
            Icons.Outlined.BusinessCenter,
            colors.onError
        ),
        TimeSummaryUi(
            stringResource(R.string.break_time),
            breakTime,
            Icons.Default.Coffee,
            colors.surfaceContainerLow
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.inverseSurface
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                ScheduleIcon(
                    size = 38
                )
                Spacer(Modifier.width(15.dp))
                Text(
                    text = stringResource(R.string.time_summary),
                    color = colors.onBackgroundColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                items.forEachIndexed { index, item ->

                    TimeSummaryItem(
                        title = item.title,
                        value = item.value,
                        imageVector = item.icon,
                        color = item.color
                    )

                    if (index != items.lastIndex) {
                        VerticalDivider(
                            modifier = Modifier.height(50.dp)
                        )
                    }
                }
            }
        }
    }
}