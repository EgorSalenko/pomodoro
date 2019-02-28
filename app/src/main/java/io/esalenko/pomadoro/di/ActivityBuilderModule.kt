package io.esalenko.pomadoro.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.esalenko.pomadoro.ui.MainActivity

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun contributesSplashActivity(): MainActivity

}