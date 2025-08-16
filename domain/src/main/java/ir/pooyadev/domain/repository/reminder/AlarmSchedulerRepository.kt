package ir.pooyadev.domain.repository.reminder

interface AlarmSchedulerRepository {
    fun schedule(timeInMillis: Long, taskId: Long)
    fun cancel(taskId: Long)
}