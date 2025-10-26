package com.example.myapplicationnewtest.time_off.components

import HourlyTimeOffRecord
import TimeOffRecord
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.time_off.data.fetchEmployeeLeaveTypes


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyCalendarPicker(
    isDialogMode: Boolean = false,
    onDateSelected: ((LocalDate) -> Unit)? = null,
    selectedDates: Set<LocalDate>,
    onDateSelectedChange: (Set<LocalDate>) -> Unit,
    validatedDates: Map<LocalDate, String> = emptyMap(),
    initialMonth: YearMonth = YearMonth.now(),
    token: String,
    onRefreshRequest: () -> Unit,
    dailyRecords: List<TimeOffRecord> = emptyList(),
    hourlyRecords: List<HourlyTimeOffRecord> = emptyList(),
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    weekendDayNames: Set<String> = emptySet(),
    publicHolidayDates: Set<LocalDate> = emptySet(),
    leaveTypeColors: Map<String, Color> = emptyMap()

) {
    var currentMonth by remember { mutableStateOf(initialMonth) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7 // Sunday = 0
    val today = LocalDate.now()
    var showDialogForDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTimeOffRecord by remember { mutableStateOf<TimeOffRecord?>(null) }
    var weekendAndPublicHolidayDialog by remember { mutableStateOf<String?>(null) }
    var showDoubleStateDialog by remember { mutableStateOf(false) }
    var selectedDateForInfoDialog by remember { mutableStateOf<LocalDate?>(null) }
    var selectedLeaveRecords by remember { mutableStateOf<List<TimeOffRecord>>(emptyList()) }
    val context = LocalContext.current
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    var dailyAndHourlyRecords by remember { mutableStateOf<Pair<List<TimeOffRecord>, List<HourlyTimeOffRecord>>?>(null) }
    var permissionDialogRecords by remember { mutableStateOf<List<HourlyTimeOffRecord>?>(null) }
    var doublePermissionDialogRecords by remember { mutableStateOf<List<HourlyTimeOffRecord>?>(null) }

    fun String.replaceDigitsWithArabic(): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return this.map { char ->
            if (char.isDigit()) arabicDigits[char.digitToInt()] else char
        }.joinToString("")
    }


    val dateToStatesMap: Map<LocalDate, Set<String>> =
        dailyRecords.flatMap { record ->
            val start = LocalDate.parse(record.start_date)
            val end = LocalDate.parse(record.end_date)
            val dates = generateSequence(start) { it.plusDays(1) }
                .takeWhile { !it.isAfter(end) }
                .map { it to record.state }
            dates
        }.groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() }

    val yearText = if (Locale.getDefault().language == "ar") {
        currentMonth.year.toString().replaceDigitsWithArabic()
    } else {
        currentMonth.year.toString()
    }


    val dateToHourlyStatesMap: Map<LocalDate, Set<String>> =
        hourlyRecords.flatMap { record ->
            val start = LocalDate.parse(record.leave_day)
            val end = LocalDate.parse(record.leave_day)
            val dates = generateSequence(start) { it.plusDays(1) }
                .takeWhile { !it.isAfter(end) }
                .map { it to record.state }
            dates
        }.groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() }







    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onSecondary)
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .then(if (isDialogMode) Modifier else Modifier.fillMaxWidth())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Month",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        currentMonth = currentMonth.minusMonths(1)
                    },
                tint = tertiaryColor
            )

            Text(
                text = currentMonth.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.getDefault()
                ) + " " + yearText,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = tertiaryColor
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Month",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        currentMonth = currentMonth.plusMonths(1)
                    },
                tint = tertiaryColor
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        // Week Days
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            listOf(
                stringResource(R.string.sun),
                stringResource(R.string.mon),
                stringResource(R.string.tue),
                stringResource(R.string.wed),
                stringResource(R.string.thu),
                stringResource(R.string.fri),
                stringResource(R.string.sat),
            ).forEach {
                Text(
                    text = it,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = tertiaryColor,
                    fontSize = 16.sp
                )
            }
        }

        // Calendar Grid
        val totalCells = firstDayOfWeek + daysInMonth

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(totalCells) { index ->
                if (index < firstDayOfWeek) {
                    Box(modifier = Modifier.size(40.dp)) // Empty space before the 1st
                } else {
                    val day = index - firstDayOfWeek + 1


                    val currentLocale = Locale.getDefault()
                    val dayText = if (currentLocale.language == "ar") {
                        day.toString().replaceDigitsWithArabic()
                    } else {
                        day.toString()
                    }


                    val date = currentMonth.atDay(day)
                    val isToday = date == today
                    val inSelectedRange = if (startDate != null && endDate != null) {
                        val minDate = minOf(startDate, endDate)
                        val maxDate = maxOf(startDate, endDate)
                        !date.isBefore(minDate) && !date.isAfter(maxDate)
                    } else {
                        false
                    }

                    val firstDailyRecord = dailyRecords.find {
                        val start = LocalDate.parse(it.start_date)
                        val end = LocalDate.parse(it.end_date)
                        !date.isBefore(start) && !date.isAfter(end)
                    }

                    val firstHourlyRecord = hourlyRecords.find {
                        val leaveDay = LocalDate.parse(it.leave_day)
                        date == leaveDay
                    }

                    val firstState = firstDailyRecord?.state ?: firstHourlyRecord?.state
                    val firstLeaveType = firstDailyRecord?.leave_type ?: firstHourlyRecord?.leave_type


                    val isWeekendHoliday = weekendDayNames.map { it.lowercase() }
                        .contains(date.dayOfWeek.name.lowercase())

                    val states = dateToStatesMap[date] ?: emptySet()
                    val hourlyStates = dateToHourlyStatesMap[date] ?: emptySet()
                    val isPermission = hourlyStates.isNotEmpty()
                    val isRefusedPermission = hourlyStates.contains("refuse")
                    val isConfirmedPermission = hourlyStates.contains("confirm")
                    val isDraftPermission = hourlyStates.contains("draft")
                    val isApprovedPermission = hourlyStates.contains("validate")

                    val hasMultipleStates = states.size > 1
                    val isRefused = states.contains("refuse")
                    val isConfirmed = states.contains("confirm")
                    val isDraft = states.contains("draft")
                    val isApproved = states.contains("validate")


                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isDialogMode) 40.dp else 50.dp)
                            .border(
                                width = if (isDialogMode && inSelectedRange) 2.dp else 0.dp,
                                color = if (isDialogMode && inSelectedRange) tertiaryColor else Color.Transparent,
                                shape = CircleShape
                            )
