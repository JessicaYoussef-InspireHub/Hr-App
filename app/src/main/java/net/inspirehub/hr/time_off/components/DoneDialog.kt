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
import kotlinx.coroutines.CoroutineScope
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.time_off.data.TimeOffRequestForRequestEmployee
import net.inspirehub.hr.time_off.data.sendApiForRequestTimeOff
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.inspirehub.hr.AddIcon
import net.inspirehub.hr.CloseIcon
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.SmallButtons
import net.inspirehub.hr.utils.convertToArabicDigits
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
    val sharedPref = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPref.getLanguage() // "ar" أو "en"
    val currentLocale = if (currentLanguage == "ar") Locale("ar") else Locale.ENGLISH

    val sameDay = startDate == endDate
    val sameMonth = startDate.month == endDate.month && startDate.year == endDate.year
    val colors = appColors()
    var apiErrorMessage by remember { mutableStateOf<String?>(null) }



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
                SmallButtons(
                    onConfirm = {
                        showDeleteConfirmation = true
                    },
                    onDismiss = {  },
                    confirmButtonText = stringResource(R.string.delete),
                )
            } else if (record.state == "validate" ) {

                SmallButtons(
                    onConfirm = {
                        onDismiss()
                    },
                    onDismiss = {  },
                    confirmButtonText = stringResource(R.string.ok),
                )
            }
        },
        title = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    CloseIcon(
                        onClick = { onDismiss() }
                    )
                }
                Text(
                    text = record.state,
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

                        "cancel" -> {
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
                        text = "${record.leave_type}: $formattedDuration $dayLabel",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.onBackgroundColor,
                        textAlign = TextAlign.Start,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                if (record.state == "cancel") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AddIcon(
                            modifier = Modifier.size(20.dp),
                            onClick = {
                                showNewVacationDialog = true
                            }
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
                    MyDialog(
                        onDismiss = { showDeleteConfirmation = false },
                        onConfirm = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val request = TimeOffRequestForRequestEmployee(
                                    employee_token = token,
                                    action = "unlink_draft_annual_leaves",
                                    leave_type_id = record.leave_id,
                                    request_date_from = record.start_date,
                                    request_date_to = record.end_date,
                                    leave_id = record.leave_id
                                )

                                Log.d("REQUEST_BODY", request.toString())

                                val response = sendApiForRequestTimeOff(context, request)
                                Log.d("API_RESPONSE", response.toString())

                                withContext(Dispatchers.Main) {

                                    if (response?.result?.status == "error") {
                                        apiErrorMessage = response.result.message ?: "Unknown error"
                                    } else {
                                        showDeleteConfirmation = false
                                        onDismiss()
                                        onRefreshRequest()}
                                }
                            }
                        },
                        title = stringResource(R.string.delete_confirmation),
                        subtitle = stringResource(R.string.are_you_sure_you_want_to_delete_this_request),
                        confirmButtonText = stringResource(R.string.yes_delete),
                        dismissButtonText = stringResource(R.string.cancel)
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

                if (apiErrorMessage != null) {
                    MyDialog(
                        onDismiss = { apiErrorMessage = null },
                        onConfirm = {
                            onRefreshRequest()
                            apiErrorMessage = null
                        },
                        title = stringResource(R.string.invalid_request),
                        subtitle = apiErrorMessage!!,
                        confirmButtonText = stringResource(R.string.ok)
                    )
                }
            }
        }
    )
}