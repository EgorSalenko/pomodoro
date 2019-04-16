package io.esalenko.pomadoro.di

import io.esalenko.pomadoro.repository.TaskRepository
import org.koin.dsl.module


val repositoryModule = module {
    single { TaskRepository(get()) }
}