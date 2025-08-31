package com.example.myapplicationnewtest.time_off.components

import TimeOffRecord
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.time_off.data.LeaveType
import com.example.myapplicationnewtest.time_off.data.sendApiForRequestTimeOff
import com.example.myapplicationnewtest.time_off.data.TimeOffRequestForRequestEmployee
import com.example.myapplicationnewtest.time_off.data.fetchEmployeeLeaveTypes
import com.example.myapplicationnewtest.time_off.data.getLeaveDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateInfoDialog(
    date: LocalDate,
    selectedDates: Set<LocalDate>,
    onDateSelectedChange: (Set<LocalDate>) -> Unit,
    onConfirm: () -> Unit,
    onDiscard: () -> Unit,
    validatedDates: Map<LocalDate, String>,
    token: String,
    onRefreshRequest: () -> Unit,
    weekendDayNames: Set<String>,
    publicHolidayDates: Set<LocalDate>,
    dailyRecords: List<TimeOffRecord>
) {

    var isHalfDay by remember { mutableStateOf(false) }
    val pleaseChooseTypeText = stringResource(R.string.please_choose_the_time_off_type)
    val errorMessageTemplate = stringResource(R.string.error_message_overlap)

    var showStartCalendar by remember { mutableStateOf(false) }
    var showEndCalendar by remember { mutableStateOf(false) }

    var selectedStartDate by remember { mutableStateOf(date) }
    var selectedEndDate by remember { mutableStateOf(date) }

    var isLoading by remember { mutableStateOf(false) }

    var leaveTypes by remember { mutableStateOf<List<LeaveType>>(emptyList()) }
    var selectedLeaveType by remember { mutableStateOf<LeaveType?>(null) }

    var showErrorDialog by remember { mutableStateOf(false) }

    val morningText = stringResource(R.string.morning)
    var halfDayOption by remember { mutableStateOf(morningText) }

    var leaveTypeError by remember { mutableStateOf("") }

    var leaveDays by remember { mutableDoubleStateOf(1.0) }



    var errorMessage by remember { mutableStateOf("") }
    LaunchedEffect(token) {
        val result = fetchEmployeeLeaveTypes(token)
        result?.result?.leave_types?.let {
            leaveTypes = it
        }
    }


    fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): Set<LocalDate> {
        val dates = mutableSetOf<LocalDate>()
        var date = startDate
        while (!date.isAfter(endDate)) {
            dates.add(date)
            date = date.plusDays(1)
        }
        return dates
    }

    Dialog(
        onDismissRequest = { }
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                DialogTitle {
                    onDiscard()
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp, horizontal = 20.dp),
                ) {
                    FirstText(stringResource(R.string.time_off_type))
                    DropDown(
                        leaveTypes = leaveTypes,
                        selectedLeaveType = selectedLeaveType,
                        onLeaveTypeSelected = {
                            selectedLeaveType = it
                            leaveTypeError = "" },
                        modifier = Modifier
                            .padding(start = 50.dp)
                    )
                    if (leaveTypeError.isNotEmpty()) {
                        Text(
                            text = leaveTypeError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 50.dp, top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    FirstText(stringResource(R.string.dates))
                    Dates(
                        startDate = selectedStartDate,
                        lastDate = selectedEndDate,
                        onStartDateClick = { showStartCalendar = true },
                        onEndDateClick = { showEndCalendar = true },
                        modifier = Modifier.padding(start = 50.dp),
                        isHalfDay = isHalfDay,
                        halfDayOption = halfDayOption,
                        onHalfDayOptionChange = { halfDayOption = it },
                    )

                    if (showStartCalendar) {
                        Dialog(
                            onDismissRequest = { showStartCalendar = false }) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.onPrimary)
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.onPrimary)
                                ) {
                                    var tempStartDate by remember { mutableStateOf(selectedStartDate) }

                                    MyCalendarPicker(
                                        selectedDates = selectedDates,
                                        onDateSelectedChange = {
                                            onDateSelectedChange(it)
                                        },
                                        isDialogMode = true,
                                        validatedDates = validatedDates,
                                        initialMonth = YearMonth.from(date),
                                        onDateSelected = {
                                            tempStartDate = it
                                        },
                                        token = token,
                                        onRefreshRequest = onRefreshRequest,
                                        startDate = tempStartDate,
                                        endDate = selectedEndDate,
                                        weekendDayNames = weekendDayNames,
                                        publicHolidayDates = publicHolidayDates,
                                        dailyRecords = dailyRecords


                                    )
                                    DialogActionsRow(
                                        onConfirm = {
                                            Log.d("CALENDAR", "save")

                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                                                    val requestDateFrom = selectedStartDate.format(formatter)
                                                    val requestDateTo = selectedEndDate.format(formatter)

                                                    Log.d("DEBUG_DATES", "start=$requestDateFrom, end=$requestDateTo, token=$token, type=6")

                                                    val response = getLeaveDuration(
                                                        employeeToken = token,
                                                        requestDateFrom = requestDateFrom,
                                                        requestDateTo = requestDateTo,
                                                        leaveTypeId = 6
                                                    )
                                                    response.result.data.let {
                                                        leaveDays = if (it.days == 0.0) 1.0 else it.days
                                                    }

                                                    Log.d("LEAVE_API", "Parsed Response: $response")
                                                } catch (e: Exception) {
                                                    Log.e("LEAVE_API", "Error in call: ${e.message}", e)
                                                }
                                            }


                                            if (tempStartDate.isBefore(selectedEndDate)) {
                                                selectedStartDate = tempStartDate
                                            } else {
                                                selectedStartDate = selectedEndDate
                                                selectedEndDate = tempStartDate
                                            }

                                            val updatedDates =
                                                getDatesBetween(selectedStartDate, selectedEndDate)
                                            onDateSelectedChange(updatedDates)

                                            showStartCalendar = false
                                        },
                                        onDiscard = {
                                            showStartCalendar = false
                                        },
                                        modifier = Modifier.padding(
                                            horizontal = 15.dp,
                                            vertical = 10.dp
                                        )
                                    )

                                }
                            }
                        }
                    }

                    if (showEndCalendar) {
                        Dialog(onDismissRequest = { showEndCalendar = false }) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.onPrimary)
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.onPrimary)
                                ) {
                                    var tempEndDate by remember { mutableStateOf(selectedEndDate) }

                                    MyCalendarPicker(
                                        selectedDates = selectedDates,
                                        onDateSelectedChange = {
                                            onDateSelectedChange(it)
                                        },
                                        isDialogMode = true,
                                        initialMonth = YearMonth.from(date),
                                        validatedDates = validatedDates,
                                        onDateSelected = {
                                            tempEndDate = it
                                        },
                                        token = token,
                                        onRefreshRequest = onRefreshRequest,
                                        startDate = selectedStartDate,
                                        endDate = tempEndDate,
                                        weekendDayNames = weekendDayNames,
                                        publicHolidayDates = publicHolidayDates,
                                        dailyRecords = dailyRecords

                                    )

                                    DialogActionsRow(
                                        onConfirm = {
                                            Log.d("CALENDAR", "save")

                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                                                    val requestDateFrom = selectedStartDate.format(formatter)
                                                    val requestDateTo = selectedEndDate.format(formatter)

                                                    Log.d("DEBUG_DATES", "start=$requestDateFrom, end=$requestDateTo, token=$token, type=6")

                                                    val response = getLeaveDuration(
                                                        employeeToken = token,
                                                        requestDateFrom = requestDateFrom,
                                                        requestDateTo = requestDateTo,
                                                        leaveTypeId = 6
                                                    )
                                                    response.result.data.let {
                                                        leaveDays = if (it.days == 0.0) 1.0 else it.days
                                                    }

                                                    Log.d("LEAVE_API", "Parsed Response: $response")
                                                } catch (e: Exception) {
                                                    Log.e("LEAVE_API", "Error in call: ${e.message}", e)
                                                }
                                            }


                                            if (tempEndDate.isAfter(selectedStartDate)) {
                                                selectedEndDate = tempEndDate
                                            } else {
                                                selectedEndDate = selectedStartDate
                                                selectedStartDate = tempEndDate
                                            }
                                            showEndCalendar = false
                                        },
                                        onDiscard = {
                                            showEndCalendar = false
                                        },
                                        modifier = Modifier.padding(
                                            horizontal = 15.dp,
                                            vertical = 10.dp
                                        )
                                    )

                                }
                            }
                        }
                    }


                    FirstText(" ")
                    HalfDayCheckbox(
                        isChecked = isHalfDay,
                        onCheckedChange = { isHalfDay = it },
                        modifier = Modifier
                            .padding(start = 30.dp)
                    )
                    if (!isHalfDay) {
                        Spacer(modifier = Modifier.height(15.dp))
                        FirstText(stringResource(R.string.duration))
                        DurationDays(
                            days = leaveDays.toInt(),
                            modifier = Modifier
                                .padding(start = 50.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    FirstText(stringResource(R.string.duration))
                    DescriptionInput(
                        modifier = Modifier
                            .padding(start = 50.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    DialogActionsRow(
                        onConfirm = {



                            if (selectedLeaveType == null) {
                                leaveTypeError = pleaseChooseTypeText
                                return@DialogActionsRow
                            }

                            isLoading = true
                            CoroutineScope(Dispatchers.IO).launch {
                                val startDateStr = selectedStartDate.toString()
                                val endDateStr = selectedEndDate.toString()
                                val request = TimeOffRequestForRequestEmployee(
                                    employee_token = token,
                                    action = "request_annual_leave",
                                    date_from = startDateStr,
                                    date_to = endDateStr,
                                    leave_type = 6,
                                )

                                Log.d("REQUEST_BODY", request.toString())

                                val response = sendApiForRequestTimeOff(request)

                                Log.d("API_RESPONSE", response.toString())

                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    if (response?.result?.status == "success") {
                                        onConfirm()
                                    } else {
                                        fun String.replaceDigitsWithArabic(): String {
                                            val arabicDigits = listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
                                            return this.map { char ->
                                                if (char.isDigit()) arabicDigits[char.digitToInt()] else char
                                            }.joinToString("")
                                        }

                                        val locale = Locale.getDefault()
                                        val formatter = DateTimeFormatter.ofPattern("d-M-yyyy", locale)

                                        var startDateStrFormatted = selectedStartDate.format(formatter)
                                        var endDateStrFormatted = selectedEndDate.format(formatter)

                                        if (locale.language == "ar") {
                                            startDateStrFormatted = startDateStrFormatted.replaceDigitsWithArabic()
                                            endDateStrFormatted = endDateStrFormatted.replaceDigitsWithArabic()
                                        }

                                        errorMessage = String.format(
                                            errorMessageTemplate,
                                            startDateStrFormatted,
                                            endDateStrFormatted
                                        )

                                        showErrorDialog = true
                                    }
                                }
                            }
                        },
                        onDiscard = {
                            if (!isLoading) onDiscard()
                        },
                        isLoading = isLoading
                    )
                    if (showErrorDialog) {
                        ErrorDialog(
                            message = errorMessage,
                            onDismiss = {
                                showErrorDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}