//                            .background(
//                                when {
//                                    isWeekendHoliday -> MaterialTheme.colorScheme.onSecondaryContainer
//                                    publicHolidayDates.contains(date) -> MaterialTheme.colorScheme.onSecondaryContainer
//                                    isDialogMode && inSelectedRange -> MaterialTheme.colorScheme.onPrimary
////                                  isRefusedPermission -> MaterialTheme.colorScheme.onSecondaryContainer
//                                    isRefused && isDraft -> MaterialTheme.colorScheme.onSecondaryContainer
//                                    isRefusedPermission && isDraftPermission -> MaterialTheme.colorScheme.onSecondaryContainer
//                                    isRefused && isConfirmed -> MaterialTheme.colorScheme.onSecondaryContainer
//                                    isRefusedPermission && isConfirmedPermission -> MaterialTheme.colorScheme.onSecondaryContainer
//                                    isRefused && isApproved -> MaterialTheme.colorScheme.onSurface
//                                    isRefusedPermission && isApprovedPermission-> MaterialTheme.colorScheme.onSurface
//
//                                    states.contains("confirm") -> MaterialTheme.colorScheme.onSecondaryContainer
//                                    isConfirmedPermission -> MaterialTheme.colorScheme.onSecondaryContainer
//
//                                    states.contains("validate") -> MaterialTheme.colorScheme.onSurface
//                                    isApprovedPermission -> MaterialTheme.colorScheme.onSurface
//
//                                    states.contains("draft") -> MaterialTheme.colorScheme.onSecondaryContainer
//                                    isDraftPermission -> MaterialTheme.colorScheme.onSecondaryContainer
//
//                                    states.contains("refuse") -> Color.Transparent
//                                    isRefusedPermission -> Color.Transparent
//
//                                    isToday -> tertiaryColor
//                                    else -> Color.Transparent
//                                },
//                                shape = when {
//                                    isWeekendHoliday || publicHolidayDates.contains(date) -> RoundedCornerShape(0.dp)
//
//                                    else -> CircleShape
//                                }
//                            )

                            .background(
//                                when {
//                                    isWeekendHoliday -> MaterialTheme.colorScheme.surfaceVariant
//                                    publicHolidayDates.contains(date) -> MaterialTheme.colorScheme.surfaceVariant
//
//                                    states.contains("refuse") || states.contains("refuse") ||
//                                            hourlyStates.contains("refuse") || hourlyStates.contains("refuse") -> Color.Transparent
//
//                                    states.contains("draft") || states.contains("confirm") ||
//                                            hourlyStates.contains("draft") || hourlyStates.contains("confirm") -> Color.Transparent
//
//                                    states.isNotEmpty() -> {
//                                        val leaveType = dailyRecords.find {
//                                            val start = LocalDate.parse(it.start_date)
//                                            val end = LocalDate.parse(it.end_date)
//                                            !date.isBefore(start) && !date.isAfter(end)
//                                        }?.leave_type
//                                        leaveTypeColors[leaveType] ?: MaterialTheme.colorScheme.primaryContainer
//                                    }
//
//                                    hourlyStates.isNotEmpty() -> {
//                                        val leaveType = hourlyRecords.find {
//                                            val leaveDay = LocalDate.parse(it.leave_day)
//                                            date == leaveDay
//                                        }?.leave_type
//                                        leaveTypeColors[leaveType] ?: MaterialTheme.colorScheme.primaryContainer
//                                    }
//
//                                    isToday -> tertiaryColor
//                                    else -> Color.Transparent
//                                },
                                when {
                                    firstState == "refuse" -> Color.Transparent
                                    firstState == "draft" || firstState == "confirm" -> Color.Transparent
                                    firstState == "validate" -> leaveTypeColors[firstLeaveType] ?:Color.Transparent
                                    isWeekendHoliday -> MaterialTheme.colorScheme.surfaceVariant
                                    publicHolidayDates.contains(date) -> MaterialTheme.colorScheme.surfaceVariant
                                    firstState == null && isToday -> tertiaryColor
                                    else -> leaveTypeColors[firstLeaveType] ?:Color.Transparent
                                },
                                shape = when {
                                    isWeekendHoliday || publicHolidayDates.contains(date) -> RoundedCornerShape(0.dp)
                                    else -> CircleShape
                                }
                            )
                            .clickable {

                                val matchedDailyRecords = dailyRecords.filter {
                                    val start = LocalDate.parse(it.start_date)
                                    val end = LocalDate.parse(it.end_date)
                                    !date.isBefore(start) && !date.isAfter(end)
                                }

                                val matchedHourlyRecords = hourlyRecords.filter {
                                    val start = LocalDate.parse(it.leave_day)
                                    val end = LocalDate.parse(it.leave_day)
                                    !date.isBefore(start) && !date.isAfter(end)
                                }

                                val matchedRecords = dailyRecords.filter {
                                    val start = LocalDate.parse(it.start_date)
                                    val end = LocalDate.parse(it.end_date)
                                    !date.isBefore(start) && !date.isAfter(end)
                                }

                                val isPermissionDay = matchedHourlyRecords.isNotEmpty()

                                when {

                                    isDialogMode && isPermissionDay -> {
                                        val newDates = if (selectedDates.contains(date)) {
                                            selectedDates - date
                                        } else {
                                            selectedDates + date
                                        }
                                        onDateSelectedChange(newDates)
                                        onDateSelected?.invoke(date)
                                    }


                                    matchedDailyRecords.isNotEmpty() && matchedHourlyRecords.isNotEmpty() -> {
                                        dailyAndHourlyRecords = matchedDailyRecords to matchedHourlyRecords
                                        showDialogForDate = date
                                    }


                                    isPermission -> {
                                        val matchedRecords = hourlyRecords.filter { record ->
                                            val start = LocalDate.parse(record.leave_day)
                                            val end = LocalDate.parse(record.leave_day)
                                            !date.isBefore(start) && !date.isAfter(end)
                                        }

                                        if (matchedRecords.size > 1) {
                                            doublePermissionDialogRecords = matchedRecords
                                            showDialogForDate = date
                                        } else {
                                            permissionDialogRecords = matchedRecords
                                        }
                                    }





                                    isWeekendHoliday && publicHolidayDates.contains(date) -> {
                                        weekendAndPublicHolidayDialog =
                                            context.getString(R.string.lucky_you_a_weekend_and_a_public_holiday_together)
                                    }

                                    publicHolidayDates.contains(date) -> {
                                        weekendAndPublicHolidayDialog =
                                            context.getString(R.string.no_work_today_its_a_public_holiday_enjoy)
                                    }

                                    isWeekendHoliday -> {
                                        weekendAndPublicHolidayDialog =
                                            context.getString(R.string.time_to_relax_its_a_weekend)
                                    }

                                    matchedRecords.size > 1 -> {
                                        selectedLeaveRecords = matchedRecords
                                        showDoubleStateDialog = true
                                        showDialogForDate = date
                                    }


                                    matchedRecords.size == 1 -> {
                                        val record = matchedRecords.first()
                                        val isRefusedState = record.state == "refuse"

                                        if (isDialogMode && isRefusedState) {
                                            val newDates = if (selectedDates.contains(date)) {
                                                selectedDates - date
                                            } else {
                                                selectedDates + date
                                            }
                                            onDateSelectedChange(newDates)
                                            onDateSelected?.invoke(date)
                                        } else {
                                            selectedTimeOffRecord = record
                                            showDialogForDate = date

                                            val newDates = if (selectedDates.contains(date)) {
                                                selectedDates - date
                                            } else {
                                                selectedDates + date
                                            }
                                            onDateSelectedChange(newDates)
                                        }
                                    }

                                    else -> {
                                        val newDates = if (selectedDates.contains(date)) {
                                            selectedDates - date
                                        } else {
                                            selectedDates + date
                                        }
                                        onDateSelectedChange(newDates)
                                        onDateSelected?.invoke(date)

                                        if (!isDialogMode && !selectedDates.contains(date)) {
                                            selectedDateForInfoDialog = date
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    )
                    {
                        if (firstState == "draft" || firstState == "confirm") {
                            val leaveTypeColor = when {
                                states.isNotEmpty() -> {
                                    val leaveType = dailyRecords.find {
                                        val start = LocalDate.parse(it.start_date)
                                        val end = LocalDate.parse(it.end_date)
                                        !date.isBefore(start) && !date.isAfter(end)
                                    }?.leave_type
                                    leaveTypeColors[leaveType] ?: MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                }

                                hourlyStates.isNotEmpty() -> {
                                    val leaveType = hourlyRecords.find {
                                        val leaveDay = LocalDate.parse(it.leave_day)
                                        date == leaveDay
                                    }?.leave_type
                                    leaveTypeColors[leaveType] ?: MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                }

                                else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                            }

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(
                                        when {
                                            isWeekendHoliday || publicHolidayDates.contains(date) -> RoundedCornerShape(0.dp)
                                            else -> CircleShape
                                        }
                                    )
                            ) {
                                DiagonalLinesIcon(
                                    modifier = Modifier.fillMaxSize(),
                                    lineColor = leaveTypeColor.copy(alpha = 0.3f)
                                )
                            }
                    }

                        Text(
                            text = dayText,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textDecoration = if (validatedDates[date] == "refuse") TextDecoration.LineThrough else TextDecoration.None,
                            fontSize = 16.sp
                        )
                        if (firstState == "refuse") {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .drawBehind {
                                        drawLine(
                                            color = when {
                                                isRefusedPermission -> {
                                                    val leaveType = hourlyRecords.find {
                                                        val leaveDay = LocalDate.parse(it.leave_day)
                                                        date == leaveDay
                                                    }?.leave_type
                                                    leaveTypeColors[leaveType] ?: Color.Transparent
                                                }
                                                isRefused -> {
                                                    val leaveType = dailyRecords.find {
                                                        val start = LocalDate.parse(it.start_date)
                                                        val end = LocalDate.parse(it.end_date)
                                                        !date.isBefore(start) && !date.isAfter(end)
                                                    }?.leave_type
                                                    leaveTypeColors[leaveType] ?: Color.Transparent
                                                }
                                                else -> tertiaryColor
                                            },
                                            start = Offset(0f, size.height / 2),
                                            end = Offset(size.width, size.height / 2),
                                            strokeWidth = 10f
                                        )
                                    }
                            )
                        }
                    }
                }
            }
        }

        doublePermissionDialogRecords?.let { records ->
            DoublePermissionDialog(
                records = records,
                onDismiss = { doublePermissionDialogRecords = null },
                token = token,
                onRefreshRequest = onRefreshRequest,
                dailyRecords = dailyRecords,
                hourlyRecords = hourlyRecords,
                weekendDayNames = weekendDayNames,
                publicHolidayDates = publicHolidayDates,
                clickedDate = showDialogForDate,
                startDate = startDate,
            )
        }


        dailyAndHourlyRecords?.let { (daily, hourly) ->
            DailyAndHourlyDialog(
                dailyRecords = daily,
                hourlyRecords = hourly,
                onDismiss = { dailyAndHourlyRecords = null },
                token = token,
                onRefreshRequest = onRefreshRequest,
                clickedDate = showDialogForDate,


                )
        }


        permissionDialogRecords?.let { records ->
            PermissionDialog(
                records = records,
                onDismiss = { permissionDialogRecords = null },
                token = token,
                onRefreshRequest = onRefreshRequest,
                dailyRecords = dailyRecords,
                hourlyRecords = hourlyRecords,
                weekendDayNames = weekendDayNames,
                publicHolidayDates = publicHolidayDates,
                clickedDate = showDialogForDate,
            )
        }



        if (showDoubleStateDialog && showDialogForDate != null) {
            DoubleStateDialog(
                selectedDate = showDialogForDate!!,
                leaveRecords = selectedLeaveRecords,
                token = token,
                onRefreshRequest = onRefreshRequest,
                clickedDate = showDialogForDate,
                startDate = startDate,
                selectedDates = selectedDates,
                onDateSelectedChange = onDateSelectedChange,
                validatedDates = validatedDates,
                weekendDayNames = weekendDayNames,
                publicHolidayDates = publicHolidayDates,
                dailyRecords = dailyRecords,
                hourlyRecords = hourlyRecords,
                onDismiss = {
                    showDoubleStateDialog = false
                    showDialogForDate = null
                    selectedLeaveRecords = emptyList()

                },
                onConfirm = {
                    showDoubleStateDialog = false
                    showDialogForDate = null
                    selectedLeaveRecords = emptyList()

                }
            )
        }

