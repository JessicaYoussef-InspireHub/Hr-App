package net.inspirehub.hr.check_in_out.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.inspirehub.hr.SharedPrefManager

/**
 * One tick of the tracking chain, used only when showNotification is false.
 *
 * The order matters and mirrors LocalAttendanceReminderReceiver:
 *
 *   1. schedule the NEXT tick immediately, so the chain can never break even if
 *      everything below fails,
 *   2. hold the CPU awake,
 *   3. hand the reading to the foreground service,
 *   4. if Android refuses the service start, take the reading right here.
 *
 * Step 4 is the normal path on Android 12+ for an employee who has not been granted
 * a battery-optimisation exemption: a background process may not start a foreground
 * service. A broadcast has far less time to work with than a service, but it beats
 * skipping the round - and on that path no notification is shown at all.
 */
class TrackingAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {

        val appContext = context.applicationContext

        val sharedPref = SharedPrefManager(appContext)

        if (sharedPref.getShowNotification()) {

            /*
             * The backend moved us back to the foreground-service mode while an
             * alarm was still pending. The service owns the schedule there, so this
             * chain has to end.
             */

            Log.d(
                "TEST_TRACKING_ALARM",
                "showNotification=true -> alarm mode is over, cancelling the chain"
            )

            TrackingAlarmManager.stop(appContext)

            return
        }

        if (!hasTrackingLocationPermissions(appContext)) {

            Log.d(
                "TEST_TRACKING_ALARM",
                "Location permissions missing -> ending the chain"
            )

            TrackingAlarmManager.stop(appContext)

            return
        }

        if (!LocationFixHandler.shouldTrack(appContext)) {

            Log.d(
                "TEST_TRACKING_ALARM",
                "Tracking should not run -> ending the chain"
            )

            /*
             * stop() rather than a bare return: a PendingIntent survives the alarm
             * that used it, so leaving it in place would make isScheduled() report a
             * chain that is not actually running, and the next check-in would never
             * restart tracking.
             */
            TrackingAlarmManager.stop(appContext)

            return
        }

        /*
         * Booked HERE, before anything that can fail, so the chain can never break.
         */
        TrackingAlarmManager.scheduleNext(appContext)

        Log.d("TEST_TRACKING_ALARM", "⏰ Alarm fired -> requesting a reading")

        /*
         * Held for the gap between this broadcast and the reading actually starting.
         * Whoever finishes the reading releases it, and the safety timeout inside
         * TrackingWakelock covers the case where nothing ever starts.
         */
        TrackingWakelock.acquire(appContext)

        try {

            appContext.startTrackingSingleFix()

        } catch (t: Throwable) {

            Log.e(
                "TEST_TRACKING_ALARM",
                "Foreground service refused -> taking the reading inside the alarm",
                t
            )

            val pendingResult = goAsync()

            SingleLocationFix.request(appContext) { location ->

                if (location == null) {

                    TrackingWakelock.release()

                    runCatching { pendingResult.finish() }

                    return@request
                }

                CoroutineScope(Dispatchers.IO).launch {

                    runCatching {
                        LocationFixHandler.handle(appContext, location)
                    }.onFailure {
                        Log.e("TEST_TRACKING_ALARM", "Reading failed", it)
                    }

                    TrackingWakelock.release()

                    runCatching { pendingResult.finish() }
                }
            }
        }
    }
}
