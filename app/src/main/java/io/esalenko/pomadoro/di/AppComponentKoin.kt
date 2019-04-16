package io.esalenko.pomadoro.di

import org.koin.core.module.Module


val appComponent: List<Module> = listOf(
    appModule,
    viewModelModule,
    persistencemodule,
    repositorymodule
)