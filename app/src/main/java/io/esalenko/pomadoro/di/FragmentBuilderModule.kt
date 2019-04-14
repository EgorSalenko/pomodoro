package io.esalenko.pomadoro.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.esalenko.pomadoro.ui.fragment.TaskFragment
import io.esalenko.pomadoro.ui.fragment.WorkTimerFragment


@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributesWorkTimerFragment(): WorkTimerFragment

    @ContributesAndroidInjector
    abstract fun contributesTaskFragment(): TaskFragment
}