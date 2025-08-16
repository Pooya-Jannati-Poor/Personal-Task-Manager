package ir.pooyadev.domain.usecases.local

import ir.pooyadev.domain.model.local.Task
import ir.pooyadev.domain.repository.local.TaskRepository
import javax.inject.Inject

class InsertOrUpdateTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(task: Task): Long {
        return taskRepository.insertOrUpdateTask(task)
    }
}