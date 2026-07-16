package net.inspirehub.hr.attendance.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarTab(
    currentMonth: YearMonth,
    days: List<AttendanceDay>,
    onDayClick: (AttendanceDay) -> Unit
) {
    val colors = appColors()

    Column(
        Modifier
            .fillMaxWidth()
            .background(colors.onSecondaryColor)
            .verticalScroll( rememberScrollState() )
    ) {

        CalendarAttendance(
            currentMonth = currentMonth,
            days = days,
            onDayClick = onDayClick
        )
        Spacer(modifier = Modifier.height(20.dp))

        AttendanceSummary(
            days = days
        )
        Spacer(modifier = Modifier.height(40.dp))
    }
}