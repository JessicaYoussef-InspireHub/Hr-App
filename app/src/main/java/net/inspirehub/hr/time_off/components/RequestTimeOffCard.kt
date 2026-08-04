package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import net.inspirehub.hr.AddIcon
import net.inspirehub.hr.R

@Composable
fun RequestTimeOffCard(
    onClick: () -> Unit
) {
    val colors = appColors()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .dashedBorder(
                color = colors.surfaceColor,
                strokeWidth = 2.dp,
                cornerRadius = 16.dp
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.transparent
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Card(
                shape = CircleShape,
                border = BorderStroke(
                    1.5.dp,
                    colors.tertiaryColor
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                AddIcon(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column {

                Text(
                    text = stringResource(R.string.request_time_off),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = colors.tertiaryColor
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = stringResource(R.string.create_another_leave_request),
                    fontSize = 13.sp,
                    color = colors.onBackgroundColor.copy(0.6f)
                )
            }
        }
    }
}


fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp = 2.dp,
    cornerRadius: Dp = 16.dp
) = drawBehind {

    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(
            cornerRadius.toPx(),
            cornerRadius.toPx()
        ),
        style = Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(12f, 8f)
            )
        )
    )
}