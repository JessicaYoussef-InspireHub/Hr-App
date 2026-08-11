package net.inspirehub.hr.check_in_out.data

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import net.inspirehub.hr.SharedPrefManager

class OfflineLocationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        Log.d("TEST_LOCATION_WORKER", "Worker started")

        val sharedPref = SharedPrefManager(applicationContext)

        if (!sharedPref.getIsTracked()) {
            Log.d("TEST_LOCATION_WORKER", "Tracking disabled")
            return Result.success()
        }

        if (!NetworkUtils.hasRealInternet()) {
            Log.d("TEST_LOCATION_WORKER", "No real internet")
            return Result.retry()
        }

        return try {

            sendOfflineLocations(applicationContext)

            Log.d("TEST_LOCATION_WORKER", "Offline locations synced successfully")

            Result.success()

        } catch (e: Exception) {

            Log.e("TEST_LOCATION_WORKER", "Sync failed: ${e.message}", e)

            Result.retry()
        }
    }
}


object LocationWorkScheduler {

    fun enqueueOfflineLocationSync(context: Context) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request =
            OneTimeWorkRequestBuilder<OfflineLocationWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "offline_location_sync",
                ExistingWorkPolicy.KEEP,
                request
            )

        Log.d("TEST_LOCATION_WORKER", "Offline location worker enqueued")
    }
}