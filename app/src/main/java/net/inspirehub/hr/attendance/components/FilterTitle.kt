package net.inspirehub.hr.attendance.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.KeyboardArrowLeftIcon
import net.inspirehub.hr.KeyboardArrowRightIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.formatLocalizedDate
import net.inspirehub.hr.utils.formatLocalizedDateRange
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FilterTitle(
    currentDate: LocalDate,
    selectedFilter: TimeFilter,
    fromDate: LocalDate? = null,
    toDate: LocalDate? = null,
    onPrevious: () -> Unit,
    onNext: () -> Unit
){
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    val currentLanguage = sharedPref.getLanguage()
    val locale = java.util.Locale.forLanguageTag(currentLanguage)

    val title = when (selectedFilter) {
        TimeFilter.DAY -> stringResource(R.string.by_day)
        TimeFilter.WEEK -> stringResource(R.string.by_week)
        TimeFilter.MONTH -> stringResource(R.string.by_month)
        TimeFilter.QUARTER -> stringResource(R.string.by_quarter)
        TimeFilter.YEAR -> stringResource(R.string.by_year)
        TimeFilter.CUSTOM -> stringResource(R.string.custom)
    }

    val dateText = when (selectedFilter) {

        TimeFilter.DAY -> {
            formatLocalizedDate(
                currentDate.toString(),
                currentLanguage
            )
        }

        TimeFilter.WEEK -> {
            val start = currentDate.with(java.time.DayOfWeek.MONDAY)
            val end = currentDate.with(java.time.DayOfWeek.SUNDAY)

            formatLocalizedDateRange(
                start,
                end,
                currentLanguage
            )
        }

        TimeFilter.MONTH -> {
            "${currentDate.month.getDisplayName(java.time.format.TextStyle.FULL, locale)} ${
                net.inspirehub.hr.utils.formatNumber(
                    currentDate.year.toString(),
                    currentLanguage
                )
            }"
        }

        TimeFilter.QUARTER -> {
            val startMonth = ((currentDate.monthValue - 1) / 3) * 3 + 1

            val start = java.time.Month.of(startMonth)
                .getDisplayName(java.time.format.TextStyle.FULL, locale)

            val end = java.time.Month.of(startMonth + 2)
                .getDisplayName(java.time.format.TextStyle.FULL, locale)

            "$start - $end ${
                net.inspirehub.hr.utils.formatNumber(
                    currentDate.year.toString(),
                    currentLanguage
                )
            }"
        }

        TimeFilter.YEAR -> {
            net.inspirehub.hr.utils.formatNumber(
                currentDate.year.toString(),
                currentLanguage
            )
        }

        TimeFilter.CUSTOM -> {
            if (fromDate != null && toDate != null) {
                formatLocalizedDateRange(
                    fromDate,
                    toDate,
                    currentLanguage
                )
            } else {
                ""
            }
        }
    }


    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        KeyboardArrowLeftIcon(
            onClick = onPrevious
        )

        Text(
            text = "$title: $dateText",
            color = colors.onBackgroundColor
        )

        KeyboardArrowRightIcon(
            onClick = onNext
        )
    }
}