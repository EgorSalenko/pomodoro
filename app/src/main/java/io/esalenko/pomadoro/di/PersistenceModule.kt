package io.esalenko.pomadoro.di

import io.esalenko.pomadoro.db.LocalRoomDatabase
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val persistenceModule = module {
    single { LocalRoomDatabase.getInstance(androidContext()) }
    single { SharedPreferenceManager(get()) }
    single { LocalRoomDatabase.getInstance(androidContext()).taskDao }
}