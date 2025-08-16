package ir.pooyadev.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.pooyadev.domain.model.local.Task
import ir.pooyadev.domain.usecases.local.FetchTasksByTaskIdUseCase
import ir.pooyadev.domain.usecases.local.InsertOrUpdateTaskUseCase
import ir.pooyadev.domain.usecases.local.UpdateTaskUseCase
import ir.pooyadev.domain.usecases.reminder.CancelTaskAlarmUseCase
import ir.pooyadev.domain.usecases.reminder.ScheduleTaskAlarmUseCase
import ir.pooyadev.presentation.view.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskFormFragmentViewModel @Inject constructor(
    application: Application,
    private val fetchTasksByTaskIdUseCase: FetchTasksByTaskIdUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val insertOrUpdateTaskUseCase: InsertOrUpdateTaskUseCase,
    private val scheduleTaskAlarmUseCase: ScheduleTaskAlarmUseCase,
    private val cancelTaskAlarmUseCase: CancelTaskAlarmUseCase,
) : BaseViewModel(application) {

    val taskLiveData: LiveData<Task>
        get() = _taskLiveData
    private val _taskLiveData: MutableLiveData<Task> =
        MutableLiveData()

    val isTaskSaved: LiveData<Boolean>
        get() = _isTaskSaved
    private val _isTaskSaved: MutableLiveData<Boolean> =
        MutableLiveData()

    fun fetchTaskByTaskId(taskId: Long) {
        viewModelScope.launch {
            _taskLiveData.value = fetchTasksByTaskIdUseCase.invoke(taskId)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase.invoke(task)
            cancelPreviousAlarm(task.id)
            setReminderAlarm(task, task.id)
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            val newTaskId = insertOrUpdateTaskUseCase.invoke(task)
            setReminderAlarm(task, newTaskId)
        }
    }

    private fun setReminderAlarm(task: Task, newTaskId: Long) {
        viewModelScope.launch {
            if (task.taskAlarmDate != null) {
                scheduleTaskAlarmUseCase.invoke(task.taskAlarmDate!!, newTaskId)
            }
            _isTaskSaved.value = true
        }
    }

    private fun cancelPreviousAlarm(taskId: Long) {
        viewModelScope.launch {
            cancelTaskAlarmUseCase.invoke(taskId)
        }
    }

}