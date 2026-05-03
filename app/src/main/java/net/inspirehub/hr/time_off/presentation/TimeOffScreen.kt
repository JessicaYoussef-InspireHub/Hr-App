package net.inspirehub.hr.time_off.presentation

import RemainingLeavesResponse
import sendApiForTimeOff
import TimeOffRequest
import TimeOffStatusResponse
import TimeOffYearResponse
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.check_in_out.data.NetworkUtils
import net.inspirehub.hr.time_off.components.LeaveTypesLazyRow
import net.inspirehub.hr.time_off.components.MyCalendarPicker
import net.inspirehub.hr.time_off.components.MyActualTimeOff
import net.inspirehub.hr.time_off.components.RetryButton
import net.inspirehub.hr.time_off.components.Shapes
import net.inspirehub.hr.time_off.data.LeaveType
import net.inspirehub.hr.time_off.data.fetchAndPrintHolidays
import net.inspirehub.hr.time_off.data.fetchEmployeeLeaveTypes
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


    val leaveTypes = leaveTypesState.value
    val leaveTypeColors = remember { mutableStateMapOf<String, Color>() }
    val isOffline = remember { mutableStateOf(false) }

    val colors = appColors()

    fun triggerRefresh() {
        shouldRefresh = !shouldRefresh
    }


    LaunchedEffect(shouldRefresh) {
        isLoading.value = true
        isOffline.value = false
        try {
            if (!NetworkUtils.isNetworkAvailable(context) || !NetworkUtils.hasRealInternet()) {
                isOffline.value = true
                return@LaunchedEffect
            }

            val leaveTypesResponse = fetchEmployeeLeaveTypes(context, token)
            leaveTypesResponse?.let {
                leaveTypesState.value = it.result.leave_types

                it.result.leave_types.forEach { leaveType ->
                    val colorHex = if (leaveType.color.isNullOrBlank()) "#00000000" else leaveType.color
                    val safeColor = try {
                        android.graphics.Color.parseColor(colorHex)
                    } catch (e: IllegalArgumentException) {
                        android.graphics.Color.TRANSPARENT // fallback آمن
                    }
                    leaveTypeColors[leaveType.name] = Color(safeColor)
                }

            }

            val result = fetchAndPrintHolidays(token , context)

            holidayText = result.weekendText
            weekendDayNames = result.weekendDays.toSet()
            officialHolidayText = result.holidaysText
            publicHolidayDates = result.publicHolidayDates

            println("weekendDayNames = $weekendDayNames")
            println("publicHolidayDates = $publicHolidayDates")

            val monthResult = sendApiForTimeOff(
                context = context,
                retry = true,
                timeOffRequest = TimeOffRequest(
                    employee_token = token,
                    action = "this_month_time_off"
                ),

            )

            if (monthResult is TimeOffYearResponse) {
                monthTimeOffData = monthResult
                Log.i("TimeOffScreen", "this_month_time_off = $monthResult")
            }

            val remainingResult = sendApiForTimeOff(
                context = context,
                retry = true,
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


            val timeOffStatus = sendApiForTimeOff(
                context = context,
                retry = true,
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


            val yearResult = sendApiForTimeOff(
                context = context,
                retry = true,
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

        Scaffold(
            containerColor = colors.onSecondaryColor,
            topBar = {
                MyAppBar(
                    label = stringResource(R.string.time_off_screen),
                    onBackClick = {
                        navController.navigate("CheckInOutScreen") {
                            popUpTo("CheckInOutScreen") { inclusive = true }
                        }
                    }
                )
            },
            bottomBar = {
                BottomBar(navController = navController)
            }
        ){
            paddingValues ->
            if (isOffline.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.onSecondaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.you_are_offline),
                            color = colors.tertiaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RetryButton {
                            triggerRefresh()
                        }
                    }
                }
            } else if (isLoading.value) {
                FullLoading()
            } else {
            Column(
                modifier = Modifier
                    .background(colors.onSecondaryColor)
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
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
                        .background(colors.inverseOnSurface)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LeaveTypesLazyRow(leaveTypes)
                    Shapes()
                }
            }
        }
    }
}