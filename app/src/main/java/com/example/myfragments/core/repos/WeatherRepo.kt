package com.example.myweatherapp.core.repos

import com.example.myweatherapp.core.models.oneDay.ApiResponse
import com.example.myweatherapp.core.network.ApiInterface
import com.example.myfragments.core.util.dataHelper.ResultWrapper
import com.example.myfragments.core.util.dataHelper.parseResponse

class WeatherRepo (private val apiInterface: ApiInterface) {

    suspend fun getCurrentWeather(lat:String, lon :String,apiKey:String) : ResultWrapper<ApiResponse?, Any?> {
        return parseResponse {
            apiInterface.getCurrentWeatherData(lat,lon,apiKey)
        }
    }

    suspend fun getCityWeather(cityName:String,apiKey:String) : ResultWrapper<ApiResponse?, Any?> {
        return parseResponse {
            apiInterface.getCityWeatherData(cityName,apiKey)
        }
    }



}