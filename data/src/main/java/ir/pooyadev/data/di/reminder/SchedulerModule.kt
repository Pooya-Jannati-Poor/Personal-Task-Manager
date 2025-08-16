package ir.pooyadev.data.di.reminder

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.pooyadev.data.repository.reminder.AlarmSchedulerRepositoryImpl
import ir.pooyadev.domain.repository.reminder.AlarmSchedulerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SchedulerModule {

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmSchedulerRepository {
        return AlarmSchedulerRepositoryImpl(context)
    }

}