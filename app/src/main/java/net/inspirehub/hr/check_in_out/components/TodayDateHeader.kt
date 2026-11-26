package net.inspirehub.hr.check_in_out.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import java.time.DayOfWeek
import java.time.LocalDate
import net.inspirehub.hr.utils.formatNumber

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodayDateHeader() {

    val colors = appColors()
    val context = LocalContext.current
    var today by remember { mutableStateOf(LocalDate.now()) }
    val sharedPref = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPref.getLanguage()

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val now = LocalDate.now()
                if (now != today) today = now
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        // System broadcasts registered at runtime; no exported flag needed for these protected actions.
        context.registerReceiver(receiver, filter)
        today = LocalDate.now()
        onDispose { context.unregisterReceiver(receiver) }
    }

    val dayName = stringResource(
        when (today.dayOfWeek) {
            DayOfWeek.MONDAY -> R.string.monday
            DayOfWeek.TUESDAY -> R.string.tuesday
            DayOfWeek.WEDNESDAY -> R.string.wednesday
            DayOfWeek.THURSDAY -> R.string.thursday
            DayOfWeek.FRIDAY -> R.string.friday
            DayOfWeek.SATURDAY -> R.string.saturday
            DayOfWeek.SUNDAY -> R.string.sunday
        }
    )

    val day = formatNumber(today.dayOfMonth.toString(), currentLanguage)
    val month = formatNumber(today.monthValue.toString(), currentLanguage)
    val year = formatNumber(today.year.toString(), currentLanguage)

    Text(
        text = stringResource(
            R.string.today_date_format,
            dayName, day, month, year
        ),
        color = colors.onBackgroundColor.copy(alpha = 0.7f),
        fontSize = 15.sp
    )
}
