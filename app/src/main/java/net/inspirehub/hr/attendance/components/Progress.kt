package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceState
import net.inspirehub.hr.attendance.presentation.DayStatus
import net.inspirehub.hr.utils.formatNumber
import net.inspirehub.hr.utils.getProgress
import net.inspirehub.hr.utils.toUi


@Composable
fun Progress(
    status: DayStatus,
    attendanceStates: List<AttendanceState>
) {
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    val currentLanguage = sharedPref.getLanguage()
    val colors = appColors()
    val progress = status.getProgress(attendanceStates)
    val color = status.toUi().color

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(12.dp)),
            color = color,
            trackColor = colors.surfaceContainerLowest
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${formatNumber("${(progress * 100).toInt()}", currentLanguage)}%",
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}