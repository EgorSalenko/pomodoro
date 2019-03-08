package io.esalenko.pomadoro.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.esalenko.pomadoro.service.CountdownService

@Module
abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract fun bindCountdownService(): CountdownService
}