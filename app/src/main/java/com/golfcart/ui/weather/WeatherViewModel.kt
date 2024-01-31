package com.golfcart.ui.weather

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.model.weather.WeatherResponse
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.single_live_event.SingleLiveEvent

class WeatherViewModel (private val repo: MyRepository) : BaseViewModel() {


    val weatherMutableLiveData: SingleLiveEvent<ApiResponse<WeatherResponse>> by lazy {
        SingleLiveEvent<ApiResponse<WeatherResponse>>()
    }

    val weatherLiveData: SingleLiveEvent<ApiResponse<WeatherResponse>> = weatherMutableLiveData

    fun getWeatherInfo(
        containerActivity: VillageActivity,url: String
    ) {
        weatherMutableLiveData.removeObservers(containerActivity)
        repo.getWeatherInfo(weatherMutableLiveData,url)
    }



}