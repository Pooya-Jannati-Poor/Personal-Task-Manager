package ir.pooyadev.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ir.pooyadev.domain.model.remote.RemoteResult
import ir.pooyadev.domain.usecases.local.SyncTasksUseCase

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncTasksUseCase: SyncTasksUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return when (syncTasksUseCase()) {
            is RemoteResult.Success -> {
                Result.success()
            }

            is RemoteResult.Error -> {
                Result.retry()
            }

            else -> {
                Result.failure()
            }
        }
    }
}