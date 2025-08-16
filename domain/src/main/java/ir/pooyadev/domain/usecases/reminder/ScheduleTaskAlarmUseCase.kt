package ir.pooyadev.domain.usecases.reminder

import ir.pooyadev.domain.repository.reminder.AlarmSchedulerRepository
import javax.inject.Inject

class ScheduleTaskAlarmUseCase @Inject constructor(
    private val alarmSchedulerRepository: AlarmSchedulerRepository
) {
    operator fun invoke(timeInMillis: Long, taskId: Long) {
        alarmSchedulerRepository.schedule(timeInMillis, taskId)
    }
}