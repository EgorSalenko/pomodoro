package io.esalenko.pomadoro

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import dagger.android.*
import io.esalenko.pomadoro.di.AppInjector
import javax.inject.Inject


class LocalApplication : Application(), HasActivityInjector, HasServiceInjector, HasBroadcastReceiverInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingAndroidServiceInjector: DispatchingAndroidInjector<Service>

    @Inject
    lateinit var dispatchingAndroidBroadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>

    override fun serviceInjector(): AndroidInjector<Service> = dispatchingAndroidServiceInjector

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> =
        dispatchingAndroidBroadcastReceiverInjector

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(application = this@LocalApplication)
    }

}