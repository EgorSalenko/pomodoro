package io.esalenko.pomadoro.di

import io.esalenko.pomadoro.repository.TaskRepository
import org.koin.dsl.module


val repositorymodule = module {
    single { TaskRepository(get()) }
}