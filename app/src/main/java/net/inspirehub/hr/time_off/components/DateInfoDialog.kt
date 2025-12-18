package net.inspirehub.hr.time_off.components

import HourlyTimeOffRecord
import TimeOffRecord
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.time_off.data.LeaveType
import net.inspirehub.hr.time_off.data.sendApiForRequestTimeOff
import net.inspirehub.hr.time_off.data.TimeOffRequestForRequestEmployee
import net.inspirehub.hr.time_off.data.fetchEmployeeLeaveTypes
import net.inspirehub.hr.time_off.data.getLeaveDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.emptyList


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
    dailyRecords: List<TimeOffRecord>,
    hourlyRecords: List<HourlyTimeOffRecord>,
    leaveTypeColors: Map<String, androidx.compose.ui.graphics.Color>
) {
    val colors = appColors()
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
    var permissionErrorMessage by remember { mutableStateOf("") }

    var leaveDays by remember { mutableDoubleStateOf(1.0) }
    var leaveHours by remember { mutableDoubleStateOf(0.0) }

    var permissionChecked by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage()

    var selectedFromHour by remember { mutableStateOf<String?>(null) }
    var selectedToHour by remember { mutableStateOf<String?>(null) }

    var errorMessage by remember { mutableStateOf("") }

    var showPermissionErrorDialog by remember { mutableStateOf(false) }

    fun convertToDoubleHour(time: String): Double? {
        try {
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
            val localTime = LocalTime.parse(time, formatter)
            return localTime.hour + localTime.minute / 60.0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun normalizeTimeForParsing(time: String, language: String): String {
        var normalized = time

        val arabicToEnglish = mapOf(
            '٠' to '0', '١' to '1', '٢' to '2', '٣' to '3', '٤' to '4',
            '٥' to '5', '٦' to '6', '٧' to '7', '٨' to '8', '٩' to '9'
        )
        normalized = normalized.map { arabicToEnglish[it] ?: it }.joinToString("")

        if (language == "ar") {
            normalized = normalized.replace("ص", "AM").replace("م", "PM")
        }

        return normalized
    }

    fun calculatePermissionDuration() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                val requestDateFrom = selectedStartDate.format(formatter)
                val requestDateTo = selectedEndDate.format(formatter)

                val fromHourDouble = selectedFromHour?.let {
                    val normalized = normalizeTimeForParsing(it, currentLanguage)
                    convertToDoubleHour(normalized)
                }
                var toHourDouble = selectedToHour?.let {
                    val normalized = normalizeTimeForParsing(it, currentLanguage)
                    convertToDoubleHour(normalized)
                }

                if (fromHourDouble != null && toHourDouble != null) {
                    if (toHourDouble < fromHourDouble) {
                        toHourDouble += 24
                    }
                }

                if (fromHourDouble == null || toHourDouble == null) {
                    return@launch
                }

                val response = getLeaveDuration(
                    context = context,
                    employeeToken = token,
                    requestDateFrom = requestDateFrom,
                    requestDateTo = requestDateTo,
                    leaveTypeId = selectedLeaveType?.id ?: 0,
                    requestUnitHours = true,
                    requestHourFrom = fromHourDouble,
                    requestHourTo = toHourDouble
                )
                withContext(Dispatchers.Main) {
                    permissionErrorMessage = if (response.result.status == "error") {
                        if (currentLanguage == "ar") {
                            "راجع الوقت اللي كتبته.. الوقت لازم يكون في حدود ساعات العمل"
                        } else {
                            "Please check the time you entered. It must be within working hours."
                        }
                    } else {
                        ""
                    }
                }


                response.result.data?.let {
                    leaveHours = it.hours ?: 0.0
                }

                Log.d("LEAVE_API_Permission", "Parsed Response: $response")
            } catch (e: Exception) {
                Log.e("LEAVE_API", "Error in call: ${e.message}", e)
            }
        }
    }


    fun convertSelectedTimeToHour24(time: String, language: String): String {
        val arabicToEnglish = mapOf(
            '٠' to '0', '١' to '1', '٢' to '2', '٣' to '3', '٤' to '4',
            '٥' to '5', '٦' to '6', '٧' to '7', '٨' to '8', '٩' to '9'
        )
        var normalized = time.map { arabicToEnglish[it] ?: it }.joinToString("")

        val isPM = if (language == "ar") normalized.contains("م") else normalized.contains("PM")
        val isAM = if (language == "ar") normalized.contains("ص") else normalized.contains("AM")

        normalized = normalized.replace("AM", "").replace("PM", "")
            .replace("ص", "").replace("م", "")
            .trim()

        val parts = normalized.split(":")
        var hour = parts[0].toIntOrNull() ?: 0
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        if (isPM && hour < 12) hour += 12
        if (isAM && hour == 12) hour = 0

        return if (minute == 30) "${hour}.5" else "$hour"
    }





    LaunchedEffect(token) {
        val result = fetchEmployeeLeaveTypes(context , token)
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
            color = colors.surfaceVariant,
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
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        FirstText(stringResource(R.string.time_off_type))
                        Spacer(modifier = Modifier.width(10.dp))
                        DropDown(
                            leaveTypes = leaveTypes,
                            selectedLeaveType = selectedLeaveType,
                            onLeaveTypeSelected = {
                                selectedLeaveType = it
                                leaveTypeError = ""
                            },
                        )
                    }

                    if (leaveTypeError.isNotEmpty()) {
                        Text(
                            text = leaveTypeError,
                            color = colors.tertiaryColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 50.dp, top = 4.dp)
                        )
                    }



                    Spacer(modifier = Modifier.height(15.dp))
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        FirstText(stringResource(R.string.dates))
                        Spacer(modifier = Modifier.width(10.dp))
                        Dates(
                            startDate = selectedStartDate,
                            lastDate = selectedEndDate,
                            onStartDateClick = { showStartCalendar = true },
                            onEndDateClick = { showEndCalendar = true },
                            isHalfDay = isHalfDay,
                            halfDayOption = halfDayOption,
                            onHalfDayOptionChange = { halfDayOption = it },
                            hideEndDate = permissionChecked
                        )
                    }


                    if (showStartCalendar) {
                        Dialog(
                            onDismissRequest = { showStartCalendar = false }) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colors.surfaceVariant)
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(colors.surfaceVariant)
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
                                        dailyRecords = dailyRecords,
                                        hourlyRecords = hourlyRecords,
                                        leaveTypeColors = leaveTypeColors

                                    )
                                    DialogActionsRow(
                                        onConfirm = {
                                            Log.d("CALENDAR", "save")

                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val formatter =
                                                        DateTimeFormatter.ofPattern("MM-dd-yyyy")
                                                    val requestDateFrom =
                                                        selectedStartDate.format(formatter)
                                                    val requestDateTo =
                                                        selectedEndDate.format(formatter)

                                                    Log.d(
                                                        "DEBUG_DATES",
                                                        "start=$requestDateFrom, end=$requestDateTo, token=$token, type=6"
                                                    )

                                                    val response = getLeaveDuration(
                                                        context = context,
                                                        employeeToken = token,
                                                        requestDateFrom = requestDateFrom,
                                                        requestDateTo = requestDateTo,
                                                        leaveTypeId = selectedLeaveType?.id ?: 0,
                                                    )


                                                    response.result.data.let {
                                                        leaveDays = if ((it?.days
                                                                ?: 0.0) == 0.0
                                                        ) 1.0 else it?.days
                                                            ?: 1.0
                                                    }


                                                    Log.d("LEAVE_API", "Parsed Response: $response")
                                                } catch (e: Exception) {
                                                    Log.e(
                                                        "LEAVE_API",
                                                        "Error in call: ${e.message}",
                                                        e
                                                    )
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
                                    .background(colors.surfaceVariant)
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(colors.surfaceVariant)
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
                                        dailyRecords = dailyRecords,
                                        hourlyRecords = hourlyRecords,
                                        leaveTypeColors = leaveTypeColors


                                        )

                                    DialogActionsRow(
                                        onConfirm = {
                                            Log.d("CALENDAR", "save")

                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val formatter =
                                                        DateTimeFormatter.ofPattern("MM-dd-yyyy")
                                                    val requestDateFrom =
                                                        selectedStartDate.format(formatter)
                                                    val requestDateTo =
                                                        selectedEndDate.format(formatter)

                                                    Log.d(
                                                        "DEBUG_DATES",
                                                        "start=$requestDateFrom, end=$requestDateTo, token=$token, type=6"
                                                    )

                                                    val response = getLeaveDuration(
                                                        context = context,
                                                        employeeToken = token,
                                                        requestDateFrom = requestDateFrom,
                                                        requestDateTo = requestDateTo,
                                                        leaveTypeId = selectedLeaveType?.id ?: 0,
                                                    )

                                                    response.result.data.let {
                                                        leaveDays = if ((it?.days
                                                                ?: 0.0) == 0.0
                                                        ) 1.0 else it?.days
                                                            ?: 1.0
                                                    }


                                                    Log.d("LEAVE_API", "Parsed Response: $response")
                                                } catch (e: Exception) {
                                                    Log.e(
                                                        "LEAVE_API",
                                                        "Error in call: ${e.message}",
                                                        e
                                                    )
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
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HalfDayCheckbox(
                            isChecked = isHalfDay,
                            onCheckedChange = { checked ->
                                isHalfDay = checked
                                if (checked) {
                                    permissionChecked = false
                                }
                            },
                            modifier = Modifier
                                .padding(start = 30.dp)
                        )
                        if (selectedLeaveType?.name.equals("Permission", ignoreCase = true)) {

                            CustomHours(
                                isCheckedHours = permissionChecked,
                                onCheckedHoursChange = { checked ->
                                    permissionChecked = checked
                                    if (checked) {
                                        isHalfDay = false
                                    }
                                },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    if (permissionChecked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 50.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CustomHourDropDown(
                                stringResource(R.string.from),
                                selectedPermissionHour = selectedFromHour,
                                onPermissionHourSelected = { time ->
                                    selectedFromHour = time
//                                    selectedFromHour = convertSelectedTimeToHour24(time, currentLanguage)
                                    calculatePermissionDuration()
                                }
                            )
                            CustomHourDropDown(
                                label = stringResource(R.string.to),
                                selectedPermissionHour = selectedToHour,
                                onPermissionHourSelected = { time ->
//                                    selectedToHour = convertSelectedTimeToHour24(time, currentLanguage)
                                    selectedToHour = time
                                    calculatePermissionDuration()
                                },
                            )
                        }
                    }

                    if (permissionErrorMessage.isNotEmpty()) {
                        Text(
                            text = permissionErrorMessage,
                            color = colors.tertiaryColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 50.dp, top = 4.dp)
                        )
                    }

                    when {
                        permissionChecked -> {
                            Spacer(modifier = Modifier.height(15.dp))
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ){
                                FirstText(stringResource(R.string.duration))
                                Spacer(Modifier.width(10.dp))
                                DurationHours(hours = leaveHours)
                            }
                        }

                        isHalfDay && !selectedLeaveType?.name.equals(
                            "Permission",
                            ignoreCase = true
                        ) -> {
                        }

                        isHalfDay && selectedLeaveType?.name.equals(
                            "Permission",
                            ignoreCase = true
                        ) -> {
                            Spacer(modifier = Modifier.height(15.dp))
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ){
                                FirstText(stringResource(R.string.duration))
                                Spacer(Modifier.width(10.dp))
                                DurationHours(hours = 4.0)
                            }
                        }

                        !isHalfDay && selectedLeaveType?.name.equals(
                            "Permission",
                            ignoreCase = true
                        ) -> {
                            Spacer(modifier = Modifier.height(15.dp))
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                FirstText(stringResource(R.string.duration))
                                DurationDays(days = 1)
                                DurationHours(hours = 8.0)
                            }
                        }

                        else -> {
                            Spacer(modifier = Modifier.height(15.dp))
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ){
                                FirstText(stringResource(R.string.duration))
                                Spacer(Modifier.width(10.dp))
                                DurationDays(days = leaveDays.toInt())
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(15.dp))
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        FirstText(stringResource(R.string.description))
                        DescriptionInput()
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    DialogActionsRow(
                        onConfirm = {
                            if (permissionErrorMessage.isNotEmpty()) {
                                return@DialogActionsRow
                            }

                            if (selectedLeaveType?.name.equals(
                                    "Permission",
                                    ignoreCase = true
                                ) && permissionChecked
                            ) {
                                Log.d("SAVE_ACTION", "Permission with custom hour")

                                isLoading = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    val startDateStr = selectedStartDate.toString()

                                    val fromHourForApi = selectedFromHour?.let { convertSelectedTimeToHour24(it, currentLanguage) } ?: "0"
                                    val toHourForApi = selectedToHour?.let { convertSelectedTimeToHour24(it, currentLanguage) } ?: "0"

                                    val request = TimeOffRequestForRequestEmployee(
                                        employee_token = token,
                                        action = "request_annual_leave",
                                        leave_type_id = selectedLeaveType?.id ?: 0,
                                        request_date_from = startDateStr,
                                        request_date_to = startDateStr,
                                        request_hour_from = fromHourForApi,
                                        request_hour_to = toHourForApi,
                                        request_unit_hours = true
                                    )

                                    Log.d("REQUEST_BODY_Permission", request.toString())

                                    val response = sendApiForRequestTimeOff(context , request)


                                    Log.d("API_RESPONSE_Permission", response.toString())

                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                        if (response?.result?.status == "success") {
                                            onConfirm()
                                        } else {
                                            val apiMessage = response?.result?.message ?: ""
                                            if (apiMessage.contains("No allocation found for this leave type", ignoreCase = true)) {
                                                errorMessage = apiMessage
                                                showPermissionErrorDialog = true

                                            } else {
//                                                errorMessage = "Error requesting Permission"
                                                errorMessage = apiMessage
                                                showPermissionErrorDialog = true                                            }
                                        }
                                    }
                                }

                                return@DialogActionsRow
                            }


                            if (selectedLeaveType == null) {
                                leaveTypeError = pleaseChooseTypeText
                                return@DialogActionsRow
                            }

                            if (isHalfDay) {
                                Log.d("HALF_DAY", "نص يوم - $halfDayOption")

                                isLoading = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    val startDateStr = selectedStartDate.toString()

                                    val period = if (halfDayOption == morningText) "am" else "pm"

                                    val request = TimeOffRequestForRequestEmployee(
                                        employee_token = token,
                                        action = "request_annual_leave",
                                        leave_type_id = selectedLeaveType?.id ?: 0,
                                        request_date_from = startDateStr,
                                        request_date_to = startDateStr,
                                        request_date_from_period = period,
                                        request_unit_half = true,
                                        request_unit_hours = true
                                    )

                                    Log.d("REQUEST_BODY_HALF_DAY", request.toString())

                                    val response = sendApiForRequestTimeOff(context , request)

                                    Log.d("API_RESPONSE_HALF_DAY", response.toString())

                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                        if (response?.result?.status == "success") {
                                            onConfirm()
                                        } else {
                                            val apiMessage = response?.result?.message ?: ""

//                                            errorMessage = "Error requesting half day"
                                            errorMessage = apiMessage
                                            showErrorDialog = true
                                        }
                                    }
                                }

                                return@DialogActionsRow
                            }


                            isLoading = true
                            CoroutineScope(Dispatchers.IO).launch {
                                val startDateStr = selectedStartDate.toString()
                                val endDateStr = selectedEndDate.toString()
                                val request = TimeOffRequestForRequestEmployee(
                                    employee_token = token,
                                    action = "request_annual_leave",
                                    leave_type_id = selectedLeaveType?.id ?: 0,
                                    request_date_from = startDateStr,
                                    request_date_to = endDateStr
                                )

                                Log.d("REQUEST_BODY", request.toString())

                                val response = sendApiForRequestTimeOff(context , request)

                                Log.d("API_RESPONSE", response.toString())

                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    if (response?.result?.status == "success") {
                                        onConfirm()
                                    } else {
                                        fun String.replaceDigitsWithArabic(): String {
                                            val arabicDigits = listOf(
                                                '٠',
                                                '١',
                                                '٢',
                                                '٣',
                                                '٤',
                                                '٥',
                                                '٦',
                                                '٧',
                                                '٨',
                                                '٩'
                                            )
                                            return this.map { char ->
                                                if (char.isDigit()) arabicDigits[char.digitToInt()] else char
                                            }.joinToString("")
                                        }

                                        val locale = Locale.getDefault()
                                        val formatter =
                                            DateTimeFormatter.ofPattern("d-M-yyyy", locale)

                                        var startDateStrFormatted =
                                            selectedStartDate.format(formatter)
                                        var endDateStrFormatted = selectedEndDate.format(formatter)

                                        val apiMessage = response?.result?.message ?: ""

                                        if (locale.language == "ar") {
                                            startDateStrFormatted =
                                                startDateStrFormatted.replaceDigitsWithArabic()
                                            endDateStrFormatted =
                                                endDateStrFormatted.replaceDigitsWithArabic()
                                        }

                                        errorMessage = apiMessage
//                                        errorMessage = String.format(
//                                            errorMessageTemplate,
//                                            startDateStrFormatted,
//                                            endDateStrFormatted
//                                        )

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

                    if (showPermissionErrorDialog) {
                        ErrorPermissionDialog(
                            message = errorMessage,
                            onDismiss = { showPermissionErrorDialog = false }
                        )
                    }

                }
            }
        }
    }
}
