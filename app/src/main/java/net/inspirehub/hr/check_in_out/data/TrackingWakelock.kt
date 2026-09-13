package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.os.PowerManager
import android.util.Log

/**
 * The same job AttendanceReminderWakelock does for the reminder, for the tracking
 * alarm. Deliberately a second lock rather than a shared one: a tracking fix and a
 * reminder fix can overlap, and whichever finished first would otherwise drop the
 * CPU out from under the other.
 *
 * The alarm wakes the phone only for the milliseconds the broadcast takes; a fix
 * needs seconds, and a foreground service does not hold the CPU by itself.
 */
object TrackingWakelock {

    private const val NAME = "InspireHub:tracking-fix"

    /** A bit longer than the timeout in SingleLocationFix. */
    private const val TIMEOUT_MS = 70_000L

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

        Log.d(
            "TEST_TRACKING_ALARM",
            "CPU held awake for the tracking fix (max ${TIMEOUT_MS / 1000}s)"
        )
    }

    @Synchronized
    fun release() {

        val wakeLock = lock ?: return

        if (wakeLock.isHeld) {

            runCatching { wakeLock.release() }

            Log.d("TEST_TRACKING_ALARM", "CPU released, phone may sleep again")
        }

        lock = null
    }
}
