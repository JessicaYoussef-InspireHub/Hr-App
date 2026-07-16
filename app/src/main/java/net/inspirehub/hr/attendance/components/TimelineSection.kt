package net.inspirehub.hr.attendance.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.AttendanceType
import net.inspirehub.hr.attendance.presentation.getDayStatus
import net.inspirehub.hr.utils.formatLocalizedTime
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.R
import net.inspirehub.hr.utils.toUi


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun TimeLineSection(
    day: AttendanceDay
) {

    val colors = appColors()
    val context = LocalContext.current
    val currentLanguage = remember {
        SharedPrefManager(context).getLanguage()
    }

    val states = day.states
    val status = getDayStatus(day)
    val statusUi = status.toUi()

    val dayStart = 7 * 60
    val dayEnd = 21 * 60
    val totalDayMinutes = dayEnd - dayStart

    val timelineHeight = when {
        states.isEmpty() -> 46.dp
        states.size == 1 -> 46.dp
        else -> (states.size * 28 + (states.size - 1) * 6 + 12).dp
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(timelineHeight)
    ) {

        val timelineWidth = maxWidth

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    colors.onSurfaceVariant,
                    RoundedCornerShape(8.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 6.dp),
            verticalArrangement = if (states.size == 1)
                Arrangement.Center
            else
                Arrangement.spacedBy(6.dp)
        ) {

            states.forEach { state ->

                val barColor =
                    if (state.type == AttendanceType.PERMISSION)
                        colors.outline
                    else
                        statusUi.color

                val end = state.endMinutes ?: state.startMinutes

                val startFraction =
                    ((state.startMinutes - dayStart).toFloat() / totalDayMinutes)
                        .coerceIn(0f, 1f)

                val widthFraction =
                    ((end - state.startMinutes).toFloat() / totalDayMinutes)
                        .coerceAtLeast(0.02f)

                val startText = formatLocalizedTime(
                    state.startMinutes / 60,
                    state.startMinutes % 60,
                    currentLanguage
                )

                val endText = state.endMinutes?.let {
                    formatLocalizedTime(
                        it / 60,
                        it % 60,
                        currentLanguage
                    )
                } ?: stringResource(R.string.till_now)


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = timelineWidth * startFraction)
                    ) {

                        Box(
                            modifier = Modifier
                                .width(timelineWidth * widthFraction)
                                .height(28.dp)
                                .background(
                                    barColor,
                                    RoundedCornerShape(6.dp)
                                )
                        )

                        Text(
                            text = "$startText - $endText",
                            color = colors.onSecondaryContainer,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 6.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}