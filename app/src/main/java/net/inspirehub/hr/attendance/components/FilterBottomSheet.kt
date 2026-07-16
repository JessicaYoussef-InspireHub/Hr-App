package net.inspirehub.hr.attendance.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.CloseIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.SmallButtons
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceFilter
import java.time.LocalDate
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.platform.LocalContext
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.utils.formatLocalizedDate
import net.inspirehub.hr.utils.formatLocalizedDateRange
import net.inspirehub.hr.utils.formatNumber
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentDate: LocalDate,
    onCurrentDateChange: (LocalDate) -> Unit,
    selectedFilter: TimeFilter,
    selectedAttendanceFilter: AttendanceFilter,
    onTimeFilterSelected: (TimeFilter) -> Unit,
    onAttendanceFilterSelected: (AttendanceFilter) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    fromDate: LocalDate?,
    toDate: LocalDate?,
    onFromDateClick: () -> Unit,
    onToDateClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = appColors()
    var showDateError by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState( skipPartiallyExpanded = true )
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPref.getLanguage()
    val locale = Locale.forLanguageTag(currentLanguage)

    val value = when (selectedFilter) {

        TimeFilter.DAY ->
            formatLocalizedDate(currentDate.toString(), currentLanguage)

        TimeFilter.WEEK ->
            formatLocalizedDateRange(
                currentDate.with(java.time.DayOfWeek.MONDAY),
                currentDate.with(java.time.DayOfWeek.SUNDAY),
                currentLanguage
            )

        TimeFilter.MONTH ->
            "${currentDate.month.getDisplayName(TextStyle.FULL, locale)} ${
                formatNumber(currentDate.year.toString(), currentLanguage)
            }"

        TimeFilter.QUARTER -> {
            val startMonth = ((currentDate.monthValue - 1) / 3) * 3 + 1

            val start = Month.of(startMonth)
                .getDisplayName(TextStyle.FULL, locale)

            val end = Month.of(startMonth + 2)
                .getDisplayName(TextStyle.FULL, locale)

            "$start - $end ${
                formatNumber(currentDate.year.toString(), currentLanguage)
            }"
        }

        TimeFilter.YEAR ->
            formatNumber(currentDate.year.toString(), currentLanguage)

        TimeFilter.CUSTOM ->
            ""
    }

    val title = when (selectedFilter) {
        TimeFilter.DAY -> stringResource(R.string.select_day)
        TimeFilter.WEEK -> stringResource(R.string.select_week)
        TimeFilter.MONTH -> stringResource(R.string.select_month)
        TimeFilter.QUARTER -> stringResource(R.string.select_quarter)
        TimeFilter.YEAR -> stringResource(R.string.select_year)
        TimeFilter.CUSTOM -> ""
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = colors.surfaceContainerHigh,
        sheetState = sheetState,
        windowInsets = WindowInsets(0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                CloseIcon ( onClick = { onDismiss() } )
            }

            Text(
                text = stringResource(R.string.attendance_status),
                color = colors.onBackgroundColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            AttendanceStatusFilterRow(
                selectedAttendanceFilter = selectedAttendanceFilter,
                onAttendanceFilterSelected = onAttendanceFilterSelected
            )

            Spacer(Modifier.height(35.dp))

            Text(
                text = stringResource(R.string.time_period),
                color = colors.onBackgroundColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            TimeFilterFlowRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { filter ->

                    onTimeFilterSelected(filter)

                    when (filter) {
                        TimeFilter.DAY,
                        TimeFilter.WEEK,
                        TimeFilter.MONTH,
                        TimeFilter.QUARTER,
                        TimeFilter.YEAR -> {
                            onCurrentDateChange(LocalDate.now())
                        }

                        TimeFilter.CUSTOM -> {}
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedFilter == TimeFilter.CUSTOM) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    CustomRange(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.from),
                        value = fromDate,
                        language = currentLanguage,
                        onClick = onFromDateClick
                    )

                    CustomRange(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.to),
                        value = toDate,
                        language = currentLanguage,
                        onClick = onToDateClick
                    )
                }

            } else {

                PeriodSelectorCard(
                    title = title,
                    value = value,

                    onPrevious = {

                        when (selectedFilter) {

                            TimeFilter.DAY ->
                                formatLocalizedDate(currentDate.toString(), currentLanguage)

                            TimeFilter.WEEK ->
                                onCurrentDateChange(currentDate.minusWeeks(1))

                            TimeFilter.MONTH ->
                                onCurrentDateChange(currentDate.minusMonths(1))

                            TimeFilter.QUARTER ->
                                onCurrentDateChange(currentDate.minusMonths(3))

                            TimeFilter.YEAR ->
                                onCurrentDateChange(currentDate.minusYears(1))

                            else -> {}
                        }
                    },

                    onNext = {

                        when (selectedFilter) {

                            TimeFilter.DAY ->
                                onCurrentDateChange(currentDate.plusDays(1))

                            TimeFilter.WEEK ->
                                onCurrentDateChange(currentDate.plusWeeks(1))

                            TimeFilter.MONTH ->
                                onCurrentDateChange(currentDate.plusMonths(1))

                            TimeFilter.QUARTER ->
                                onCurrentDateChange(currentDate.plusMonths(3))

                            TimeFilter.YEAR ->
                                onCurrentDateChange(currentDate.plusYears(1))

                            else -> {}
                        }
                    }
                )
            }

            if (showDateError) {
                Text(
                    text = stringResource(R.string.please_check_the_date_you_entered),
                    color = colors.error,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                )
            }
            Spacer(Modifier.height(60.dp))

            SmallButtons(
                onConfirm = {
                    if (
                        selectedFilter == TimeFilter.CUSTOM &&
                        fromDate != null &&
                        toDate != null &&
                        fromDate.isAfter(toDate)
                    ) {
                        showDateError = true
                    } else {
                        showDateError = false
                        onApply()
                    }
                },
                onDismiss = {
                    onReset()
                },
                confirmButtonText = stringResource(R.string.apply),
                dismissButtonText = stringResource(R.string.reset)
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}