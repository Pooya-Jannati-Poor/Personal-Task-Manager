package ir.pooyadev.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.pooyadev.domain.model.local.SortOrder
import ir.pooyadev.domain.model.local.Task
import ir.pooyadev.domain.model.remote.RemoteResult
import ir.pooyadev.domain.repository.local.UserPreferencesRepository
import ir.pooyadev.domain.usecases.local.DeleteTaskUseCase
import ir.pooyadev.domain.usecases.local.FetchSortedTasksFirstUseCase
import ir.pooyadev.domain.usecases.local.SyncTasksUseCase
import ir.pooyadev.domain.usecases.reminder.CancelTaskAlarmUseCase
import ir.pooyadev.presentation.util.TaskUiState
import ir.pooyadev.presentation.view.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    application: Application,
    private val fetchSortedTasksFirstUseCase: FetchSortedTasksFirstUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val syncTasksUseCase: SyncTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val cancelTaskAlarmUseCase: CancelTaskAlarmUseCase
) : BaseViewModel(application) {

    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Loading)
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private var isDataLoadingStarted = false

    fun startLoadingData() {
        if (isDataLoadingStarted) return
        isDataLoadingStarted = true

        observeLocalTasks()
        syncData()
    }

    private fun observeLocalTasks() {
        viewModelScope.launch {
            fetchSortedTasksFirstUseCase().collect { tasks ->
                if (tasks.isEmpty()) {
                    _uiState.value = TaskUiState.Empty
                } else {
                    _uiState.value = TaskUiState.Success(tasks)
                }
            }
        }
    }

    fun syncData() {
        viewModelScope.launch {
            _uiState.value = TaskUiState.Loading
            when (val result = syncTasksUseCase.invoke()) {
                is RemoteResult.Error -> {
                    _uiState.value = TaskUiState.Error(result.message ?: "An unknown error occurred")
                }
                is RemoteResult.Success -> {
                }
                else -> {
                }
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
            cancelTaskAlarmUseCase(task.id)
        }
    }

    fun changeSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            userPreferencesRepository.updateSortOrder(sortOrder)
        }
    }

}