//
        selectedTimeOffRecord?.let { record ->
            TimeOffDetailsDialog(
                record = record,
                onDismiss = {
                    selectedTimeOffRecord = null
                    showDialogForDate = null
                },
                token = token,
                onRefreshRequest = onRefreshRequest,
                dailyRecords = dailyRecords,
                hourlyRecords = hourlyRecords,
                weekendDayNames = weekendDayNames,
                publicHolidayDates = publicHolidayDates,
                clickedDate = showDialogForDate,
            )
        }

        weekendAndPublicHolidayDialog?.let { message ->
            WeekendAndPublicHolidayDialog(
                message = message,
                onDismiss = { weekendAndPublicHolidayDialog = null }
            )
        }

        println("🎨 MyCalendarPicker: hourlyRecords size = ${hourlyRecords.size}")
        println("🎨 MyCalendarPicker: dailyRecords size = ${dailyRecords.size}")


        selectedDateForInfoDialog?.let { selectedDate ->
            DateInfoDialog(
                date = selectedDate,
                selectedDates = selectedDates,
                onDateSelectedChange = onDateSelectedChange,
                onConfirm = {
                    onDateSelectedChange(selectedDates - selectedDate)
                    selectedDateForInfoDialog = null
                    onRefreshRequest()
                },
                onDiscard = {
                    onDateSelectedChange(selectedDates - selectedDate)
                    selectedDateForInfoDialog = null
                },
                validatedDates = validatedDates,
                token = token,
                onRefreshRequest = onRefreshRequest,
                weekendDayNames = weekendDayNames,
                publicHolidayDates = publicHolidayDates,
                dailyRecords = dailyRecords,
                hourlyRecords = hourlyRecords
            )
        }
    }
}


@Composable
fun DiagonalLinesIcon(modifier: Modifier = Modifier, lineColor: Color) {
    Canvas(modifier = modifier) {
        val lineSpacing = 20f
        var startX = -size.height

        while (startX < size.width) {
            drawLine(
                color = lineColor,
                start = Offset(startX, 0f),
                end = Offset(startX + size.height, size.height),
                strokeWidth = 8f
            )
            startX += lineSpacing
        }
    }
}
