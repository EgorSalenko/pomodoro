package io.esalenko.pomadoro.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import io.esalenko.pomadoro.LocalApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        AppModule::class,
        PersistenceModule::class,
        RepositoryModule::class,
        ActivityBuilderModule::class,
        ViewModelFactoryModule::class,
        ServiceBuilderModule::class,
        FragmentBuilderModule::class,
        BroadcastReceiverBuildeModule::class
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(application: LocalApplication)

}