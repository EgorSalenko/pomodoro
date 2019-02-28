package io.esalenko.pomadoro.di

import dagger.Module
import dagger.Provides
import io.esalenko.pomadoro.domain.LocalRoomDatabase
import io.esalenko.pomadoro.domain.dao.TaskDao
import io.esalenko.pomadoro.repository.TaskRepository
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(localRoomDatabase: LocalRoomDatabase) = TaskRepository(localRoomDatabase.taskDao)

}