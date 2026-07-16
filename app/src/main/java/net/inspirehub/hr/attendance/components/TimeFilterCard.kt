package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.CheckIcon
import net.inspirehub.hr.appColors

@Composable
fun TimeFilterCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val colors = appColors()

    Box(
        modifier = Modifier
            .width(95.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected)
                    colors.tertiaryColor
                else
                    colors.onBackgroundColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {

        if (selected) {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .size(14.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        colors.tertiaryColor,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                CheckIcon(
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = colors.onBackgroundColor
        )
    }
}
