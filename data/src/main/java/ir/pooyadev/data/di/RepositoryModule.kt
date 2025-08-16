package ir.pooyadev.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.pooyadev.data.repository.local.TaskRepositoryImpl
import ir.pooyadev.data.repository.local.UserPreferencesRepositoryImpl
import ir.pooyadev.domain.repository.local.TaskRepository
import ir.pooyadev.domain.repository.local.UserPreferencesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

}