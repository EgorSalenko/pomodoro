package io.esalenko.pomadoro.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import io.esalenko.pomadoro.manager.LocalNotificationManager
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application) : Context = application.applicationContext

    @Singleton
    @Provides
    fun provideSharedPreference(ctx: Context): SharedPreferences {
        return ctx.getSharedPreferences(SharedPreferenceManager.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providesLocalNotificationManager(ctx: Context): LocalNotificationManager {
        return LocalNotificationManager(ctx)
    }

}