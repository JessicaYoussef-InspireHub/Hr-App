package net.inspirehub.hr.check_in_out.components

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import java.time.ZonedDateTime
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
private fun currentDeviceHour(): Int = ZonedDateTime.now(TimeZone.getDefault().toZoneId()).hour

/** Time-of-day bucket. Depends only on the hour, never on the app language. */
@StringRes
private fun greetingResFor(hour: Int): Int = when (hour) {
    in 5..11 -> R.string.good_morning
    in 12..16 -> R.string.good_afternoon
    else -> R.string.good_evening
}

@SuppressLint("LocalContextConfigurationRead")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GreetingHeader(
    name: String
) {

    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }

    // --- 1. Time detection: device time zone + local time only ---
    var hour by remember { mutableIntStateOf(currentDeviceHour()) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val now = currentDeviceHour()
                if (now != hour) hour = now
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        // System broadcasts registered at runtime; no exported flag needed for these protected actions.
        context.registerReceiver(receiver, filter)
        hour = currentDeviceHour()
        onDispose { context.unregisterReceiver(receiver) }
    }

    // --- 2. Greeting language: app's selected language only ---
    // getLanguage() gives "en"/"ar"; the full tag is needed to tell Saudi
    // Arabic ("ar-SA" -> values-ar-rSA) apart from standard Arabic.
    val languageCode = sharedPref.getLanguage()
    val languageTag = sharedPref.getLanguageTag()
    val localizedResources = remember(languageCode, languageTag) {
        val locale = Locale.forLanguageTag(languageTag.ifBlank { languageCode })
        val config = Configuration(context.resources.configuration).apply { setLocale(locale) }
        context.createConfigurationContext(config).resources
    }

    Text(
        text = localizedResources.getString(greetingResFor(hour)) + ", " + name,
        color = colors.onBackgroundColor,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
    )
}
