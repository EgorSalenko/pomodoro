package io.esalenko.pomadoro.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import io.esalenko.pomadoro.domain.LocalRoomDatabase
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import javax.inject.Singleton

@Module
class PersistenceModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(ctx: Context): LocalRoomDatabase = LocalRoomDatabase.getInstance(ctx)

    @Provides
    @Singleton
    fun provideSharedPreferenceManager(sharedPreferences : SharedPreferences) = SharedPreferenceManager(sharedPreferences)
}