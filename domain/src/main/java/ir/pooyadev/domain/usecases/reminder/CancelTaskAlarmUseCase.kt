package ir.pooyadev.domain.usecases.reminder

import ir.pooyadev.domain.repository.reminder.AlarmSchedulerRepository
import javax.inject.Inject

class CancelTaskAlarmUseCase @Inject constructor(
    private val alarmSchedulerRepository: AlarmSchedulerRepository
) {
    operator fun invoke(taskId: Long) {
        alarmSchedulerRepository.cancel(taskId)
    }
}