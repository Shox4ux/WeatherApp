package com.example.myfragments.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweatherapp.core.models.oneDay.ApiResponse
import com.example.myweatherapp.core.repos.WeatherRepo
import kotlinx.coroutines.launch
import com.example.myfragments.core.util.dataHelper.ResultWrapper
import javax.inject.Inject

class MainVM @Inject constructor(private val weatherRepo: WeatherRepo) : ViewModel() {
    private val errorResponse = MutableLiveData<Any?>()
    val requestLiveData = MutableLiveData<ApiResponse?>()


    fun callCurrentWeatherData(lat: String, lon: String, apiKey: String) {
        viewModelScope.launch {
            when (val response = weatherRepo.getCurrentWeather(lat, lon, apiKey)) {
                is ResultWrapper.Success -> {
                    requestLiveData.postValue(response.value)
                }
                is ResultWrapper.ErrorResponse -> {
                    errorResponse.postValue(response.error)
                }
                is ResultWrapper.NetworkError -> {
                }
            }
        }

    }

    fun callCityWeatherData(cityName: String, apiKey: String) {
        viewModelScope.launch {
            when (val response = weatherRepo.getCityWeather(cityName, apiKey)) {
                is ResultWrapper.Success -> {
                    requestLiveData.postValue(response.value)
                }
                is ResultWrapper.ErrorResponse -> {
                    errorResponse.postValue(response.error)
                }
                is ResultWrapper.NetworkError -> {

                }
            }
        }

    }



}



