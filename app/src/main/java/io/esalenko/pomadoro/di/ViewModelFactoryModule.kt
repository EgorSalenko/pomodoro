package io.esalenko.pomadoro.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.esalenko.pomadoro.vm.SharedCountdownViewModel

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SharedCountdownViewModel::class)
    abstract fun bindCountdownModel(viewModelShared: SharedCountdownViewModel): ViewModel
}