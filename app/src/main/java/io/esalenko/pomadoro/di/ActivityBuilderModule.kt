package io.esalenko.pomadoro.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.esalenko.pomadoro.ui.MainActivity
import io.esalenko.pomadoro.ui.SettingsActivity

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributesSplashActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributesSettingsFragment() : SettingsActivity

}