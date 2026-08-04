package net.inspirehub.hr.attendance.presentation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import net.inspirehub.hr.FilterAltIcon
import net.inspirehub.hr.attendance.components.AttendanceTabRow
import java.time.LocalDate
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.GridViewIcon
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.ViewAgendaIcon
import net.inspirehub.hr.ViewListIcon
import net.inspirehub.hr.attendance.components.AttendanceEmptyState
import net.inspirehub.hr.attendance.components.CalendarTab
import net.inspirehub.hr.attendance.components.FilterBottomSheet
import net.inspirehub.hr.attendance.components.DetailsBottomSheet
import net.inspirehub.hr.attendance.components.LargeCard
import net.inspirehub.hr.attendance.components.GridCard
import net.inspirehub.hr.attendance.components.ListCard
import net.inspirehub.hr.attendance.components.FilterTitle
import net.inspirehub.hr.attendance.components.TimeFilter
import net.inspirehub.hr.attendance.components.TimelineTab
import net.inspirehub.hr.attendance.data.AttendanceResponse
import net.inspirehub.hr.attendance.data.fetchAttendance
import net.inspirehub.hr.attendance.data.toAttendanceDays
import net.inspirehub.hr.expenses.components.ExpenseCalendar
import java.time.YearMonth


data class AttendanceState(
    val startMinutes: Int,
    val endMinutes: Int?,
    val workedHoursAndMinutes: String,
    val workedHoursPercentage: Double,
    val workEntryType : String
)

enum class AttendanceView {
    LIST,
    CARD,
    GRID
}

enum class AttendanceType {
    ATTENDANCE,
    PERMISSION
}


data class AttendanceDay(
    val date: String,
    val states: List<AttendanceState>,
    val hasPermission: Boolean = false
)

fun getDayStatus(day: AttendanceDay): DayStatus {

    if (day.states.isEmpty()) {
        return DayStatus.ABSENT
    }

    val hasOpenAttendance = day.states.any { it.endMinutes == null }

    if (hasOpenAttendance) {
        return DayStatus.IN_PROGRESS
    }
    return DayStatus.PRESENT
}

enum class DayStatus {
    PRESENT,
    LATE,
    ABSENT,
    IN_PROGRESS
}

enum class AttendanceFilter {
    ALL,
    PRESENT,
    LATE,
    ABSENT
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("AutoboxingStateCreation")
@Composable
fun AttendanceScreen(
    navController: NavController
) {
    val colors = appColors()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        stringResource(R.string.list),
        stringResource(R.string.timeline),
        stringResource(R.string.calendar)
    )

