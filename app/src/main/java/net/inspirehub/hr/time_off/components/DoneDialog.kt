package net.inspirehub.hr.time_off.components

import HourlyTimeOffRecord
import TimeOffRecord
import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.time_off.data.TimeOffRequestForRequestEmployee
import net.inspirehub.hr.time_off.data.sendApiForRequestTimeOff
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@SuppressLint("NewApi")
@Composable
fun TimeOffDetailsDialog(
    record: TimeOffRecord,
    onDismiss: () -> Unit,
    token: String,
    onRefreshRequest: () -> Unit,
    validatedDates: Map<LocalDate, String>? = null,
    weekendDayNames: Set<String>,
    publicHolidayDates: Set<LocalDate>,
    selectedDates: Set<LocalDate>? = null,
    onDateSelectedChange: ((Set<LocalDate>) -> Unit)? = null,
    dailyRecords: List<TimeOffRecord>,
    hourlyRecords: List<HourlyTimeOffRecord>,
    clickedDate: LocalDate? = null,
    leaveTypeColors: Map<String, androidx.compose.ui.graphics.Color>
) {

    val startDate = LocalDate.parse(record.start_date)
    val endDate = LocalDate.parse(record.end_date)
    var showNewVacationDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage() // "ar" أو "en"
    val currentLocale = if (currentLanguage == "ar") Locale("ar") else Locale.ENGLISH

    val sameDay = startDate == endDate
    val sameMonth = startDate.month == endDate.month && startDate.year == endDate.year
    val colors = appColors()


    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
        return input.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
    }


    val dateText = if (sameDay) {
        val formatted = startDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", currentLocale))
        if (currentLanguage == "ar") convertToArabicDigits(formatted) else formatted
    } else if (sameMonth) {
        val formatted = "${startDate.dayOfMonth}–${endDate.dayOfMonth} ${
            startDate.month.getDisplayName(TextStyle.FULL, currentLocale)
        } ${startDate.year}"
        if (currentLanguage == "ar") convertToArabicDigits(formatted) else formatted
    } else {
        val formatted = "${startDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", currentLocale))} – ${
            endDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", currentLocale))
        }"
        if (currentLanguage == "ar") convertToArabicDigits(formatted) else formatted
    }


    val formattedDuration = if (currentLanguage == "ar") {
        convertToArabicDigits(record.duration_days.toString())
    } else {
        record.duration_days.toString()
    }



    fun translateState(state: String, language: String): String {
        return when (language) {
            "ar" -> when (state.lowercase(Locale.ROOT)) {
                "draft" -> "قيد الموافقة"
                "validate" -> "تمت الموافقة النهائية"
                "confirm" -> "قيد الموافقة"
                "refuse" -> "مرفوض"
                else -> state
            }
            else -> state
        }
    }



    fun translateLeaveType(typeKey: String, language: String): String {
        return when (language) {
            "ar" -> when (typeKey.lowercase(Locale.ROOT)) {
                "annual leave" -> "إجازة سنوية"
                "sick time off" -> "إجازة مرضية"
                "unpaid" -> "بدون راتب"
                "permissions" -> "أذونات"
                else -> typeKey
            }
            else -> typeKey
        }
    }

    val translatedLeaveType = translateLeaveType(record.leave_type, currentLanguage)

    fun getLocalizedDayText(context: Context, count: Int, language: String): String {
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

    val dayLabel = getLocalizedDayText(context, record.duration_days.toInt(), currentLanguage)



    AlertDialog(
        containerColor = colors.surfaceVariant,
        onDismissRequest = { onDismiss() },
        confirmButton = {
            if (record.state == "draft" || record.state == "confirm") {
                Button(
                    onClick = {
                        showDeleteConfirmation = true

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.tertiaryColor,
                        contentColor = colors.onSecondaryColor
                    ),
                    shape = RoundedCornerShape(10.dp)

                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = colors.onSecondaryColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (record.state == "validate" ) {
                Button(
                    onClick = {
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.tertiaryColor,
                        contentColor = colors.onSecondaryColor
                    ),
                    shape = RoundedCornerShape(10.dp)

                ) {
                    Text(
                        text = stringResource(R.string.ok),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
        title = {
            Column {
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
                    text = translateState(record.state, currentLanguage),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = colors.tertiaryColor,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = dateText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackgroundColor,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
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
                        text = "$translatedLeaveType: $formattedDuration $dayLabel",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.onBackgroundColor,
                        textAlign = TextAlign.Start,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                if (record.state == "refuse") {
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
                if (showDeleteConfirmation) {
                    DeleteConfirmationDialog(
                        onDismiss = { showDeleteConfirmation = false },
                        onConfirmDelete = {
                            val request = TimeOffRequestForRequestEmployee(
                                employee_token = token,
                                action = "unlink_draft_annual_leaves",
                                leave_type_id = record.leave_id,
                                request_date_from = record.start_date,
                                request_date_to = record.end_date,
                                leave_id = record.leave_id
                            )

                            Log.d("REQUEST_BODY", request.toString())

                            val response = sendApiForRequestTimeOff(request)
                            Log.d("API_RESPONSE", response.toString())

                            withContext(Dispatchers.Main) {
                                showDeleteConfirmation = false
                                onDismiss()
                                onRefreshRequest()
                            }
                        }
                    )
                }

                if (showNewVacationDialog) {
                    DateInfoDialog(
                        date = clickedDate ?: startDate,                         selectedDates = selectedDates ?: emptySet(),
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
        }
    )
}