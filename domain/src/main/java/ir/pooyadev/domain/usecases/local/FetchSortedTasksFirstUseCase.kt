package ir.pooyadev.domain.usecases.local

import ir.pooyadev.domain.model.local.SortOrder
import ir.pooyadev.domain.model.local.Task
import ir.pooyadev.domain.repository.local.TaskRepository
import ir.pooyadev.domain.repository.local.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class FetchSortedTasksFirstUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Task>> = userPreferencesRepository.sortOrder
        .flatMapLatest { sortOrder ->
            when (sortOrder) {
                SortOrder.BY_NEWEST -> taskRepository.fetchTasksSortByNewestFirst()
                SortOrder.BY_OLDEST -> taskRepository.fetchTasksSortByOldestFirst()
            }
        }
}