package net.inspirehub.hr.notifications.data

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import net.inspirehub.hr.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Writes every push message to the log in full, under one tag. Read it with:
 * `adb logcat -s HR-Push`
 *
 * Four moments are written, so every case is covered:
 *  1. raw     - the message exactly as Android handed it over, for every push, also
 *               the ones Android shows by itself while the app is closed,
 *  2. message - the same message field by field, when the app's own code handles it,
 *  3. deleted - the server's messages that Google dropped before they arrived,
 *  4. opened  - what the app gets when the employee taps a notification.
 *
 * Debug builds only: a push can carry an employee's data.
 *
 * Note: the app never sees the HTTP headers the server sent to Google. Firebase hands
 * over the message's own fields (id, sender, sent time, priority, time to live) and
 * its two parts, data and notification. All of them are written here.
 */
object PushLogger {

    const val TAG = "HR-Push"

    /** Android's log cuts a line at about 4000 characters, so longer values are split. */
    private const val CHUNK = 3000

    /**
     * Called from FirebaseMessagingService.handleIntent(), before Firebase decides
     * anything. The only place that sees a push with a notification part while the app
     * is in the background: Firebase shows those itself and never calls
     * onMessageReceived().
     */
    fun logRaw(intent: Intent) = safely("raw") {

        line("━━━━━━━━ 📨 PUSH ARRIVED (raw, as Android delivered it) ━━━━━━━━")
        line("action = ${intent.action}")
        logBundle(intent.extras)
        line("━━━━━━━━ end of raw push ━━━━━━━━")
    }

    /** Called first thing in onMessageReceived(). */
    @Suppress("DEPRECATION")
    fun logMessage(message: RemoteMessage) = safely("message") {

        line("━━━━━━━━ 📩 PUSH HANDLED BY THE APP (onMessageReceived) ━━━━━━━━")

        line("── meta ──")
        line("messageId = ${message.messageId}")
        line("messageType = ${message.messageType}")
        line("from = ${message.from}")
        line("to = ${message.to}")
        line("senderId = ${message.senderId}")
        line("collapseKey = ${message.collapseKey}")
        line("sentTime = ${message.sentTime} (${formatTime(message.sentTime)})")
        line("ttl = ${message.ttl} seconds")
        line("priority = ${priorityName(message.priority)}")
        line("originalPriority = ${priorityName(message.originalPriority)}")

        line("── data (${message.data.size} keys) ──")
        if (message.data.isEmpty()) {
            line("(none)")
        } else {
            message.data.toSortedMap().forEach { (key, value) -> line("$key = $value") }
        }

        val rawData = message.rawData
        if (rawData != null) {
            line("── rawData (${rawData.size} bytes) ──")
            line(String(rawData, Charsets.UTF_8))
        }

        logNotificationPart(message.notification)

        line("━━━━━━━━ end of handled push ━━━━━━━━")
    }

    /**
     * Called from onDeletedMessages(). Google dropped messages meant for this phone,
     * most often because the phone was offline longer than their time to live or too
     * many were waiting. What they said is lost; only this line says it happened.
     */
    fun logDeleted() = safely("deleted") {

        line("━━━━━━━━ 🗑 GOOGLE DROPPED PUSH MESSAGES BEFORE THEY ARRIVED ━━━━━━━━")
    }

    /**
     * Called when MainActivity starts or is brought back. Writes the extras only when
     * they come from a notification: one Android showed by itself (google.* keys) or
     * one the app showed (navigateTo).
     */
    fun logOpened(intent: Intent?) = safely("opened") {

        val extras = intent?.extras ?: return@safely

        val fromPush = extras.keySet().any { it.startsWith("google.") || it == "from" }
        val fromOwnNotification = extras.containsKey("navigateTo")

        if (!fromPush && !fromOwnNotification) return@safely

        line("━━━━━━━━ 👆 NOTIFICATION TAPPED, APP OPENED WITH ━━━━━━━━")
        line("shown by = ${if (fromPush) "Android (the push had a notification part)" else "the app"}")
        line("action = ${intent.action}")
        logBundle(extras)
        line("━━━━━━━━ end of tap ━━━━━━━━")
    }

