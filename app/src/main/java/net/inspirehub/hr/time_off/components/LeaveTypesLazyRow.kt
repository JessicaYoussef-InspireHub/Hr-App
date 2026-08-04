package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.time_off.data.LeaveType
import androidx.core.graphics.toColorInt
import net.inspirehub.hr.appColors


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LeaveTypesCard(
    leaveTypes: List<LeaveType>
) {
    val colors = appColors()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            2.dp,
            colors.surfaceColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = colors.transparent
        )
    ) {

        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            leaveTypes.forEach { leaveType ->

                val color = leaveType.color?.let {
                    try {
                        Color(it.toColorInt())
                    } catch (_: Exception) {
                        colors.tertiaryColor
                    }
                } ?: colors.tertiaryColor

                LegendItem(
                    color = color,
                    text = leaveType.name
                )
            }
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = text,
            color = appColors().onBackgroundColor
        )
    }
}