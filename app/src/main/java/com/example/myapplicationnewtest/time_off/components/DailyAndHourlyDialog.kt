package com.example.myapplicationnewtest.time_off.components

import HourlyTimeOffRecord
import TimeOffRecord
import android.content.Context
import android.os.Build
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyAndHourlyDialog(
    dailyRecords: List<TimeOffRecord>,
    hourlyRecords: List<HourlyTimeOffRecord>,
    onDismiss: () -> Unit,
    token: String,
    onRefreshRequest: () -> Unit,
    clickedDate: LocalDate?
) {
    val formattedDate = clickedDate?.format(
        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
    ) ?: ""

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

    fun getLocalizedDayText(
        context: Context,
        count: Int,
        language: String
    ): String {
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

    fun getLocalizedHourText(context: Context, count: Double?, language: String): String {
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

    val hasValidate = hourlyRecords.any { it.state == "validate" } || dailyRecords.any { it.state == "validate" }
    val hasConfirm = hourlyRecords.any { it.state == "confirm" } || dailyRecords.any { it.state == "confirm" }
    val hasDraft = hourlyRecords.any { it.state == "draft" } || dailyRecords.any { it.state == "draft" }

    val buttonColor = if ( hasValidate ) {
        MaterialTheme.colorScheme.secondary
    } else if (hasDraft || hasConfirm) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.error
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.onPrimary,
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
                    dailyRecords.forEach { record ->

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
                        val durationInt = record.duration_days.toInt()
                        val daysText =
                            if (currentLanguage == "ar") convertToArabicDigits(durationInt.toString()) else durationInt.toString()
                        val dayWord = getLocalizedDayText(
                            context = androidx.compose.ui.platform.LocalContext.current,
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
                                "$translatedLeaveType: $daysText $dayWord",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(start = 20.dp),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
           Column {
                    hourlyRecords.forEach { record ->
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
                            context = androidx.compose.ui.platform.LocalContext.current,
                            count = durationInt,
                            language = currentLanguage
                        )

               Column {
                   Row (
                       modifier = Modifier.fillMaxWidth(),
                       horizontalArrangement = Arrangement.Start,
                       verticalAlignment = Alignment.CenterVertically,
                   ){
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
                   Spacer(modifier = Modifier.height(12.dp))
               }
                }}
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(stringResource(R.string.ok),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

