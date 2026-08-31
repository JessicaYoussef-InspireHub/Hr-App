package net.inspirehub.hr.settings.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import net.inspirehub.hr.MainActivity
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager

/**
 * Everything the employee is shown about the attendance location check, in one
 * place, so every path that can put this notification on screen - or take it off -
 * says exactly the same thing.
 *
 * How it gets on screen: AttendanceLocationForegroundService hands build() straight
 * to startForeground() and Android owns the notification for as long as the service
 * lives. The service now lives for exactly one GPS reading, so the notification is
 * on screen for exactly one GPS reading.
 *
 * PERSISTENT_NOTIFICATION then decides whether it disappears between readings or
 * stays up until the reminders are switched off.
 *
 * Nothing here keeps the app alive. A notification is a message to the employee,
 * nothing more - even the foreground service one, which merely happens to be the
 * price Android charges for the service rather than the thing doing the work.
 */
object AttendanceLocationNotification {

    /** Same channel the service has always used, so nothing changes for the user. */
    const val CHANNEL_ID = "attendance_location_service"

    /** Same id the service has always used. */
    const val ID = 9100

    /**
     * Which notification the employee gets.
     *
     *  true  - one notification from the moment a reminder is enabled until the
     *          last one is switched off. Google Play requires this of a monitoring
     *          app: a persistent notification at all times while it runs.
     *  false - a notification only while a GPS reading is being taken, a few
     *          seconds every interval. This is the behaviour asked for. Nothing is
     *          hidden, but between readings there is no reminder, and that is the
     *          part Play's monitoring rules object to.
     *
     * Flip to true if this ever goes on the Play Store.
     */
    const val PERSISTENT_NOTIFICATION = false

    /** Cheap and idempotent; safe to call before every post. */
    fun ensureChannel(context: Context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager =
            context.getSystemService(NotificationManager::class.java) ?: return

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Attendance location tracking",
                NotificationManager.IMPORTANCE_LOW // no sound, no vibration
            ).apply {

                description = "Used while attendance reminders are enabled"

                setShowBadge(false)
            }

        notificationManager.createNotificationChannel(channel)
    }

    /**
     * The notification itself. The service needs the Notification object for
     * startForeground(), which is why this is public and separate from the posting
     * helpers below.
     */
    fun build(context: Context): Notification {

        val openAppIntent =
            Intent(
                context,
                MainActivity::class.java
            ).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                9101,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        return NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.inspire_hub_logo)
            .setContentTitle(context.getString(R.string.attendance_reminders_active))
            .setContentText(context.getString(R.string.work_location_checked_periodically))
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(false)
            .setForegroundServiceBehavior(
                NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
            )
            .setContentIntent(pendingIntent)
            .build()
    }

    /* ------------------------------------------------- lifecycle hooks */

    /** A reminder was switched on. Only means anything in persistent mode. */
    fun onRemindersEnabled(context: Context) {

        if (PERSISTENT_NOTIFICATION && anyReminderEnabled(context)) {
            post(context)
        }
    }

    /**
     * A reading finished. In persistent mode the notification stays; otherwise it
     * goes away until the next alarm.
     *
     * The anyReminderEnabled() check matters: this also runs on the paths where the
     * employee switched the reminders off mid-reading, and there we must not
     * re-post over a notification we are supposed to be removing.
     */
    fun onFixDone(context: Context) {

        if (PERSISTENT_NOTIFICATION && anyReminderEnabled(context)) {
            post(context)
        } else {
            hide(context)
        }
    }

    /** The last reminder was switched off. */
    fun onRemindersDisabled(context: Context) = hide(context)

    fun hide(context: Context) {

        val notificationManager =
            context.getSystemService(NotificationManager::class.java) ?: return

        runCatching { notificationManager.cancel(ID) }
    }

    /* ------------------------------------------------------ internals */

    private fun post(context: Context) {

        ensureChannel(context)

        val notificationManager =
            context.getSystemService(NotificationManager::class.java) ?: return

        // Silently dropped if POST_NOTIFICATIONS was refused on Android 13+. That is
        // the employee's choice and it must not break the location checks, so we do
        // not treat it as an error.
        runCatching { notificationManager.notify(ID, build(context)) }
    }

    private fun anyReminderEnabled(context: Context): Boolean {

        val sharedPrefManager = SharedPrefManager(context.applicationContext)

        return sharedPrefManager.isCheckInReminderEnabled() ||
                sharedPrefManager.isCheckOutReminderEnabled()
    }
}
