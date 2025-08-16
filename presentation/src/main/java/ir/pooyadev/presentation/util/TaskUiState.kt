package ir.pooyadev.presentation.util

import ir.pooyadev.domain.model.local.Task

sealed class TaskUiState {
    object Loading : TaskUiState()
    data class Success(val tasks: List<Task>) : TaskUiState()
    data class Error(val message: String) : TaskUiState()
    object Empty : TaskUiState()
}