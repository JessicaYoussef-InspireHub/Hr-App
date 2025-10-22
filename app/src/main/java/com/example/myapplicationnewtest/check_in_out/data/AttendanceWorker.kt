package com.example.myapplicationnewtest.check_in_out.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AttendanceWorker(
    appContext: Context, params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Log.d("AttendanceWorker", "🚀 Worker started")

        val token = inputData.getString("token") ?: return Result.failure()
        val action = inputData.getString("action") ?: return Result.failure()
        val lat = inputData.getString("lat") ?: "0.0"
        val lng = inputData.getString("lng") ?: "0.0"
        val actionTime = inputData.getString("action_time")
        val diffMinutes = inputData.getString("diff_minutes")?.toLongOrNull() ?: 0L

        Log.d("AttendanceWorker", "📦 Input data → token=$token, action=$action, lat=$lat, lng=$lng, diff=$diffMinutes")

        // Modify the time by adding the difference in minutes
        val adjustedActionTime = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
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
            val result = sendAttendanceAction(token, action, lat, lng, adjustedActionTime)
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

