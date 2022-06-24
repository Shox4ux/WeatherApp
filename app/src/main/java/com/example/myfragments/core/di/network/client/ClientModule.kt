package com.example.myfragments.core.di.network.client

import com.example.myweatherapp.core.network.ApiInterface
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
open class ClientModule {
    @Provides
    @Singleton
    open fun provideLoginService(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }


}