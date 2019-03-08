package io.esalenko.pomadoro

import android.app.Activity
import android.app.Application
import android.app.Service
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import io.esalenko.pomadoro.di.AppInjector
import javax.inject.Inject


class LocalApplication : Application(), HasActivityInjector, HasServiceInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingAndroidServiceInjector: DispatchingAndroidInjector<Service>

    override fun serviceInjector(): AndroidInjector<Service> = dispatchingAndroidServiceInjector

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector


    override fun onCreate() {
        super.onCreate()
        AppInjector.init(application = this@LocalApplication)
    }

}