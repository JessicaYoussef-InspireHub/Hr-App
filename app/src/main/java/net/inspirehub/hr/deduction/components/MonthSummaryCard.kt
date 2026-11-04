package net.inspirehub.hr.deduction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.R
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@Composable
fun MonthSummaryCard(
    usedMinutes: Int,
    allowedMinutes: Int,
    remainingMinutes: Int,
    excessMinutes: Int
) {

    val color = appColors()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = stringResource(R.string.this_month_summary),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = color.onBackgroundColor
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                SummaryItem(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.allowed),
                    value = allowedMinutes.toString(),
                    icon = Icons.Outlined.Schedule,
                    iconColor = color.surfaceContainer,
                )

                SummaryItem(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.used),
                    value = usedMinutes.toString(),
                    icon = Icons.Outlined.Timer,
                    iconColor = color.surfaceContainerLow,
                )

                SummaryItem(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.remaining),
                    value = remainingMinutes.toString(),
                    icon = Icons.Outlined.Update,
                    iconColor =color.outline,
                )

                SummaryItem(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.excess),
                    value = excessMinutes.toString(),
                    icon = Icons.Outlined.Timer,
                    iconColor = color.onError,
                )
            }
        }
    }
}