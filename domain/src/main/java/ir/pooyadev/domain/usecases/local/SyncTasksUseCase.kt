package ir.pooyadev.domain.usecases.local

import ir.pooyadev.domain.model.remote.RemoteResult
import ir.pooyadev.domain.repository.local.TaskRepository
import javax.inject.Inject

class SyncTasksUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(): RemoteResult<Unit> = taskRepository.syncTasks()
}