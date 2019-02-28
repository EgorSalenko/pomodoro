package io.esalenko.pomadoro.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.esalenko.pomadoro.vm.CountdownViewModel
import io.esalenko.pomadoro.vm.SharedViewModel

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SharedViewModel::class)
    abstract fun bindSharedViewModel(viewModel: SharedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CountdownViewModel::class)
    abstract fun bindCountdownModel(viewModel : CountdownViewModel) : ViewModel
}