package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.util.Log
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.sign_in.data.getTrackingConfig

/**
 * Reads the tracking settings from the server, saves them, and applies them to the
 * tracking that runs now.
 *
 * Used by the location_tracking_config_update push and when the check in/out screen
 * opens, so a push that failed while the phone was offline is caught up on the next
 * visit. A failed call keeps the saved settings as they are.
 *
 * Returns true when the settings were read and saved.
 */
suspend fun refreshTrackingConfig(
    context: Context,
    token: String
): Boolean {

    val appContext = context.applicationContext

    val sharedPref = SharedPrefManager(appContext)

    return try {

        val config = getTrackingConfig(
            context = appContext,
            employeeToken = token
        ).result

        val intervalChanged =
            sharedPref.getTrackingIntervalMinutes() != config.tracking_interval_minutes

        sharedPref.saveIsTracked(config.is_tracked)

        sharedPref.saveWorkingHoursOnly(config.working_hours_only)

        sharedPref.saveTrackingIntervalMinutes(config.tracking_interval_minutes)

        sharedPref.saveMinDistanceMeters(config.min_distance_meters)

        sharedPref.saveShowNotification(config.show_notification)

        Log.d("TEST FCM_CONFIG", "✅ Latest config saved from API")

        Log.d(
            "TEST FCM_CONFIG",
            "isTracked=${config.is_tracked} | " +
                    "workingHoursOnly=${config.working_hours_only} | " +
                    "interval=${config.tracking_interval_minutes} | " +
                    "minDistance=${config.min_distance_meters} | " +
                    "showNotification=${config.show_notification} | " +
                    "intervalChanged=$intervalChanged"
        )

        // Update tracking immediately
        if (intervalChanged) {
            LocationTrackingManager.applyNewInterval(appContext)
        } else {
            LocationTrackingManager.updateTracking(appContext)
        }

        true

    } catch (e: Exception) {

        Log.e("TEST FCM_CONFIG", "❌ Failed to fetch tracking config", e)

        false
    }
}
