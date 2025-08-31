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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplicationnewtest.MyAppBar
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.time_off.components.ColorsOfTimeOff
import com.example.myapplicationnewtest.time_off.components.MyCalendarPicker
import com.example.myapplicationnewtest.time_off.components.MyActualTimeOff
import com.example.myapplicationnewtest.time_off.data.fetchAndPrintHolidays
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
    var TimeOffStatusResponse by remember { mutableStateOf<TimeOffStatusResponse?>(null) }
    var monthTimeOffData by remember { mutableStateOf<TimeOffYearResponse?>(null) }
    val validatedDates = remember { mutableStateOf<Map<LocalDate, String>>(emptyMap()) }

    var permissionRemainingHours by remember { mutableStateOf("0") }

    val isLoading = remember { mutableStateOf(false) }

    var shouldRefresh by remember { mutableStateOf(false) }

    var holidayText by remember { mutableStateOf("") }

    var weekendDayNames by remember { mutableStateOf<Set<String>>(emptySet()) }

    var officialHolidayText by remember { mutableStateOf("") }

    var publicHolidayDates by remember { mutableStateOf<Set<LocalDate>>(emptySet()) }


    fun triggerRefresh() {
        shouldRefresh = !shouldRefresh
    }


    LaunchedEffect(shouldRefresh) {
        isLoading.value = true

        val result = fetchAndPrintHolidays(token)

        holidayText = result.weekendText
        weekendDayNames = result.weekendDays.toSet()
        officialHolidayText = result.holidaysText
        publicHolidayDates = result.publicHolidayDates

        println("🟦 weekendDayNames = $weekendDayNames")
        println("🟦 publicHolidayDates = $publicHolidayDates")


        try {

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
                    it.leave_type.equals("permissions", ignoreCase = true)
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


            val time_off_status = SendApiForTimeOff(
                timeOffRequest = TimeOffRequest(
                    employee_token = token,
                    action = "time_off_status"
                )
            )

            if (time_off_status is TimeOffStatusResponse) {
                TimeOffStatusResponse = TimeOffStatusResponse(
                    status = time_off_status.status,
                    records = time_off_status.records
                )

                Log.i("TimeOffScreen", "✅ time_off_status loaded")

                val validated = time_off_status.records.daily_records
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
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            topBar = {
                MyAppBar(label = stringResource(R.string.time_off_screen), navController = navController)
            },
            bottomBar = {
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
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
//                                it.leave_type.equals("permissions", ignoreCase = true)
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
                    annualLeaveRemainingDays = annualLeaveRemaining,
                    permissionRemainingHours = permissionRemainingHours,
                )

                Spacer(modifier = Modifier.height(0.dp))
                MyCalendarPicker(
                    selectedDates = selectedDates,
                    onDateSelectedChange = { selectedDates = it },
                    validatedDates = validatedDates.value,
                    token = token,
                    onRefreshRequest = { triggerRefresh() },
                    dailyRecords = yearTimeOffData?.records?.daily_records ?: emptyList(),
                    weekendDayNames = weekendDayNames ,
                    publicHolidayDates = publicHolidayDates

                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(10.dp))
                ColorsOfTimeOff()
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}