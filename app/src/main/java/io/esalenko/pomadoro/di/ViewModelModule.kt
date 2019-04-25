package io.esalenko.pomadoro.di

import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.TimerViewModel
import io.esalenko.pomadoro.vm.ToDoListVIewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { ToDoListVIewModel(get()) }
    viewModel { SharedViewModel() }
    viewModel { TimerViewModel(get()) }
}