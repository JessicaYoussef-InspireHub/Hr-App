package net.inspirehub.hr.settings.data

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import androidx.core.net.toUri

/**
 * The battery-optimisation exemption, which is the single biggest reliability switch
 * the attendance reminder has.
 *
 * Two separate things depend on it:
 *
 *  - From Android 12 on, a process that is in the background may not start a
 *    foreground service. The alarm receiver is in the background by definition, so
 *    without this exemption every scheduled check is refused and the app falls back
 *    to reading the position inside the broadcast, which has roughly ten seconds -
 *    usually not enough for a cold GPS fix.
 *  - On aggressive vendor ROMs (Xiaomi, Samsung, Huawei, Oppo) it is the difference
 *    between the alarm surviving the night and the app being killed after an hour.
 *
 * Nothing here forces anything on the employee: it only reads the state and builds
 * the Intent that opens the system dialog. The card asks, the employee decides, and
 * the card keeps a warning on screen for as long as the answer is no.
 */
object AttendanceReminderPowerSettings {

    /** True when Android has been told to leave this app alone. */
    fun isBatteryUnrestricted(context: Context): Boolean = runCatching {

        context.getSystemService(PowerManager::class.java)
            ?.isIgnoringBatteryOptimizations(context.packageName) == true

    }.getOrDefault(false)

    /**
     * The system dialog that asks for the exemption in one tap. Needs
     * REQUEST_IGNORE_BATTERY_OPTIMIZATIONS in the manifest, and some ROMs remove the
     * Activity altogether - hence [batterySettingsIntent] as the fallback.
     */
    fun exemptionIntent(context: Context): Intent = Intent(
        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
        "package:${context.packageName}".toUri()
    )

    /** The full "battery optimisation" list, where the app has to be found by hand. */
    fun batterySettingsIntent(): Intent =
        Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
}
