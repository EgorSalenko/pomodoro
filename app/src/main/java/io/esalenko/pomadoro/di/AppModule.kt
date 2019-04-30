package io.esalenko.pomadoro.di

import android.content.Context
import io.esalenko.pomadoro.manager.LocalAlarmManager
import io.esalenko.pomadoro.manager.LocalNotificationManager
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {
    single {
        androidContext().getSharedPreferences(
            SharedPreferenceManager.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
    }
    single { LocalNotificationManager() }
    single { LocalAlarmManager(get()) }
}