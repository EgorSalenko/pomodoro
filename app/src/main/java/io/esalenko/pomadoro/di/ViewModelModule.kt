package io.esalenko.pomadoro.di

import io.esalenko.pomadoro.vm.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { ToDoListViewModel(get(), get(), get()) }
    viewModel { SharedViewModel() }
    viewModel { TimerViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { SharedSettingsViewModel() }
}