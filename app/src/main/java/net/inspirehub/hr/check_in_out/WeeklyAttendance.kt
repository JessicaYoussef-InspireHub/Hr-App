package net.inspirehub.hr.check_in_out

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import java.util.Calendar

@Composable
fun WeeklyAttendance() {

    val colors = appColors()
    val days = listOf(
        R.string.sun,
        R.string.mon,
        R.string.tue,
        R.string.wed,
        R.string.thu
    ).map { stringResource(it) }

    // Get current day: Sunday = 0, Monday = 1, etc.
    val todayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1


    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        days.forEachIndexed { index, day ->

            val isToday = index == todayIndex
            val isPastDay = index < todayIndex

            val indicatorColor = when {
                isToday -> colors.onBackgroundColor
                isPastDay -> colors.tertiaryColor
                else -> colors.tertiaryColor.copy(alpha = 0.30f)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )


                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = day,
                    color = indicatorColor,
                    fontSize = 11.sp,
                    fontWeight = if (isToday)
                        FontWeight.ExtraBold
                    else
                        FontWeight.Normal
                )
            }
        }
    }
}