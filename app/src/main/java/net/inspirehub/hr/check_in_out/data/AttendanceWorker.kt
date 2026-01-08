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
            val result = if (isSingleRecord) {
                val res = sendAttendanceAction(
                    context = applicationContext,
                    token, action, lat, lng, adjustedActionTime)
                Log.d("AttendanceWorker", "one")
                res
            } else {

                val log = mapOf(
                    "action" to action,
                    "lat" to lat,
                    "lng" to lng,
                    "action_time" to adjustedActionTime,
                    "action_tz" to "UTC"
                )
                sendOfflineAttendanceAction(
                    context = applicationContext,
                    token, listOf(log))

                Log.d("AttendanceWorker", "more")
            }


            if (result != null) {
                Log.d("AttendanceWorker", "✅ Worker send success: $result")
                Result.success()
            } else {
                Log.e("AttendanceWorker", "❌ Worker send failed (result null)")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("AttendanceWorker", "💥 Exception in Worker: ${e.message}", e)
            Result.retry()
        }
    }
}