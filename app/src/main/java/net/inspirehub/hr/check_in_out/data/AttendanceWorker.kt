package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.work.WorkManager
import androidx.work.WorkInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OfflineAttendanceWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val database = AppDatabase.getDatabase(appContext)
    private val offlineDao = database.offlineLogDao()

    override suspend fun doWork(): Result {
        Log.d("OfflineWorker", "🚀 Worker started")

        val token = inputData.getString("token") ?: return Result.failure()

        try {
            val logs = offlineDao.getAllLogs()
            Log.d("OfflineWorker", "📦 Found ${logs.size} offline logs to send")

            if (logs.isEmpty()) return Result.success()

            val formattedLogs = logs.map { log ->
                mapOf(
                    "action" to log.action,
                    "lat" to log.lat.toString(),
                    "lng" to log.lng.toString(),
                    "action_time" to log.action_time,
                    "action_tz" to log.action_tz
                )
            }

            val sentIds = logs.map { it.id }

            val accepted = withContext(Dispatchers.IO) {
                sendOfflineAttendanceAction(
                    context = applicationContext,
                    token = token,
                    logs = formattedLogs
                )
            }

            if (accepted) {
                Log.d("OfflineWorker", "✅ Successfully sent ${logs.size} logs")
                // Only the uploaded rows — a punch made while this batch was in flight stays queued.
                offlineDao.deleteLogsByIds(sentIds)
                return Result.success()
            } else {
                Log.e("OfflineWorker", "❌ Failed to send logs, keeping them queued and retrying")
                return Result.retry()
            }

        } catch (e: Exception) {
            Log.e("OfflineWorker", "💥 Exception in OfflineWorker: ${e.message}", e)
            return Result.retry()
        }
    }
}



/**
 * No longer enqueued: offline punches now go to the offline_logs table and are drained by
 * [OfflineAttendanceWorker], so there is exactly one delivery path per punch.
 *
 * The class is kept because WorkManager persists enqueued jobs across app updates — devices
 * that upgrade with a pending attendance_tag job still need this worker to deliver it.
 * Do not enqueue new work here; that reintroduces double-sending.
 */
class AttendanceWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Log.d("AttendanceWorker", "🚀 Worker started")

        val token = inputData.getString("token") ?: return Result.failure()
        val action = inputData.getString("action") ?: return Result.failure()
        val lat = inputData.getString("lat") ?: "0.0"
        val lng = inputData.getString("lng") ?: "0.0"
        val actionTime = inputData.getString("action_time")
        val diffMinutes = inputData.getString("diff_minutes")?.toLongOrNull() ?: 0L

        // ✅ Check how many attendance records are queued in WorkManager
        val workManager = WorkManager.getInstance(applicationContext)
        val allWorkInfos = workManager.getWorkInfosByTag("attendance_tag").get()

        // Count active works excluding this current Worker
        val activeWorksExcludingCurrent = allWorkInfos.filter { workInfo ->
            (workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING) &&
                    workInfo.id != this.id
        }

        val isSingleRecord = activeWorksExcludingCurrent.isEmpty()

        Log.d(
            "AttendanceWorker",
            if (isSingleRecord) "📦 Only one record to process"
            else "📦 More than one record queued (${activeWorksExcludingCurrent.size + 1})"
        )

        Log.d("AttendanceWorker", "📦 Input data → token=$token, action=$action, lat=$lat, lng=$lng, diff=$diffMinutes")

        // 🕒 Adjust time by adding difference in minutes
        val adjustedActionTime = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = sdf.parse(actionTime ?: sdf.format(Date()))!!
            val calendar = Calendar.getInstance().apply {
                time = date
                add(Calendar.MINUTE, diffMinutes.toInt())
            }
            sdf.format(calendar.time)
        } catch (e: Exception) {
            Log.e("AttendanceWorker", "❌ Time modification error: ${e.message}")
            actionTime ?: SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        }

        Log.d("AttendanceWorker", "🕒 Adjusted action time after diff: $adjustedActionTime")

        return try {
            // Must be a Boolean: both senders return a non-null value even when they fail,
            // so a null check here would report every send as a success.
            val sent: Boolean = if (isSingleRecord) {
                Log.d("AttendanceWorker", "one")
                val res = sendAttendanceAction(
                    context = applicationContext,
                    token, action, lat, lng, adjustedActionTime)
                res != null && !res.status.equals("Error", ignoreCase = true)
            } else {
                Log.d("AttendanceWorker", "more")
                val log = mapOf(
                    "action" to action,
                    "lat" to lat,
                    "lng" to lng,
                    "action_time" to adjustedActionTime,
                    "action_tz" to "UTC"
                )
                withContext(Dispatchers.IO) {
                    sendOfflineAttendanceAction(
                        context = applicationContext,
                        token, listOf(log))
                }
            }


            if (sent) {
                Log.d("AttendanceWorker", "✅ Worker send success")
                Result.success()
            } else {
                Log.e("AttendanceWorker", "❌ Worker send failed, will retry")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("AttendanceWorker", "💥 Exception in Worker: ${e.message}", e)
            Result.retry()
        }
    }
}