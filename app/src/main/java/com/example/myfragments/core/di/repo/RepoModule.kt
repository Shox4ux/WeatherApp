package com.example.myfragments.core.di.repo

import com.example.myweatherapp.core.network.ApiInterface
import dagger.Module
import dagger.Provides
import com.example.myweatherapp.core.repos.WeatherRepo
import javax.inject.Singleton

@Module
open class RepoModule {
    @Provides
    @Singleton
    open fun  provideWeatherRepo(apiInterface: ApiInterface): WeatherRepo {
        return WeatherRepo(apiInterface)
    }
}