package com.ashes.dev.works.system.core.internals.antar.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

class BatteryLogWorker(
    context: Context,
    params: WorkerParameters,
    private val batteryRepository: BatteryRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // No battery state to read right now: try again later, within the same retry cap.
            if (!batteryRepository.logCurrentBattery()) return retryOrFail()

            // Purge logs older than 30 days
            val thirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
            batteryRepository.deleteLogsOlderThan(thirtyDaysAgo)

            Result.success()
        } catch (e: CancellationException) {
            throw e // WorkManager stopped us — that's not a failure to retry
        } catch (e: Exception) {
            // A persistent error (disk full, corrupt DB) must not retry forever every period.
            if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
        }
    }

    private fun retryOrFail(): Result =
        if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()

    private companion object {
        const val MAX_RETRIES = 3
    }
}
