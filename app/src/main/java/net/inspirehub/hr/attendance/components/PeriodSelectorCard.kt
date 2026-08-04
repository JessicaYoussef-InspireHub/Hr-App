package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.DateRangeIcon
import net.inspirehub.hr.KeyboardArrowLeftIcon
import net.inspirehub.hr.KeyboardArrowRightIcon
import net.inspirehub.hr.appColors

@Composable
fun PeriodSelectorCard(
    title: String,
    value: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {

    val colors = appColors()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colors.transparent)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.5.dp,
                color = colors.onBackgroundColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.transparent
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 14.sp,
                color = colors.onBackgroundColor
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                KeyboardArrowLeftIcon(
                    onClick = onPrevious
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    DateRangeIcon()

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = value,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = colors.onBackgroundColor
                    )
                }

                KeyboardArrowRightIcon(
                    onClick = onNext
                )
            }
        }
    }
}