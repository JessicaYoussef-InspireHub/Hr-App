package net.inspirehub.hr.time_off.components

import HourlyTimeOffRecord
import TimeOffRecord
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.time_off.data.TimeOffRequestForRequestEmployee
import net.inspirehub.hr.time_off.data.sendApiForRequestTimeOff
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@SuppressLint("NewApi")
@Composable
fun DoubleStateDialog(
    selectedDate: LocalDate,
    leaveRecords: List<TimeOffRecord>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    token: String,
    onRefreshRequest: () -> Unit,
    clickedDate: LocalDate? = null,
    startDate: LocalDate? = null,
    selectedDates: Set<LocalDate>? = null,
    onDateSelectedChange: ((Set<LocalDate>) -> Unit)? = null,
    validatedDates: Map<LocalDate, String>? = null,
    weekendDayNames: Set<String> = emptySet(),
    publicHolidayDates: Set<LocalDate> = emptySet(),
    dailyRecords: List<TimeOffRecord> = emptyList(),
    hourlyRecords: List<HourlyTimeOffRecord> = emptyList(),
    leaveTypeColors: Map<String, androidx.compose.ui.graphics.Color>
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val currentLanguage = Locale.getDefault().language


    val locale = if (currentLanguage == "ar") Locale("ar") else Locale.ENGLISH

    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
        return input.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
    }

    val formattedDateRaw = selectedDate.format(
        DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
    )

    val formattedDate = if (currentLanguage == "ar") {
        convertToArabicDigits(formattedDateRaw)
    } else {
        formattedDateRaw
    }

    fun getLocalizedDayText(context: android.content.Context, count: Int, language: String): String {
        return if (language == "ar") {
            when (count) {
                1 -> "يوم"
                2 -> "يومين"
                in 3..10 -> "أيام"
                else -> "يومًا"
            }
        } else {
            if (count == 1) context.getString(R.string.day)
            else context.getString(R.string.days)
        }
    }

    var showNewVacationDialog by remember { mutableStateOf(false) }

    val allRefused = leaveRecords.isNotEmpty() && leaveRecords.all { it.state == "refuse" }

    val hasDraftOrConfirm = leaveRecords.any { it.state == "draft" || it.state == "confirm" }
    val colors = appColors()

    AlertDialog(
        containerColor = colors.surfaceVariant,
        onDismissRequest = onDismiss,
        confirmButton = {
//            if (!allRefused) {
                Button(
                onClick = {
                    if (hasDraftOrConfirm) {
                        showDeleteConfirmation = true
                    } else {
                        onConfirm()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.tertiaryColor,
//                        if (hasDraftOrConfirm) MaterialTheme.colorScheme.tertiary
//                    else if (allRefused) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary ,
                    contentColor = colors.onSecondaryColor
                ),
                shape = RoundedCornerShape(10.dp)

            ) {
                Text(
                    text = if (hasDraftOrConfirm)
                        stringResource(R.string.remove_pending) else stringResource(R.string.ok),
                )
            }
//        }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = colors.tertiaryColor,
                        modifier = Modifier
                            .clickable { onDismiss() }
                    )
                }
                Text(
                    text = formattedDate,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.tertiaryColor,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(15.dp))
                Column {
                    leaveRecords.forEach { record ->


                        val translatedLeaveType = record.leave_type
                        val durationInt = record.duration_days.toInt()
                        val daysText = if (currentLanguage == "ar") convertToArabicDigits(durationInt.toString()) else durationInt.toString()
                        val dayWord = getLocalizedDayText(context = LocalContext.current, count = durationInt, language = currentLanguage)


                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (record.state.lowercase(Locale.ROOT)) {
                                    "validate" -> {
                                        Box(
                                            modifier = Modifier
                                                .size(15.dp)
                                                .background(
                                                    color = colors.tertiaryColor,
                                                    shape = CircleShape
                                                )
                                        )
                                    }

                                    "refuse" -> {
                                        Box(
                                            modifier = Modifier
                                                .size(15.dp)
                                                .border(1.dp, colors.tertiaryColor, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .width(15.dp)
                                                    .height(2.dp)
                                                    .background(
                                                        color = colors.tertiaryColor,
                                                        shape = RoundedCornerShape(2.dp)
                                                    )
                                            )
                                        }
                                    }

                                    "confirm", "draft" -> {
                                        Canvas(
                                            modifier = Modifier
                                                .size(15.dp)
                                                .background(colors.transparent, CircleShape)
                                                .border(1.dp, colors.tertiaryColor, CircleShape)
                                        ) {
                                            val spacing = 6.dp.toPx()
                                            clipPath(Path().apply {
                                                addOval(Rect(0f, 0f, size.width, size.height))
                                            }) {
                                                for (i in -size.height.toInt()..size.width.toInt() step spacing.toInt()) {
                                                    drawLine(
                                                        color = colors.tertiaryColor,
                                                        start = Offset(i.toFloat(), 0f),
                                                        end = Offset(i + size.height, size.height),
                                                        strokeWidth = 4f,
                                                        cap = StrokeCap.Round
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                   record.state,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = colors.onBackgroundColor,
                                )
                            }
                            Text(
                                "$translatedLeaveType: $daysText $dayWord",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Normal,
                                color = colors.onBackgroundColor,
                                modifier = Modifier.padding(start = 20.dp),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (showDeleteConfirmation) {
                            val draftRecord = leaveRecords.find { it.state == "draft" || it.state == "confirm" }
                            if (draftRecord != null) {
                                DeleteConfirmationDialog(
                                    onDismiss = { showDeleteConfirmation = false },
                                    onConfirmDelete = {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val request = TimeOffRequestForRequestEmployee(
                                                employee_token = token,
                                                action = "unlink_draft_annual_leaves",
                                                leave_type_id = draftRecord.leave_id,
                                                request_date_from = draftRecord.start_date,
                                                request_date_to = draftRecord.end_date,
                                                leave_id = draftRecord.leave_id
                                            )

                                            Log.d("REQUEST_BODY", request.toString())

                                            val response = sendApiForRequestTimeOff(context , request)
                                            Log.d("API_RESPONSE", response.toString())

                                            withContext(Dispatchers.Main) {
                                                showDeleteConfirmation = false
                                                onDismiss()
                                                onRefreshRequest()
                                            }
                                        }
                                    }
                                )
                            }
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
                                hourlyRecords = hourlyRecords,
                                leaveTypeColors = leaveTypeColors,
                            )
                        }

                    }
                    if (allRefused) {
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
                                tint = colors.tertiaryColor
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = stringResource(R.string.create_another_one),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = colors.tertiaryColor,
                                modifier = Modifier.clickable {
                                    showNewVacationDialog = true

                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