    private fun logNotificationPart(notification: RemoteMessage.Notification?) {

        if (notification == null) {
            line("── notification part ──")
            line("(none - data-only message)")
            return
        }

        line("── notification part ──")
        line("title = ${notification.title}")
        line("titleLocalizationKey = ${notification.titleLocalizationKey}")
        line("titleLocalizationArgs = ${notification.titleLocalizationArgs?.contentToString()}")
        line("body = ${notification.body}")
        line("bodyLocalizationKey = ${notification.bodyLocalizationKey}")
        line("bodyLocalizationArgs = ${notification.bodyLocalizationArgs?.contentToString()}")
        line("icon = ${notification.icon}")
        line("imageUrl = ${notification.imageUrl}")
        line("sound = ${notification.sound}")
        line("tag = ${notification.tag}")
        line("color = ${notification.color}")
        line("clickAction = ${notification.clickAction}")
        line("channelId = ${notification.channelId}")
        line("link = ${notification.link}")
        line("ticker = ${notification.ticker}")
        line("sticky = ${notification.sticky}")
        line("localOnly = ${notification.localOnly}")
        line("defaultSound = ${notification.defaultSound}")
        line("defaultVibrateSettings = ${notification.defaultVibrateSettings}")
        line("defaultLightSettings = ${notification.defaultLightSettings}")
        line("notificationPriority = ${notification.notificationPriority}")
        line("visibility = ${notification.visibility}")
        line("notificationCount = ${notification.notificationCount}")
        line("eventTime = ${notification.eventTime}")
        line("lightSettings = ${notification.lightSettings?.contentToString()}")
        line("vibrateTimings = ${notification.vibrateTimings?.contentToString()}")
    }

    /** Every key, sorted, with the type of its value, so nothing is left out. */
    @Suppress("DEPRECATION")
    private fun logBundle(bundle: Bundle?) {

        if (bundle == null || bundle.isEmpty) {
            line("extras = (none)")
            return
        }

        line("── extras (${bundle.size()} keys) ──")

        bundle.keySet().sorted().forEach { key ->

            val value = bundle.get(key)

            val text = when (value) {
                null -> "null"
                is ByteArray -> "bytes[${value.size}] ${String(value, Charsets.UTF_8)}"
                is IntArray -> value.contentToString()
                is LongArray -> value.contentToString()
                is Array<*> -> value.contentToString()
                is Bundle -> "Bundle(${value.keySet().sorted().joinToString { "$it=${value.get(it)}" }})"
                else -> value.toString()
            }

            val type = value?.javaClass?.simpleName ?: "null"

            line("$key ($type) = $text")
        }
    }

    private fun priorityName(priority: Int): String = when (priority) {
        RemoteMessage.PRIORITY_HIGH -> "HIGH ($priority)"
        RemoteMessage.PRIORITY_NORMAL -> "NORMAL ($priority)"
        else -> "UNKNOWN ($priority)"
    }

    private fun formatTime(millis: Long): String =
        if (millis <= 0L) {
            "not sent"
        } else {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US).format(Date(millis))
        }

    /** One log line, split when longer than Android allows. */
    private fun line(text: String) {

        if (text.length <= CHUNK) {
            Log.d(TAG, text)
            return
        }

        text.chunked(CHUNK).forEachIndexed { index, part ->
            Log.d(TAG, "  [part ${index + 1}] $part")
        }
    }

    /** Logging must never break a push, so any failure here is only written down. */
    private inline fun safely(what: String, block: () -> Unit) {

        if (!BuildConfig.DEBUG) return

        try {
            block()
        } catch (e: Exception) {
            Log.e(TAG, "Could not log the $what push", e)
        }
    }
}
