package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import net.inspirehub.hr.utils.formatNumber
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.attendance.presentation.DayStatus

@Composable
fun AttendanceInfoItem(
    title: String,
    value: String,
    status: DayStatus? = null
) {

    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val language = sharedPref.getLanguage()

    val statusColor = when (status) {
        DayStatus.PRESENT -> colors.surfaceContainer
        DayStatus.LATE -> colors.surfaceContainerLow
        DayStatus.ABSENT -> colors.surfaceContainerHighest
        else -> colors.onBackgroundColor
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(statusColor, CircleShape)
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = title,
            color = colors.onBackgroundColor
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = formatNumber(value , language ) + " " + stringResource(R.string.days),
            color = colors.onBackgroundColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}