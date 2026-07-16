package net.inspirehub.hr.attendance.components

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors

@Composable
fun CheckInItem(
    title: String,
    value: String,
    statusColor: Color
) {
    val colors = appColors()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(statusColor)
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = title,
            color = colors.onBackgroundColor.copy(alpha = 0.7f)
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = colors.onBackgroundColor
        )
    }
}