    var selectedDay by remember { mutableStateOf<AttendanceDay?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(TimeFilter.MONTH) }
    var selectedAttendanceFilter by remember { mutableStateOf(AttendanceFilter.ALL) }
    var attendanceView by rememberSaveable { mutableStateOf(AttendanceView.LIST) }
    var tempSelectedFilter by remember { mutableStateOf(selectedFilter) }
    var tempSelectedAttendanceFilter by remember { mutableStateOf(selectedAttendanceFilter) }
    var showFromCalendar by remember { mutableStateOf(false) }
    var showToCalendar by remember { mutableStateOf(false) }
    var selectedRange by remember { mutableStateOf(LocalDate.now()) }
    var tempSelectedRange by remember { mutableStateOf(selectedRange) }
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    var attendanceResponse by remember { mutableStateOf<AttendanceResponse?>(null) }
    var fromDate by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var isLoading by remember { mutableStateOf(true) }
    val allDays = remember(attendanceResponse) { attendanceResponse?.toAttendanceDays() ?: emptyList() }
    var toDate by remember { mutableStateOf( LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())) }
    var isFilterApplied by rememberSaveable { mutableStateOf(false) }
    var tempFromDate by remember { mutableStateOf(fromDate) }
    var tempToDate by remember { mutableStateOf(toDate) }

    val (apiFromDate, apiToDate) = remember(
        selectedFilter,
        selectedRange,
        fromDate,
        toDate
    ) {
        when (selectedFilter) {

            TimeFilter.DAY -> {
                selectedRange to selectedRange
            }

            TimeFilter.WEEK -> {
                val start = selectedRange.with(java.time.DayOfWeek.MONDAY)
                val end = selectedRange.with(java.time.DayOfWeek.SUNDAY)
                start to end
            }

            TimeFilter.MONTH -> {
                selectedRange.withDayOfMonth(1) to
                        selectedRange.withDayOfMonth(selectedRange.lengthOfMonth())
            }

            TimeFilter.QUARTER -> {
                val startMonth = ((selectedRange.monthValue - 1) / 3) * 3 + 1

                val start = LocalDate.of(
                    selectedRange.year,
                    startMonth,
                    1
                )

                val end = start.plusMonths(2)
                    .withDayOfMonth(start.plusMonths(2).lengthOfMonth())

                start to end
            }

            TimeFilter.YEAR -> {
                LocalDate.of(selectedRange.year, 1, 1) to
                        LocalDate.of(selectedRange.year, 12, 31)
            }

            TimeFilter.CUSTOM -> {
                fromDate to toDate
            }
        }
    }

    LaunchedEffect(apiFromDate, apiToDate) {
        isLoading = true

        attendanceResponse = fetchAttendance(
            context = context,
            token = sharedPref.getToken(),
            fromDate = apiFromDate.toString(),
            toDate = apiToDate.toString()
        )

        isLoading = false
    }


    fun applyFilters(
        list: List<AttendanceDay>,
        timeFilter: TimeFilter,
        attendanceFilter: AttendanceFilter
    ): List<AttendanceDay> {

        val timeFiltered = when (timeFilter) {
            TimeFilter.DAY,
            TimeFilter.WEEK,
            TimeFilter.MONTH,
            TimeFilter.QUARTER,
            TimeFilter.YEAR,
            TimeFilter.CUSTOM -> {
                list
            }
        }

        return when (attendanceFilter) {

            AttendanceFilter.ALL -> timeFiltered

            AttendanceFilter.PRESENT -> {
                timeFiltered.filter {
                    getDayStatus(it) == DayStatus.PRESENT
                }
            }

            AttendanceFilter.LATE -> {
                timeFiltered.filter {
                    getDayStatus(it) == DayStatus.LATE
                }
            }

            AttendanceFilter.ABSENT -> {
                timeFiltered.filter {
                    getDayStatus(it) == DayStatus.ABSENT
                }
            }
        }
    }

    val filteredDays = remember(
        selectedFilter,
        selectedAttendanceFilter,
        selectedRange,
        allDays
    ) {
        applyFilters(
            allDays,
            selectedFilter,
            selectedAttendanceFilter
        )
    }

    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = {
            MyAppBar(
                label = stringResource(R.string.AttendanceHistory),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (selectedTab == 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ViewListIcon(
                            color = if (attendanceView == AttendanceView.LIST) {
                                colors.tertiaryColor
                            } else {
                                colors.onBackgroundColor
                            },
                            onClick = {
                                attendanceView = AttendanceView.LIST
                            }
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        GridViewIcon(
                            color = if (attendanceView == AttendanceView.GRID) {
                                colors.tertiaryColor
                            } else {
                                colors.onBackgroundColor
                            },
                            onClick = {
                                attendanceView = AttendanceView.GRID
                            }
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        ViewAgendaIcon(
                            color = if (attendanceView == AttendanceView.CARD) {
                                colors.tertiaryColor
                            } else {
                                colors.onBackgroundColor
                            },
                            onClick = {
                                attendanceView = AttendanceView.CARD
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                BottomBar(navController = navController)
            }
        }
    )
    { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.onSecondaryColor)
                .padding(innerPadding)
                .padding(vertical = 5.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center

        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Box(modifier = Modifier.weight(1f)) {
                    AttendanceTabRow(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        tabs = tabs
                    )
                }
                Box(
                    modifier = Modifier
                        .background(colors.surfaceContainerHigh, CircleShape)
                        .clickable {
                            tempSelectedFilter = selectedFilter
                            tempSelectedAttendanceFilter = selectedAttendanceFilter
                            tempSelectedRange = selectedRange
                            showFilterSheet = true
                        }
                ) {
                    FilterAltIcon(
                        isActive = showFilterSheet || isFilterApplied
                    )
                }

            }

            Spacer(modifier = Modifier.height(10.dp))

            FilterTitle(
                currentDate = selectedRange,
                selectedFilter = selectedFilter,
                fromDate = fromDate,
                toDate = toDate,
                onPrevious = {
                    when (selectedFilter) {
                        TimeFilter.DAY -> selectedRange = selectedRange.minusDays(1)
                        TimeFilter.WEEK -> selectedRange = selectedRange.minusWeeks(1)
                        TimeFilter.MONTH -> selectedRange = selectedRange.minusMonths(1)
                        TimeFilter.QUARTER -> selectedRange = selectedRange.minusMonths(3)
                        TimeFilter.YEAR -> selectedRange = selectedRange.minusYears(1)
                        TimeFilter.CUSTOM -> {}
                    }
                },
                onNext = {
                    when (selectedFilter) {
                        TimeFilter.DAY -> selectedRange = selectedRange.plusDays(1)
                        TimeFilter.WEEK -> selectedRange = selectedRange.plusWeeks(1)
                        TimeFilter.MONTH -> selectedRange = selectedRange.plusMonths(1)
                        TimeFilter.QUARTER -> selectedRange = selectedRange.plusMonths(3)
                        TimeFilter.YEAR -> selectedRange = selectedRange.plusYears(1)
                        TimeFilter.CUSTOM -> {}
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        FullLoading()
                    }
                }

                filteredDays.isEmpty() -> {
                    AttendanceEmptyState()
                }

                else -> {
                    when (selectedTab) {

                        0 -> {

                            val sampleCards = filteredDays
                            when (attendanceView) {

                                AttendanceView.LIST -> {

                                    if (sampleCards.isEmpty()) {
                                        AttendanceEmptyState()

                                    } else {

                                        LazyColumn(
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {

                                            items(sampleCards) { day ->

                                                ListCard(
                                                    day = day,
                                                    onClick = {
                                                        selectedDay = it
                                                        showBottomSheet = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                AttendanceView.GRID -> {

                                    if (sampleCards.isEmpty()) {
                                        AttendanceEmptyState()
                                    } else {
                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(2),
                                            modifier = Modifier.fillMaxSize(),
                                            contentPadding = PaddingValues(0.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {

                                            items(sampleCards.size) { index ->

                                                val day = sampleCards[index]

                                                GridCard(
                                                    day = day,
                                                    onClick = {
                                                        selectedDay = it
                                                        showBottomSheet = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                AttendanceView.CARD -> {
                                    if (sampleCards.isEmpty()) {
                                        AttendanceEmptyState()
                                    } else {
                                        LazyColumn(
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            items(sampleCards) { day ->

                                                LargeCard(
                                                    day = day,
                                                    onClick = {
                                                        selectedDay = it
                                                        showBottomSheet = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                            }
                        }

                        1 -> {
                            TimelineTab(
                                days = filteredDays,
                                onClick = {
                                    selectedDay = it
                                    showBottomSheet = true
                                }

                            )
                        }

                        2 -> {
                            CalendarTab(
                                currentMonth = YearMonth.from(selectedRange),
                                days = filteredDays,
                                onDayClick = { day ->
                                    selectedDay = day
                                    showBottomSheet = true
                                },
                                totalWorkedHours = attendanceResponse?.total_worked_hours ?: 0.0,
                                totalExpectedHours = attendanceResponse?.expected_worked ?: 0.0,
                            )
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                currentDate = tempSelectedRange,
                onCurrentDateChange = {
                    tempSelectedRange = it
                },
                selectedFilter = tempSelectedFilter,
                selectedAttendanceFilter = tempSelectedAttendanceFilter,
                onTimeFilterSelected = {
                    tempSelectedFilter = it
                },
                onAttendanceFilterSelected = {
                    tempSelectedAttendanceFilter = it
                },
                fromDate = tempFromDate,
                toDate = tempToDate,
                onFromDateClick = {
                    showFromCalendar = true
                },
                onToDateClick = {
                    showToCalendar = true
                },
                onApply = {
                    selectedFilter = tempSelectedFilter
                    selectedAttendanceFilter = tempSelectedAttendanceFilter

                    selectedRange = tempSelectedRange

                    fromDate = tempFromDate
                    toDate = tempToDate

                    isFilterApplied = true
                    showFilterSheet = false
                },
                onReset = {
                    tempSelectedFilter = TimeFilter.MONTH
                    tempSelectedAttendanceFilter = AttendanceFilter.ALL

                    tempSelectedRange = LocalDate.now()

                    tempFromDate = LocalDate.now().withDayOfMonth(1)
                    tempToDate =
                        LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())

                    selectedFilter = tempSelectedFilter
                    selectedAttendanceFilter = tempSelectedAttendanceFilter
                    selectedRange = tempSelectedRange
                    fromDate = tempFromDate
                    toDate = tempToDate

                    isFilterApplied = false
                    showFilterSheet = false
                },
                onDismiss = {
                    tempSelectedRange = selectedRange
                    showFilterSheet = false
                }
            )
        }

        if (showFromCalendar) {
            ExpenseCalendar(
                initialDate = fromDate,
                onDismiss = {
                    showFromCalendar = false
                },
                onDateSelected = {
                    tempFromDate = it
                }
            )
        }

        if (showToCalendar) {
            ExpenseCalendar(
                initialDate = toDate,
                onDismiss = {
                    showToCalendar = false
                },
                onDateSelected = {
                    tempToDate = it
                }
            )
        }

        if (showBottomSheet && selectedDay != null) {
            DetailsBottomSheet(
                day = selectedDay!!,
                onDismiss = {
                    showBottomSheet = false
                }
            )
        }
    }
}