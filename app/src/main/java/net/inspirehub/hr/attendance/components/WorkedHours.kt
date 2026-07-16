package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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


@Composable
fun WorkedHours(
    workedText: String,
    size: Int,
    showIcon: Boolean = true
) {
    val colors = appColors()

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            ScheduleIcon()
        }

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(R.string.hours_worked),
            fontWeight = FontWeight.SemiBold,
            fontSize = size.sp,
            color = colors.onBackgroundColor
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = workedText,
            fontSize = size.sp,
            color = colors.onBackgroundColor
        )
    }
}