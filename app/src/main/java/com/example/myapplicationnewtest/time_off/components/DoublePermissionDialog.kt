package com.example.myapplicationnewtest.time_off.components

import HourlyTimeOffRecord
import TimeOffRecord
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.time_off.data.TimeOffRequestForRequestEmployee
import com.example.myapplicationnewtest.time_off.data.sendApiForRequestTimeOff
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoublePermissionDialog(
    records: List<HourlyTimeOffRecord>,
    onDismiss: () -> Unit,
    token: String,
    onRefreshRequest: () -> Unit,
    dailyRecords: List<TimeOffRecord>,
    hourlyRecords: List<HourlyTimeOffRecord>,
    weekendDayNames: Set<String>,
    publicHolidayDates: Set<LocalDate>,
    clickedDate: LocalDate?,
    selectedDates: Set<LocalDate>? = null,
    validatedDates: Map<LocalDate, String>? = null,
    onDateSelectedChange: ((Set<LocalDate>) -> Unit)? = null,
    startDate: LocalDate? = null,
    ){

    val formattedDate = clickedDate?.format(
        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
    ) ?: ""

    var recordToDelete by remember { mutableStateOf<HourlyTimeOffRecord?>(null) }


    fun translateLeaveType(typeKey: String, language: String): String {
        return when (language) {
            "ar" -> when (typeKey.lowercase(Locale.ROOT)) {
                "annual leave" -> "إجازة سنوية"
                "sick time off" -> "إجازة مرضية"
                "unpaid" -> "بدون راتب"
                "permissions" -> "أذونات"
                else -> typeKey
            }

            else -> when (typeKey.lowercase(Locale.ROOT)) {
                "annual leave" -> "Annual Leave"
                "sick time off" -> "Sick Time Off"
                "unpaid" -> "Unpaid"
                "permissions" -> "Permissions"
                else -> typeKey
            }
        }
    }

    val currentLanguage = Locale.getDefault().language

    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return input.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }
            .joinToString("")
    }

    fun getLocalizedHourText(count: Double?, language: String): String {
        if (count == null) return ""

        return if (language == "ar") {
            when {
                count == 0.5 -> "نصف ساعة"
                count == 1.0 -> "ساعة"
                count == 1.5 -> "ساعة ونصف"
                count == 2.0 -> "ساعتين"
                count == 2.5 -> "ساعتين ونصف"
                count in 3.0..10.0 && count % 1 == 0.0 -> "${count.toInt()} ساعات"
                count > 10 && count % 1 == 0.0 -> "${count.toInt()} ساعة"
                count % 1 == 0.5 -> "${count.toInt()} ساعة ونصف"
                else -> "$count ساعة"
            }
        } else {
            when {
                count == 0.5 -> "Half an hour"
                count == 1.0 -> "1 hour"
                count == 1.5 -> "1 hour and a half"
                count == 2.0 -> "2 hours"
                count == 2.5 -> "2 hours and a half"
                count % 1 == 0.0 -> "${count.toInt()} hours"
                count % 1 == 0.5 -> "${count.toInt()} and a half hours"
                else -> "$count hours"
            }
        }
    }

    fun formatDecimalHourToTime(decimalHour: Double?, currentLanguage: String): String {
        if (decimalHour == null) return ""

        val hours = decimalHour.toInt()
        val minutes = ((decimalHour - hours) * 60).toInt()

        val time = LocalTime.of(hours, minutes)

        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
        val formatted = time.format(formatter)

        return if (currentLanguage == "ar") {
            formatted.replace("AM", "ص").replace("PM", "م")
        } else {
            formatted
        }
    }

    val allRefused = records.all { it.state == "refuse" }
    val hasConfirmOrDraft = records.any { it.state == "confirm" || it.state == "draft" }
    val hasValidate = records.any { it .state == "validate" }
    val hasOtherThanConfirm = records.any { it.state != "confirm" }
    val approveWithOtherThanConfirm = hasValidate && hasOtherThanConfirm && !hasConfirmOrDraft
    var showNewVacationDialog by remember { mutableStateOf(false) }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.onPrimary,
        onDismissRequest = { onDismiss() },
        confirmButton = {
            when {
                allRefused -> {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = stringResource(R.string.ok))
                    }
                }


                approveWithOtherThanConfirm -> {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = stringResource(R.string.ok))
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }
                Text(
                    text = formattedDate,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(15.dp))
                Column {
                    records.forEach { record ->

                        val stateLabel = when (record.state) {
                            "draft" -> stringResource(R.string.pending_approval)
                            "validate" -> stringResource(R.string.final_approved)
                            "confirm" -> stringResource(R.string.pending_approval)
                            "refuse" -> stringResource(R.string.rejected)
                            else -> record.state
                        }

                        val colorCircle = when (record.state) {
                            "validate" -> MaterialTheme.colorScheme.secondary
                            "draft" -> MaterialTheme.colorScheme.tertiary
                            "confirm" -> MaterialTheme.colorScheme.tertiary
                            "refuse" -> MaterialTheme.colorScheme.error
                            else -> Color.Transparent
                        }

                        val translatedLeaveType =
                            translateLeaveType(record.leave_type, currentLanguage)
                        val durationInt = record.duration_hours
                        val hourWord = getLocalizedHourText(
                            count = durationInt,
                            language = currentLanguage
                        )

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(15.dp)
                                        .background(color = colorCircle, shape = CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    stateLabel,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                            Text(
                                "$translatedLeaveType: $hourWord",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(start = 20.dp),
                            )
                            Text(
                                text = "${stringResource(R.string.from)} ${
                                    if (currentLanguage == "ar") {
                                        convertToArabicDigits(
                                            formatDecimalHourToTime(
                                                record.request_hour_from?.toDoubleOrNull(),
                                                currentLanguage
                                            )
                                        )
                                    } else {
                                        formatDecimalHourToTime(
                                            record.request_hour_from?.toDoubleOrNull(),
                                            currentLanguage
                                        )
                                    }
                                } ${stringResource(R.string.to)} ${
                                    if (currentLanguage == "ar") {
                                        convertToArabicDigits(
                                            formatDecimalHourToTime(
                                                record.request_hour_to?.toDoubleOrNull(),
                                                currentLanguage
                                            )
                                        )
                                    } else {
                                        formatDecimalHourToTime(
                                            record.request_hour_to?.toDoubleOrNull(),
                                            currentLanguage
                                        )
                                    }
                                }",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(start = 20.dp),
                            )
                            if (record.state == "draft" || record.state == "confirm") {
                                Row (
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                ){
                                    Button(
                                        onClick = {
                                            recordToDelete = record
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.padding(start = 20.dp)
                                    ) {
                                        Text(text = stringResource(R.string.remove_pending))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (recordToDelete != null) {
                            DeleteConfirmationDialog(
                                onDismiss = { recordToDelete = null },
                                onConfirmDelete = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val request = TimeOffRequestForRequestEmployee(
                                            employee_token = token,
                                            action = "unlink_draft_annual_leaves",
                                            leave_type_id = 6,
                                            request_date_from = recordToDelete!!.leave_day,
                                            request_date_to = recordToDelete!!.leave_day,
                                            leave_id = recordToDelete!!.leave_id
                                        )

                                        Log.d("REQUEST_BODY", request.toString())
                                        val response = sendApiForRequestTimeOff(request)
                                        Log.d("API_RESPONSE", response.toString())

                                        withContext(Dispatchers.Main) {
                                            recordToDelete = null
                                            onDismiss()
                                            onRefreshRequest()
                                        }
                                    }
                                }
                            )
                        }


                        if (showNewVacationDialog) {
                            DateInfoDialog(
                                date = (clickedDate ?: startDate) ?: LocalDate.now(),
                                selectedDates = selectedDates ?: emptySet(),
                                onDateSelectedChange = onDateSelectedChange ?: {},
                                onConfirm = {
                                    showNewVacationDialog = false
                                    onRefreshRequest()
                                },
                                onDiscard = {
                                    showNewVacationDialog = false
                                },
                                validatedDates = validatedDates ?: emptyMap(),
                                token = token,
                                onRefreshRequest = onRefreshRequest,
                                weekendDayNames = weekendDayNames,
                                publicHolidayDates = publicHolidayDates,
                                dailyRecords = dailyRecords,
                                hourlyRecords = hourlyRecords
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    showNewVacationDialog = true
                                },
                            tint = if (allRefused) MaterialTheme.colorScheme.error
                            else if (hasConfirmOrDraft) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.secondary,
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.create_another_one),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (allRefused) MaterialTheme.colorScheme.error
                                   else if (hasConfirmOrDraft) MaterialTheme.colorScheme.tertiary
                                   else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.clickable {
                                showNewVacationDialog = true

                            }
                        )
                    }
                }
            }
        },
    )
}
