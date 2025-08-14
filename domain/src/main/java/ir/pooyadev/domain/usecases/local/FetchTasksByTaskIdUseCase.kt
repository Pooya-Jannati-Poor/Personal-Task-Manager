package ir.pooyadev.domain.usecases.local

import ir.pooyadev.domain.model.local.Task
import ir.pooyadev.domain.repository.local.TaskRepository
import javax.inject.Inject

class FetchTasksByTaskIdUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: Long): Task? {
        return taskRepository.fetchTasksByTaskId(taskId)
    }
}