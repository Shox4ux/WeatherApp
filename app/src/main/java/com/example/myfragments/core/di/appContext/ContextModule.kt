package com.example.myfragments.core.di.appContext

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface ContextModule {

    @Binds
    @Singleton
    fun context(application: Application?): Context?

}