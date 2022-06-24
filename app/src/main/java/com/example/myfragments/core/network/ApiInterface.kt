package com.example.myweatherapp.core.network

import com.example.myweatherapp.core.models.oneDay.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}

// https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}

interface ApiInterface {

    @GET("weather")
   suspend fun getCurrentWeatherData(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") apiKey: String
    ): Response<ApiResponse?>

    @GET("weather")
    suspend fun getCityWeatherData(
        @Query("q") cityName:String,
        @Query("appid") apiKey:String
    ): Response<ApiResponse?>


}