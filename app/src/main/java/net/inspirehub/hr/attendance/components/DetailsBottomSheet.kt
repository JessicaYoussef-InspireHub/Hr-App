package net.inspirehub.hr.attendance.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.CloseIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.getDayStatus
import net.inspirehub.hr.utils.formatLocalizedDate
import net.inspirehub.hr.utils.formatLocalizedTime
import net.inspirehub.hr.utils.getLocalizedWorkedTime
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsBottomSheet(
    day: AttendanceDay,
    onDismiss: () -> Unit
) {
    val colors = appColors()
    val status = getDayStatus(day)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPref.getLanguage()
    val attendanceStates = day.states.filter {  it.workEntryType.equals("Attendance", true)}
    val firstAttendance = attendanceStates.minByOrNull { it.startMinutes }
    val lastAttendance = attendanceStates.filter { it.endMinutes != null }.maxByOrNull { it.endMinutes!! }
    val scrollState = rememberScrollState()
    val totalWorkedHours = attendanceStates.sumOf { it.workedHoursPercentage }
    val workedHoursCount = totalWorkedHours.toInt()
    val workedMinutesCount = ((totalWorkedHours - workedHoursCount) * 60).toInt()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = colors.surfaceContainerHigh,
        contentWindowInsets = { WindowInsets(0) },
        sheetState = sheetState
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(bottom = 40.dp)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                CloseIcon(onClick = { onDismiss() })
            }

            Text(
                text = stringResource(R.string.daily_attendance_summary),
                color = colors.onBackgroundColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = formatLocalizedDate(day.date, currentLanguage),
                color = colors.onBackgroundColor.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            DetailsStatusCard(
                lateMinutes = 0,
                status = status
            )

            Spacer(Modifier.height(12.dp))

            PermissionCard(
                hasPermission = day.hasPermission
            )

            Spacer(Modifier.height(12.dp))

            TimeSummaryCard(
                checkIn = firstAttendance?.let {
                    formatLocalizedTime(
                        hour = it.startMinutes / 60,
                        minute = it.startMinutes % 60,
                        language = currentLanguage
                    )
                } ?: "--:--",

                checkOut = lastAttendance?.let {
                    formatLocalizedTime(
                        hour = it.endMinutes!! / 60,
                        minute = it.endMinutes % 60,
                        language = currentLanguage
                    )
                } ?: "--:--",

                workedHours = getLocalizedWorkedTime(
                    hours = workedHoursCount,
                    minutes = workedMinutesCount,
                    language = currentLanguage
                ),

                breakTime = getLocalizedWorkedTime(1, 30, currentLanguage)
            )

            Spacer(Modifier.height(12.dp))

            PunctualityCard(
                day = day,
                15
            )

            Spacer(Modifier.height(12.dp))

            SessionsCard(day = day)
        }
    }
}