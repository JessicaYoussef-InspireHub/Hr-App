package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors


@Composable
fun AttendanceSummaryItemRow(
    name: String,
    count: Int,
    color: Color?
) {

    val colors = appColors()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = color ?: colors.onBackgroundColor.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(50)
                )
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = name,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = colors.onBackgroundColor
        )

        Text(
            text = count.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackgroundColor
        )
    }
}