package io.esalenko.pomadoro.di

import io.esalenko.pomadoro.vm.SharedCountdownViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { SharedCountdownViewModel() }
}