package net.inspirehub.hr.time_off.components

import HourlyTimeOffRecord
import TimeOffRecord
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.CloseIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.forEach
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import net.inspirehub.hr.utils.convertToArabicDigits
import net.inspirehub.hr.utils.formatLocalizedTime
import net.inspirehub.hr.utils.formatNumber
import net.inspirehub.hr.utils.getLocalizedDayText


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailsDayBottomSheet(
    clickedDate: LocalDate?,
    dailyRecords: List<TimeOffRecord>,
    hourlyRecords: List<HourlyTimeOffRecord>,
    onDismiss: () -> Unit,
    onAddAnother: () -> Unit,
    onDelete: (leaveId: Int?, date: String?) -> Unit,
    leaveTypeColors: Map<String, Color>
) {
    val colors = appColors()
    val context = LocalContext.current
    val currentLanguage = remember { SharedPrefManager(context).getLanguage() }
    val locale = Locale.forLanguageTag(currentLanguage)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val requestsCount = dailyRecords.size + hourlyRecords.size

    val formattedDate = clickedDate?.format(
        DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
    )?.let {
        if (currentLanguage == "ar") convertToArabicDigits(it) else it
    } ?: ""

    val titleDate = dailyRecords.firstOrNull()?.let { record ->
        val start = LocalDate.parse(record.start_date)
        val end = LocalDate.parse(record.end_date)

        val text = if (start != end) {
            if (start.month == end.month && start.year == end.year) {
                "${start.dayOfMonth} - ${end.dayOfMonth} ${
                    end.format(DateTimeFormatter.ofPattern("MMM yyyy", locale))
                }"
            } else {
                "${start.format(DateTimeFormatter.ofPattern("d MMM yyyy", locale))} - ${
                    end.format(DateTimeFormatter.ofPattern("d MMM yyyy", locale))
                }"
            }
        } else {
            start.format(DateTimeFormatter.ofPattern("d MMMM yyyy", locale))
        }

        if (currentLanguage == "ar") convertToArabicDigits(text) else text
    } ?: formattedDate

    val requestText = if (requestsCount == 1)
            stringResource(R.string.request)
        else
            stringResource(R.string.requests)

    fun formatDecimalHourToTime(
        decimalHour: Double?,
        currentLanguage: String
    ): String {

        if (decimalHour == null) return ""

        val hours = decimalHour.toInt()
        val minutes = ((decimalHour - hours) * 60).toInt()

        return formatLocalizedTime(
            hour = hours,
            minute = minutes,
            language = currentLanguage
        )
    }

    ModalBottomSheet(
        containerColor = colors.surfaceContainerHigh,
        contentWindowInsets = { WindowInsets(0) },
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                CloseIcon(
                    onClick = { onDismiss() }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = titleDate,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackgroundColor
                )

                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.onBackgroundColor.copy(0.2f)
                    )
                ) {
                    Text(
                        text = "${formatNumber(requestsCount.toString(), currentLanguage)} $requestText",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                        color = colors.onBackgroundColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            if (dailyRecords.isNotEmpty()) {
                dailyRecords.forEach { record ->

                    val leaveColor = leaveTypeColors[record.leave_type] ?: colors.tertiaryColor

                    val duration = getLocalizedDayText(
                        record.duration_days,
                        currentLanguage
                    )

                    DetailsCardBottomSheet(
                        title = record.leave_type,
                        subTitle = duration,
                        state = record.state,
                        leaveColor = leaveColor,
                        onDelete =
                            if (record.state == "draft" || record.state == "confirm") {
                                {
                                    onDelete(
                                        record.leave_id,
                                        record.start_date
                                    )
                                }
                            } else null
                    )
                }
            }

            if (hourlyRecords.isNotEmpty()) {


                hourlyRecords.forEach { record ->
                    val leaveColor = leaveTypeColors[record.leave_type] ?: colors.tertiaryColor


                    val subTitle =
                        "${
                            formatDecimalHourToTime(
                                record.request_hour_from?.toDoubleOrNull(),
                                currentLanguage
                            )
                        } - " +
                                formatDecimalHourToTime(
                                    record.request_hour_to?.toDoubleOrNull(),
                                    currentLanguage
                                )

                    DetailsCardBottomSheet(
                        title = record.leave_type,
                        subTitle = subTitle,
                        state = record.state,
                        leaveColor = leaveColor,
                        onDelete =
                            if (record.state == "draft" || record.state == "confirm") {
                                {
                                    onDelete(
                                        record.leave_id,
                                        record.leave_day
                                    )
                                }
                            } else null
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            RequestTimeOffCard(
                onClick = {
                    onAddAnother()
                }
            )
        }
    }
}