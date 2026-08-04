package net.inspirehub.hr.time_off.components

import HourlyTimeOffRecord
import TimeOffRecord
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.inspirehub.hr.GeneralIcon
import net.inspirehub.hr.KeyboardArrowLeftIcon
import net.inspirehub.hr.KeyboardArrowRightIcon
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.time_off.data.TimeOffRequestForRequestEmployee
import net.inspirehub.hr.time_off.data.sendApiForRequestTimeOff
import net.inspirehub.hr.utils.convertToArabicDigits
import net.inspirehub.hr.utils.formatNumber
import kotlin.math.ceil

data class CalendarStatusIcon(
    val icon: ImageVector,
    val color: Color
)

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
    var selectedDateForInfoDialog by remember { mutableStateOf<LocalDate?>(null) }
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    val currentLanguage = sharedPref.getLanguage()
    val colors = appColors()
    val locale = Locale.forLanguageTag(currentLanguage)
    var recordToDelete by remember { mutableStateOf<Pair<Int?, String?>?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var dayRecords by remember {
        mutableStateOf<Pair<List<TimeOffRecord>, List<HourlyTimeOffRecord>>?>(
            null
        )
    }

    val yearText = if (currentLanguage == "ar") {
        convertToArabicDigits(currentMonth.year.toString())
    } else {
        currentMonth.year.toString()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = if (!isDialogMode) {
            BorderStroke(
                2.dp,
                colors.surfaceColor
            )
        } else null,
        colors = CardDefaults.cardColors(
            containerColor = colors.transparent
        )
    ) {
        Column(
            modifier = Modifier
                .background(if (isDialogMode) colors.surfaceVariant else colors.onSecondaryColor)
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .then(if (isDialogMode) Modifier else Modifier.fillMaxWidth())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                KeyboardArrowLeftIcon(
                    onClick = {
                        currentMonth = currentMonth.minusMonths(1)
                    }
                )

                Text(
                    text = currentMonth.month.getDisplayName(
                        TextStyle.FULL,
                        locale
                    ) + " " + yearText,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.tertiaryColor
                )

                KeyboardArrowRightIcon(
                    onClick = {
                        currentMonth = currentMonth.plusMonths(1)
                    }
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
                        color = colors.onBackgroundColor,
                        fontSize = 16.sp
                    )
                }
            }

            // Calendar Grid
            val totalCells = firstDayOfWeek + daysInMonth
            val totalRows = ceil(totalCells / 7.0).toInt()

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(totalRows) { row ->

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(7) { column ->

                            val index = row * 7 + column

                            Box(
                                modifier = Modifier.weight(1f)
                                    .let { if (isDialogMode) it.height(72.dp) else it },
                                contentAlignment = Alignment.Center
                            ) {
                                if (index >= firstDayOfWeek && index < totalCells) {

                                    val day = index - firstDayOfWeek + 1

                                    val dayText =
                                        if (currentLanguage == "ar")
                                            convertToArabicDigits(day.toString())
                                        else
                                            day.toString()

                                    val date = currentMonth.atDay(day)

                                    val isToday = date == today

                                    val inSelectedRange =
                                        if (startDate != null && endDate != null) {
                                            val minDate = minOf(startDate, endDate)
                                            val maxDate = maxOf(startDate, endDate)
                                            !date.isBefore(minDate) && !date.isAfter(maxDate)
                                        } else { false }

                                    val isWeekendHoliday = weekendDayNames.map { it.lowercase() }
                                        .contains(date.dayOfWeek.name.lowercase())

                                    Column(
                                        modifier = Modifier.fillMaxHeight(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .size(if (isDialogMode) 40.dp else 46.dp)
                                                .border(
                                                    width = when {
                                                        isDialogMode && inSelectedRange -> 2.dp
                                                        isWeekendHoliday || publicHolidayDates.contains(
                                                            date
                                                        ) -> 2.dp

                                                        else -> 0.dp
                                                    },
                                                    color = when {
                                                        isDialogMode && inSelectedRange -> colors.tertiaryColor

                                                        isWeekendHoliday || publicHolidayDates.contains(date) -> colors.onSurface

                                                        else -> Color.Transparent
                                                    },
                                                    shape = when {
                                                        isWeekendHoliday || publicHolidayDates.contains( date ) ->
                                                            RoundedCornerShape(8.dp)

                                                        else -> CircleShape
                                                    }
                                                )

                                                .background(
                                                    when {
                                                        isWeekendHoliday || publicHolidayDates.contains( date ) ->
                                                            if (isDialogMode) colors.surfaceColor else colors.surfaceVariant

                                                        isToday -> colors.tertiaryColor

                                                        else -> colors.transparent
                                                    },
                                                    shape = when {
                                                        isWeekendHoliday || publicHolidayDates.contains( date ) ->
                                                            RoundedCornerShape(8.dp )

                                                        else -> CircleShape
                                                    }
                                                )
                                                .clickable {

                                                    val matchedDailyRecords = dailyRecords.filter {
                                                        it.state != "cancel" &&
                                                                !date.isBefore(LocalDate.parse(it.start_date)) &&
                                                                !date.isAfter(LocalDate.parse(it.end_date))
                                                    }

                                                    val matchedHourlyRecords =
                                                        hourlyRecords.filter {
                                                            it.state != "cancel" &&
                                                                    LocalDate.parse(it.leave_day) == date
                                                        }

                                                    if (!isDialogMode &&
                                                        (matchedDailyRecords.isNotEmpty() || matchedHourlyRecords.isNotEmpty())
                                                    ) {

                                                        showDialogForDate = date
                                                        dayRecords = matchedDailyRecords to matchedHourlyRecords

                                                    } else {
                                                        val newDates =
                                                            if (selectedDates.contains(date))
                                                                selectedDates - date
                                                            else
                                                                selectedDates + date

                                                        onDateSelectedChange(newDates)
                                                        onDateSelected?.invoke(date)

                                                        if (!isDialogMode) {
                                                            selectedDateForInfoDialog = date
                                                        }
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = dayText,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isToday) colors.onSecondaryColor else colors.onBackgroundColor,
                                                fontSize = 16.sp
                                            )
                                        }
                                        val icons = buildList {

                                            dailyRecords
                                                .filter {
                                                    it.state == "validate" &&
                                                            !date.isBefore(LocalDate.parse(it.start_date)) &&
                                                            !date.isAfter(LocalDate.parse(it.end_date))
                                                }
                                                .forEach {
                                                    add(
                                                        CalendarStatusIcon(
                                                            icon = Icons.Outlined.CheckCircleOutline,
                                                            color = leaveTypeColors[it.leave_type]
                                                                ?: colors.tertiaryColor
                                                        )
                                                    )
                                                }


                                            hourlyRecords
                                                .filter {
                                                    it.state == "validate" &&
                                                            LocalDate.parse(it.leave_day) == date
                                                }
                                                .forEach {
                                                    add(
                                                        CalendarStatusIcon(
                                                            icon = Icons.Outlined.CheckCircleOutline,
                                                            color = leaveTypeColors[it.leave_type]
                                                                ?: colors.tertiaryColor
                                                        )
                                                    )
                                                }


                                            dailyRecords
                                                .filter {
                                                    (it.state == "draft" || it.state == "confirm") &&
                                                            !date.isBefore(LocalDate.parse(it.start_date)) &&
                                                            !date.isAfter(LocalDate.parse(it.end_date))
                                                }
                                                .forEach {
                                                    add(
                                                        CalendarStatusIcon(
                                                            icon = Icons.Outlined.Schedule,
                                                            color = leaveTypeColors[it.leave_type]
                                                                ?: colors.tertiaryColor
                                                        )
                                                    )
                                                }


                                            hourlyRecords
                                                .filter {
                                                    (it.state == "draft" || it.state == "confirm") &&
                                                            LocalDate.parse(it.leave_day) == date
                                                }
                                                .forEach {
                                                    add(
                                                        CalendarStatusIcon(
                                                            icon = Icons.Outlined.Schedule,
                                                            color = leaveTypeColors[it.leave_type]
                                                                ?: colors.tertiaryColor
                                                        )
                                                    )
                                                }


                                            dailyRecords
                                                .filter {
                                                    it.state == "refuse" &&
                                                            !date.isBefore(LocalDate.parse(it.start_date)) &&
                                                            !date.isAfter(LocalDate.parse(it.end_date))
                                                }
                                                .forEach {
                                                    add(
                                                        CalendarStatusIcon(
                                                            icon = Icons.Outlined.DoNotDisturbOn,
                                                            color = leaveTypeColors[it.leave_type]
                                                                ?: colors.tertiaryColor
                                                        )
                                                    )
                                                }


                                            hourlyRecords
                                                .filter {
                                                    it.state == "refuse" &&
                                                            LocalDate.parse(it.leave_day) == date
                                                }
                                                .forEach {
                                                    add(
                                                        CalendarStatusIcon(
                                                            icon = Icons.Outlined.DoNotDisturbOn,
                                                            color = leaveTypeColors[it.leave_type]
                                                                ?: colors.tertiaryColor
                                                        )
                                                    )
                                                }
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            icons.take(2).forEach { item ->

                                                GeneralIcon(
                                                    imageVector = item.icon,
                                                    contentDescription = " ",
                                                    tint = item.color,
                                                    modifier = Modifier.size(if(isDialogMode )12.dp else 18.dp)
                                                )
                                            }

                                            if (icons.size > 2) {
                                                Text(
                                                    text = "+${formatNumber((icons.size - 2).toString(), currentLanguage)}",
                                                    color = colors.onBackgroundColor,
                                                    fontSize = if(isDialogMode) 10.sp else 13.sp,
                                                    lineHeight = 10.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            var openAddAnother by remember { mutableStateOf(false) }

            dayRecords?.let { (daily, hourly) ->

                DetailsDayBottomSheet(
                    dailyRecords = daily,
                    hourlyRecords = hourly,
                    onDismiss = {
                        dayRecords = null
                        showDialogForDate = null
                    },
                    onAddAnother = {
                        val date = showDialogForDate
                        dayRecords = null
                        showDialogForDate = null
                        selectedDateForInfoDialog = date
                    },
                    onDelete = { leaveId, date ->

                        recordToDelete = leaveId to date
                        showDeleteConfirmation = true
                    },
                    clickedDate = showDialogForDate,
                    leaveTypeColors = leaveTypeColors
                )
            }

            println("🎨 MyCalendarPicker: hourlyRecords size = ${hourlyRecords.size}")
            println("🎨 MyCalendarPicker: dailyRecords size = ${dailyRecords.size}")

            LaunchedEffect(openAddAnother) {
                if (openAddAnother) {
                    selectedDateForInfoDialog = showDialogForDate
                    openAddAnother = false
                }
            }

            if (showDeleteConfirmation) {

                MyDialog(
                    onDismiss = {
                        showDeleteConfirmation = false
                    },
                    onConfirm = {

                        CoroutineScope(Dispatchers.IO).launch {

                            val request = TimeOffRequestForRequestEmployee(
                                employee_token = token,
                                action = "unlink_draft_annual_leaves",
                                leave_type_id = recordToDelete?.first,
                                leave_id = recordToDelete?.first,
                                request_date_from = recordToDelete?.second
                            )

                            val response = sendApiForRequestTimeOff(context, request)
                            Log.d("API_RESPONSE", response.toString())

                            withContext(Dispatchers.Main) {
                                showDeleteConfirmation = false
                                dayRecords = null
                                onRefreshRequest()
                            }
                        }
                    },
                    title = stringResource(R.string.delete_confirmation),
                    subtitle = stringResource(R.string.are_you_sure_you_want_to_delete_this_request),
                    confirmButtonText = stringResource(R.string.yes_delete),
                    dismissButtonText = stringResource(R.string.cancel)
                )
            }

            selectedDateForInfoDialog?.let { selectedDate ->
                TimeOffRequestBottomSheet(
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
                    hourlyRecords = hourlyRecords,
                    leaveTypeColors = leaveTypeColors,
                )
            }
        }
    }
}