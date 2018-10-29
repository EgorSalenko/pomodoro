package io.esalenko.pomadoro.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.esalenko.pomadoro.ui.CountdownFragment


@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributesCountdownFragment(): CountdownFragment

}