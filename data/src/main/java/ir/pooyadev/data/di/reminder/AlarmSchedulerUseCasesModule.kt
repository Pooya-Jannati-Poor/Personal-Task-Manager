package ir.pooyadev.data.di.reminder

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.pooyadev.domain.repository.reminder.AlarmSchedulerRepository
import ir.pooyadev.domain.usecases.reminder.CancelTaskAlarmUseCase
import ir.pooyadev.domain.usecases.reminder.ScheduleTaskAlarmUseCase

@Module
@InstallIn(SingletonComponent::class)
object AlarmSchedulerUseCasesModule {

    @Provides
    fun provideCancelTaskAlarmUseCase(alarmSchedulerRepository: AlarmSchedulerRepository): CancelTaskAlarmUseCase {
        return CancelTaskAlarmUseCase(alarmSchedulerRepository)
    }

    @Provides
    fun provideScheduleTaskAlarmUseCase(alarmSchedulerRepository: AlarmSchedulerRepository): ScheduleTaskAlarmUseCase {
        return ScheduleTaskAlarmUseCase(alarmSchedulerRepository)
    }

}