package net.inspirehub.hr.time_off.components

import HourlyTimeOffRecord
import TimeOffRecord
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import net.inspirehub.hr.CloseIcon
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.SmallButtons
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.convertToArabicDigits
import net.inspirehub.hr.utils.getLocalizedHourText

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyAndHourlyDialog(
    dailyRecords: List<TimeOffRecord>,
    hourlyRecords: List<HourlyTimeOffRecord>,
    onDismiss: () -> Unit,
    clickedDate: LocalDate?
) {
    val colors = appColors()
    val context = LocalContext.current
    val currentLanguage = remember { SharedPrefManager(context).getLanguage() }
    val locale = Locale.forLanguageTag(currentLanguage)

    val formattedDate = clickedDate?.format(
        DateTimeFormatter.ofPattern("dd MMMM yyyy", locale)
    ) ?: ""


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

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceVariant,
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
                    CloseIcon(
                        onClick = { onDismiss() }
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
                    dailyRecords.forEach { record ->

                        val translatedLeaveType = record.leave_type
                        val duration = record.duration_days

                        val displayText = if (duration % 1.0 == 0.0) {
                            duration.toInt().toString()
                        } else {
                            duration.toString()
                        }

                        val dayWord = when (duration) {
                            0.5 -> {
                                if (currentLanguage == "ar") "يوم" else "Day"
                            }
                            1.0 -> {
                                if (currentLanguage == "ar") "يوم" else "Day"
                            }
                            2.0 -> {
                                if (currentLanguage == "ar") "يومين" else "Days"
                            }
                            in 3.0..10.0 -> {
                                if (currentLanguage == "ar") "أيام" else "Days"
                            }
                            else -> {
                                if (currentLanguage == "ar") "يومًا" else "Days"
                            }
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                when (record.state) {
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
                                "$translatedLeaveType: $displayText $dayWord",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Normal,
                                color = colors.onBackgroundColor,
                                modifier = Modifier.padding(start = 20.dp),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
           Column {
                    hourlyRecords.forEach { record ->


                        val translatedLeaveType = record.leave_type
                        val durationInt = record.duration_hours
                        val hourWord = getLocalizedHourText(
                            count = durationInt,
                            language = currentLanguage
                        )

               Column {
                   Row (
                       modifier = Modifier.fillMaxWidth(),
                       horizontalArrangement = Arrangement.Start,
                       verticalAlignment = Alignment.CenterVertically,
                   ){

                       when (record.state) {
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
                       "$translatedLeaveType: $hourWord",
                       fontSize = 17.sp,
                       fontWeight = FontWeight.Normal,
                       color = colors.onBackgroundColor,
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
                       color = colors.onBackgroundColor,
                       modifier = Modifier.padding(start = 20.dp),
                   )
                   Spacer(modifier = Modifier.height(12.dp))
               }
                }}
            }
        },
        confirmButton = {
            SmallButtons(
                onConfirm = { onDismiss() },
                confirmButtonText = stringResource(R.string.ok)
            )
        }
    )
}

