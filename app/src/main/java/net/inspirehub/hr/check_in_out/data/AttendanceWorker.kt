package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class OfflineAttendanceWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val database = AppDatabase.getDatabase(appContext)
    private val offlineDao = database.offlineLogDao()

    override suspend fun doWork(): Result {

        Log.d(
            "OfflineWorker",
            "🚀 Worker started"
        )

        val token =
            inputData.getString("token")
                ?: return Result.failure()

        return try {

            // 1️⃣ Get all offline logs from Room
            val logs =
                offlineDao.getAllLogs()

            Log.d(
                "OfflineWorker",
                "📦 Found ${logs.size} offline logs to send"
            )

            // Nothing to send
            if (logs.isEmpty()) {

                Log.d(
                    "OfflineWorker",
                    "✅ No offline logs to send"
                )

                return Result.success()
            }

            // 2️⃣ Convert Room objects to API format
            val formattedLogs =
                logs.map { log ->

                    mapOf(
                        "action" to log.action,
                        "lat" to log.lat.toString(),
                        "lng" to log.lng.toString(),
                        "action_time" to log.action_time,
                        "action_tz" to log.action_tz
                    )
                }

            Log.d(
                "OfflineWorker",
                "📤 Sending ${formattedLogs.size} logs..."
            )

            // 3️⃣ Send logs to server
            val success =
                sendOfflineAttendanceAction(
                    context = applicationContext,
                    token = token,
                    logs = formattedLogs
                )

            Log.d(
                "OfflineWorker",
                "📤 Offline endpoint success = $success"
            )

            // 4️⃣ Delete ONLY if server accepted the logs
            if (success) {

                Log.d(
                    "OfflineWorker",
                    "✅ Server accepted offline logs"
                )

                offlineDao.deleteAllLogs()

                Log.d(
                    "OfflineWorker",
                    "🗑 Offline logs deleted"
                )

                Result.success()

            } else {

                Log.e(
                    "OfflineWorker",
                    "❌ Server rejected/failed offline logs"
                )

                Result.retry()
            }

        } catch (e: Exception) {

            Log.e(
                "OfflineWorker",
                "💥 Exception in OfflineWorker: ${e.message}",
                e
            )

            Result.retry()
        }
    }
}