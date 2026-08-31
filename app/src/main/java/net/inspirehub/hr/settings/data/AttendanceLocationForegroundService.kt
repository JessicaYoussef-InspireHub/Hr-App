package net.inspirehub.hr.settings.data

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ServiceCompat
import net.inspirehub.hr.SharedPrefManager

/**
 * The foreground service that hosts one GPS reading for the attendance reminder.
 *
 * It is short-lived on purpose. It used to run all day - which is why its
 * notification never went away; now it exists for exactly as long as one GPS fix
 * takes: a few seconds normally, at worst the timeout in AttendanceLocationFix.
 *
 * Life of one instance:
 *   alarm fires -> LocalAttendanceReminderReceiver starts us with
 *   ACTION_GET_LOCATION -> we become a foreground service (notification appears)
 *   -> one fix -> stopSelf() (notification disappears, unless
 *   AttendanceLocationNotification.PERSISTENT_NOTIFICATION detaches it) -> the
 *   process is free to die until the next alarm.
 *
 * The catch: from Android 12 on, a process that is in the background is not allowed
 * to start a foreground service, and an alarm receiver is in the background by
 * definition. LocalAttendanceReminderReceiver catches that refusal and reads the
 * position inside the broadcast instead.
 */
class AttendanceLocationForegroundService : Service() {

    companion object {

        const val ACTION_START = "net.inspirehub.hr.ATTENDANCE_LOCATION_START"

        const val ACTION_GET_LOCATION = "net.inspirehub.hr.ATTENDANCE_LOCATION_GET"

        const val ACTION_STOP = "net.inspirehub.hr.ATTENDANCE_LOCATION_STOP"

        /**
         * Is a reading happening right now. Static, not an instance field, because
         * the service now dies between readings: the receiver fallback path and the
         * "skip this tick" check both need to know without holding a reference to a
         * service that may not exist.
         */
        @Volatile
        var isTakingFix = false
            internal set
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val action = intent?.action

        /*
         * Every cheap reason to give up is checked BEFORE we go foreground, so none
         * of those cases puts a notification on screen at all. Stopping without ever
         * calling startForeground() is allowed as long as we are quick about it, and
         * we are - nothing above that line does any real work.
         */

        if (action == ACTION_STOP) {

            finish("stop requested")

            return START_NOT_STICKY
        }

        val sharedPrefManager = SharedPrefManager(applicationContext)

        if (
            !sharedPrefManager.isCheckInReminderEnabled() &&
            !sharedPrefManager.isCheckOutReminderEnabled()
        ) {

            finish("no reminder enabled")

            return START_NOT_STICKY
        }

        if (
            action != ACTION_START &&
            action != ACTION_GET_LOCATION
        ) {

            /*
             * Either Android restarted us on its own (intent == null) or someone
             * sent an action we do not handle. The alarm owns the schedule, so the
             * right answer is always to go away and wait for it.
             */

            Log.w(
                "Alarm",
                "Service started without a location request -> going back to sleep " +
                        "(action=" + action + " startId=" + startId + " flags=" + flags + ")"
            )

            finish("not a location request")

            return START_NOT_STICKY
        }

        if (isTakingFix) {

            /*
             * This tick arrived while the GPS was still searching for the previous
             * one. Let the running fix finish; it owns the notification and it will
             * stop the service itself.
             */

            Log.d("Alarm", "Location request already running -> skip")

            return START_NOT_STICKY
        }

        // Android gives us ~10 seconds to become a foreground service.
        if (!goForeground()) return START_NOT_STICKY

        Log.d("Alarm", "Service up for one reading (startId=" + startId + ")")

        takeFix()

        /*
         * Nothing to resurrect if we are killed mid-fix: the next alarm is already
         * booked by the receiver, so a sticky restart would only cost a pointless
         * notification.
         */
        return START_NOT_STICKY
    }

    /** One GPS reading, then take the notification down and stop. */
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun takeFix() {

        isTakingFix = true

        AttendanceLocationFix.request(
            context = applicationContext
        ) { location ->

            isTakingFix = false

            if (location != null) {

                Log.d(
                    "Alarm",
                    "Foreground service -> GPS Location = " +
                            "${location.latitude}, " +
                            "${location.longitude}, " +
                            "accuracy=${location.accuracy}"
                )
            }

            LocalAttendanceReminderReceiver.handleFixResult(
                context = applicationContext,
                location = location
            )

            /*
             * Wake lock first, then stop: releasing after stopSelf() would run
             * against a half torn-down service.
             */
            AttendanceReminderWakelock.release(applicationContext)

            finish("fix finished")
        }
    }

    /** Take the notification down and stop. Safe even if we never went foreground. */
    private fun finish(why: String) {

        Log.d("Alarm", "Service stopping - " + why)

        AttendanceReminderWakelock.release(applicationContext)

        /*
         * DETACH leaves the notification on screen after the service is gone, which
         * is the only way a foreground-service notification can outlive its service.
         * REMOVE takes it with us. onFixDone() then either re-posts the detached one
         * or cancels it.
         */
        ServiceCompat.stopForeground(
            this,
            if (AttendanceLocationNotification.isPersistentNotificationEnabled(applicationContext)) {
                ServiceCompat.STOP_FOREGROUND_DETACH
            } else {
                ServiceCompat.STOP_FOREGROUND_REMOVE
            }
        )

        AttendanceLocationNotification.onFixDone(applicationContext)

        stopSelf()
    }

    /**
     * @return false when we must give up (a missing permission would make
     * startForeground() throw on Android 14+, or the OS refuses the start).
     */
    private fun goForeground(): Boolean {

        if (
            checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Log.e("Alarm", "Cannot start location foreground service: permission missing")

            finish("no location permission")

            return false
        }

        return try {

            AttendanceLocationNotification.ensureChannel(this)

            ServiceCompat.startForeground(
                this,
                AttendanceLocationNotification.ID,
                AttendanceLocationNotification.build(this),
                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.Q
                ) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                } else {
                    0
                }
            )

            true

        } catch (e: Throwable) {

            Log.e("Alarm", "Could not promote service to foreground", e)

            finish("foreground refused")

            false
        }
    }

    override fun onDestroy() {

        if (isTakingFix) {

            Log.w(
                "Alarm",
                "Service destroyed while a fix was still running - this reading is lost"
            )
        }

        isTakingFix = false

        AttendanceReminderWakelock.release(applicationContext)

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = null
}
