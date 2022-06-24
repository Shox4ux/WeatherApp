package com.example.myfragments.core.di.component

import android.app.Application
import com.example.myfragments.core.di.ui.builderModules.ActivityBuilderModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import com.example.myfragments.core.app.App
import com.example.myfragments.core.di.appContext.ContextModule
import com.example.myfragments.core.di.network.client.ClientModule
import com.example.myfragments.core.di.network.retrofit.NetworkModule
import com.example.myfragments.core.di.repo.RepoModule
import com.example.myfragments.core.di.ui.vm.MainVMModule
import dagger.android.AndroidInjectionModule

import javax.inject.Singleton

@Component(
    modules = [
        AndroidInjectionModule::class,
        ClientModule::class,
        NetworkModule::class,
        ActivityBuilderModule::class,
        RepoModule::class,
        ContextModule::class,
    ]
)
@Singleton
interface AppComponent : AndroidInjector<App>{
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}