package ir.pooyadev.data.di.local

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.pooyadev.domain.repository.local.TaskRepository
import ir.pooyadev.domain.repository.local.UserPreferencesRepository
import ir.pooyadev.domain.usecases.local.DeleteTaskUseCase
import ir.pooyadev.domain.usecases.local.FetchTasksByTaskIdUseCase
import ir.pooyadev.domain.usecases.local.FetchSortedTasksFirstUseCase
import ir.pooyadev.domain.usecases.local.InsertOrUpdateTaskUseCase
import ir.pooyadev.domain.usecases.local.SyncTasksUseCase
import ir.pooyadev.domain.usecases.local.UpdateTaskUseCase

@Module
@InstallIn(SingletonComponent::class)
object TaskUseCasesModule {

    @Provides
    fun provideInsertTaskUseCase(taskRepository: TaskRepository): InsertOrUpdateTaskUseCase {
        return InsertOrUpdateTaskUseCase(taskRepository)
    }

    @Provides
    fun provideFetchSortedTasksFirstUseCase(taskRepository: TaskRepository, userPreferencesRepository: UserPreferencesRepository): FetchSortedTasksFirstUseCase {
        return FetchSortedTasksFirstUseCase(taskRepository, userPreferencesRepository)
    }

    @Provides
    fun provideFetchTasksByTaskIdUseCase(taskRepository: TaskRepository): FetchTasksByTaskIdUseCase {
        return FetchTasksByTaskIdUseCase(taskRepository)
    }

    @Provides
    fun provideUpdateTaskUseCase(taskRepository: TaskRepository): UpdateTaskUseCase {
        return UpdateTaskUseCase(taskRepository)
    }

    @Provides
    fun provideDeleteTaskUseCase(taskRepository: TaskRepository): DeleteTaskUseCase {
        return DeleteTaskUseCase(taskRepository)
    }

    @Provides
    fun provideSyncTasksUseCase(taskRepository: TaskRepository): SyncTasksUseCase {
        return SyncTasksUseCase(taskRepository)
    }

}