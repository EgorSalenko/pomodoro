package io.esalenko.pomadoro.di

import io.esalenko.pomadoro.repository.TaskRxRepository
import org.koin.dsl.module


val repositoryModule = module {
    single { TaskRxRepository(get()) }
}