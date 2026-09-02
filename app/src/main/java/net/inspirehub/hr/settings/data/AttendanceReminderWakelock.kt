package net.inspirehub.hr.settings.data

import android.content.Context
import android.os.PowerManager
import android.util.Log

/**
 * The alarm wakes the phone, but only for the few milliseconds the broadcast takes.
 * Getting a satellite fix takes seconds, the callbacks need a running CPU, and a
 * foreground service does NOT keep the CPU awake by itself - so we hold a partial
 * wake lock (screen stays off, CPU stays on) for the duration of one fix and
 * release it the moment we are done.
 *
 * It also has a safety timeout so a bug can never drain a battery all night.
 */
object AttendanceReminderWakelock {

    private const val NAME = "InspireHub:attendance-fix"

    /** A bit longer than the GPS timeout in AttendanceLocationFix. */
    private const val TIMEOUT_MS = 90_000L

    private var lock: PowerManager.WakeLock? = null

    @Synchronized
    fun acquire(context: Context) {

        if (lock?.isHeld == true) return

        val powerManager =
            context.getSystemService(PowerManager::class.java) ?: return

        val wakeLock =
            powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                NAME
            )

        wakeLock.setReferenceCounted(false)

        wakeLock.acquire(TIMEOUT_MS)

        lock = wakeLock

        Log.d("Alarm", "CPU held awake for the location fix (max ${TIMEOUT_MS / 1000}s)")
    }

    @Synchronized
    fun release() {

        val wakeLock = lock ?: return

        if (wakeLock.isHeld) {

            runCatching { wakeLock.release() }

            Log.d("Alarm", "CPU released, phone may sleep again")
        }

        lock = null
    }
}
