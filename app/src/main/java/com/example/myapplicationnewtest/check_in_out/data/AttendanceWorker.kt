package com.example.myapplicationnewtest.check_in_out.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class AttendanceWorker(
    appContext: Context, params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Log.d("AttendanceWorker", "🚀 Worker started")

        val token = inputData.getString("token") ?: return Result.failure()
        val action = inputData.getString("action") ?: return Result.failure()
        val lat = inputData.getString("lat") ?: "0.0"
        val lng = inputData.getString("lng") ?: "0.0"

        Log.d("AttendanceWorker", "📦 Input data → token=$token, action=$action, lat=$lat, lng=$lng")

        return try {
            val result = sendAttendanceAction(token, action, lat, lng)
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
