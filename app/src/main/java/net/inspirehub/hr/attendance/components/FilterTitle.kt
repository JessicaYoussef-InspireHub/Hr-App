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
import net.inspirehub.hr.KeyboardArrowLeftIcon
import net.inspirehub.hr.KeyboardArrowRightIcon
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.formatLocalizedDateRange
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FilterTitle(
    startDate: LocalDate,
    endDate: LocalDate,
    onPrevious: () -> Unit,
    onNext: () -> Unit
){
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    val currentLanguage = sharedPref.getLanguage()

    val dateText = formatLocalizedDateRange(
        startDate = startDate,
        endDate = endDate,
        language = currentLanguage
    )


    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        KeyboardArrowLeftIcon(
            color = colors.onBackgroundColor,
            onClick = onPrevious
        )

        Text(
            text = "By month: $dateText",
            color = colors.onBackgroundColor
        )

        KeyboardArrowRightIcon(
            color = colors.onBackgroundColor,
            onClick = onNext
        )
    }
}