package io.esalenko.pomadoro

import android.app.Application
import io.esalenko.pomadoro.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class LocalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@LocalApplication)
            modules(appComponent)
        }
    }

}