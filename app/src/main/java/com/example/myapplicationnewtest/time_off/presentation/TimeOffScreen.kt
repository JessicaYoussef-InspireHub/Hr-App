package com.example.myapplicationnewtest.time_off.presentation

import RemainingLeavesResponse
import SendApiForTimeOff
import TimeOffRequest
import TimeOffStatusResponse
import TimeOffYearResponse
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplicationnewtest.BottomBar
import com.example.myapplicationnewtest.MyAppBar
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.time_off.components.LeaveTypesLazyRow
import com.example.myapplicationnewtest.time_off.components.MyCalendarPicker
import com.example.myapplicationnewtest.time_off.components.MyActualTimeOff
import com.example.myapplicationnewtest.time_off.components.Shapes
import com.example.myapplicationnewtest.time_off.data.LeaveType
import com.example.myapplicationnewtest.time_off.data.fetchAndPrintHolidays
import com.example.myapplicationnewtest.time_off.data.fetchEmployeeLeaveTypes
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeOffScreen(
    navController: NavController
) {
    var selectedDates by remember { mutableStateOf(setOf<LocalDate>()) }

    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val token = remember { sharedPrefManager.getToken() ?: "No token found" }

    var remainingLeavesData by remember { mutableStateOf<RemainingLeavesResponse?>(null) }
    var annualLeaveRemaining by remember { mutableStateOf("0") }

    var yearTimeOffData by remember { mutableStateOf<TimeOffYearResponse?>(null) }
    var timeOffStatusResponse by remember { mutableStateOf<TimeOffStatusResponse?>(null) }
    var monthTimeOffData by remember { mutableStateOf<TimeOffYearResponse?>(null) }
    val validatedDates = remember { mutableStateOf<Map<LocalDate, String>>(emptyMap()) }

    var permissionRemainingHours by remember { mutableStateOf("0") }

    val isLoading = remember { mutableStateOf(false) }

    var shouldRefresh by remember { mutableStateOf(false) }

    var holidayText by remember { mutableStateOf("") }

    var weekendDayNames by remember { mutableStateOf<Set<String>>(emptySet()) }

    var officialHolidayText by remember { mutableStateOf("") }

    var publicHolidayDates by remember { mutableStateOf<Set<LocalDate>>(emptySet()) }

    val leaveTypesState = remember { mutableStateOf<List<LeaveType>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()
    val leaveTypes = leaveTypesState.value
    val leaveTypeColors = remember { mutableStateMapOf<String, Color>() }

    fun triggerRefresh() {
        shouldRefresh = !shouldRefresh
    }


    LaunchedEffect(shouldRefresh) {
        isLoading.value = true

        try {
            val leaveTypesResponse = fetchEmployeeLeaveTypes(token)
            leaveTypesResponse?.let {
                leaveTypesState.value = it.result.leave_types

                it.result.leave_types.forEach { leaveType ->
                    val colorHex = leaveType.color ?: "#808080"
                    leaveTypeColors[leaveType.name] = Color(android.graphics.Color.parseColor(colorHex))
                }
            }

            val result = fetchAndPrintHolidays(token)

            holidayText = result.weekendText
            weekendDayNames = result.weekendDays.toSet()
            officialHolidayText = result.holidaysText
            publicHolidayDates = result.publicHolidayDates

            println("weekendDayNames = $weekendDayNames")
            println("publicHolidayDates = $publicHolidayDates")

            val monthResult = SendApiForTimeOff(
                timeOffRequest = TimeOffRequest(
                    employee_token = token,
                    action = "this_month_time_off"
                )
            )

            if (monthResult is TimeOffYearResponse) {
                monthTimeOffData = monthResult
                Log.i("TimeOffScreen", "this_month_time_off = $monthResult")
            }

            val remainingResult = SendApiForTimeOff(
                timeOffRequest = TimeOffRequest(
                    employee_token = token,
                    action = "remaining_leaves"
                )
            )

            if (remainingResult is RemainingLeavesResponse) {
                remainingLeavesData = remainingResult
                val annual = remainingResult.leave_summary.find {
                    it.leave_type.equals("annual leave", ignoreCase = true)
                }
                annualLeaveRemaining = annual?.remaining_days?.toString() ?: "0"

                val permission = remainingResult.permission_summary.find {
                    it.leave_type.equals("permission", ignoreCase = true)
                }
                permissionRemainingHours = permission?.remaining_hours?.toString() ?: "0"

                println("📋 Remaining Leaves Summary:")
                remainingResult.leave_summary.forEach { summary ->
                    println("🔹 ${summary.leave_type}: Allocated = ${summary.allocated_days}, Used = ${summary.used_days}, Remaining = ${summary.remaining_days}")
                }

                println("📋 Permission Summary:")
                remainingResult.permission_summary.forEach { permission ->
                    println("🔸 ${permission.leave_type}: Allocated = ${permission.allocated_hours}, Used = ${permission.used_hours}, Remaining = ${permission.remaining_hours}")
                }

                println("📅 Year: ${remainingResult.year}")
                println("📊 Total Days - Allocated: ${remainingResult.total_allocated_days}, Used: ${remainingResult.total_used_days}, Remaining: ${remainingResult.total_remaining_days}")
                println("🕐 Total Hours - Allocated: ${remainingResult.total_allocated_hours}, Used: ${remainingResult.total_used_hours}, Remaining: ${remainingResult.total_remaining_hours}")
            }


            val timeOffStatus = SendApiForTimeOff(
                timeOffRequest = TimeOffRequest(
                    employee_token = token,
                    action = "time_off_status"
                )
            )

            if (timeOffStatus is TimeOffStatusResponse) {
                timeOffStatusResponse = TimeOffStatusResponse(
                    status = timeOffStatus.status,
                    records = timeOffStatus.records
                )

                Log.i("TimeOffScreen", "✅ time_off_status loaded")

                val validated = timeOffStatus.records.daily_records
                    .filter { it.state in listOf("confirm", "draft", "validate", "refuse") }
                    .flatMap { record ->
                        val start = LocalDate.parse(record.start_date)
                        val end = LocalDate.parse(record.end_date)
                        val state = record.state
                        generateSequence(start) { date ->
                            if (date < end) date.plusDays(1) else null
                        }.plus(end).map { date -> date to state }
                    }.toMap()

                validatedDates.value = validated
            }


            val yearResult = SendApiForTimeOff(
                timeOffRequest = TimeOffRequest(
                    employee_token = token,
                    action = "this_year_time_off"
                )
            )

            if (yearResult is TimeOffYearResponse) {
                yearTimeOffData = yearResult

                Log.i("TimeOffScreen", "this_year_time_off = $yearResult")

                val validated = yearResult.records.daily_records
                    .filter { it.state in listOf("confirm", "draft", "validate", "refuse") }
                    .flatMap { record ->
                        val start = LocalDate.parse(record.start_date)
                        val end = LocalDate.parse(record.end_date)
                        val state = record.state
                        generateSequence(start) { date ->
                            if (date < end) date.plusDays(1) else null
                        }.plus(end).map { date -> date to state }
                    }.toMap()

                validatedDates.value = validated
            }
        } catch (e: Exception) {
            Log.e("TimeOffScreen", "Error loading time off data", e)
        } finally {
            isLoading.value = false
        }
    }


    if (isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onSecondary),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.onSecondary,
            topBar = {
                MyAppBar(
                    label = stringResource(R.string.time_off_screen),
                )
            },
            bottomBar = {
                BottomBar(navController = navController)
            }
        ){
            paddingValues ->
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSecondary)
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
//                LeaveTypesList(leaveTypes = leaveTypesState.value)
//                if (holidayText.isNotEmpty()) {
//                    Text(
//                        text = holidayText,
//                        modifier = Modifier.padding(16.dp),
//                        color = Color(0xFF333333)
//                    )
//                }
//                if (officialHolidayText.isNotEmpty()) {
//                    Text(
//                        text = officialHolidayText,
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        color = Color(0xFF555555)
//                    )
//                }


//                Button(
//                    onClick = {
//                        coroutineScope.launch {
//                            fetchAndPrintHolidays(token)
//                        }
//                    },
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text("Weekend")
//                }

//
//            Button(
//                onClick = {
//                    coroutineScope.launch {
//                        SendApiForTimeOff(
//                            timeOffRequest = TimeOffRequest(
//                                employee_token = token,
//                                action = "this_month_time_off"
//                            ),
//                        )
//                    }
//                }
//            ) {
//                Text("this_month_time_off")
//            }

//
//            Button(
//                onClick = {
//                    coroutineScope.launch {
//                        SendApiForTimeOff(
//                            timeOffRequest = TimeOffRequest(
//                                employee_token = token,
//                                action = "this_year_time_off"
//                            )
//                        )
//                    }
//                }
//            ) {
//                Text("this_year_time_off")
//            }
//
//
//            Button(
//                onClick = {
//                    coroutineScope.launch {
//                        val result = SendApiForTimeOff(
//                            timeOffRequest = TimeOffRequest(
//                                employee_token = token,
//                                action = "remaining_leaves"
//                            )
//                        )
//
//                        if (result is RemainingLeavesResponse) {
//                            remainingLeavesData = result
//
//                            val annual = result.leave_summary.find {
//                                it.leave_type.equals("annual leave", ignoreCase = true)
//                            }
//                            annualLeaveRemaining = annual?.remaining_days?.toString() ?: "0"
//
//                            val permission = result.permission_summary.find {
//                                it.leave_type.equals("permission", ignoreCase = true)
//                            }
//                            permissionRemainingHours =
//                                permission?.remaining_hours?.toString() ?: "0"
//
//                            println("📋 Remaining Leaves Summary:")
//                            result.leave_summary.forEach { summary ->
//                                println("🔹 ${summary.leave_type}: Allocated = ${summary.allocated_days}, Used = ${summary.used_days}, Remaining = ${summary.remaining_days}")
//                            }
//
//                            println("📋 Permission Summary:")
//                            result.permission_summary.forEach { permission ->
//                                println("🔸 ${permission.leave_type}: Allocated = ${permission.allocated_hours}, Used = ${permission.used_hours}, Remaining = ${permission.remaining_hours}")
//                            }
//
//                            println("📅 Year: ${result.year}")
//                            println("📊 Total Days - Allocated: ${result.total_allocated_days}, Used: ${result.total_used_days}, Remaining: ${result.total_remaining_days}")
//                            println("🕐 Total Hours - Allocated: ${result.total_allocated_hours}, Used: ${result.total_used_hours}, Remaining: ${result.total_remaining_hours}")
//                        }
//                    }
//                }
//            ) {
//                Text("remaining_leaves")
//            }
                MyActualTimeOff(
                    leaveTypes = leaveTypesState.value
                )

                Spacer(modifier = Modifier.height(0.dp))
                MyCalendarPicker(
                    selectedDates = selectedDates,
                    onDateSelectedChange = { selectedDates = it },
                    validatedDates = validatedDates.value,
                    token = token,
                    onRefreshRequest = { triggerRefresh() },
                    dailyRecords = yearTimeOffData?.records?.daily_records ?: emptyList(),
                    hourlyRecords = timeOffStatusResponse?.records?.hourly_records ?: emptyList(),
                    weekendDayNames = weekendDayNames,
                    publicHolidayDates = publicHolidayDates,
                    leaveTypeColors = leaveTypeColors
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LeaveTypesLazyRow(leaveTypes)
                    Shapes()
                }
            }
        }
    }
}
