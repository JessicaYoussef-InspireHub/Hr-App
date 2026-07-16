package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun StatusBadge(
    roundedCorner: Int ,
    text: String,
    color: Color,
    fontSize: Int,
    modifier: Modifier
) {
    Surface(
        shape = RoundedCornerShape(roundedCorner.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.sp,
            modifier = modifier
        )
    }
}