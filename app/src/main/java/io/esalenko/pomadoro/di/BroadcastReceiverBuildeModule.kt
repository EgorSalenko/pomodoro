package io.esalenko.pomadoro.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.esalenko.pomadoro.receiver.AlarmReceiver

@Module
abstract class BroadcastReceiverBuildeModule {

    @ContributesAndroidInjector
    abstract fun bindAlarmReceiver(): AlarmReceiver
}