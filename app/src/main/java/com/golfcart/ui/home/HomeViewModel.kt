package com.golfcart.ui.home

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.home.CategoryResponse
import com.golfcart.model.profile.ProfileResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.model.weather.WeatherResponse
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.single_live_event.SingleLiveEvent

class HomeViewModel (private val repo: MyRepository) : BaseViewModel() {

    val categoriesMutableLiveData: SingleLiveEvent<ApiResponse<CategoryResponse>> by lazy {
        SingleLiveEvent<ApiResponse<CategoryResponse>>()
    }

    val categoriesliveData: SingleLiveEvent<ApiResponse<CategoryResponse>> = categoriesMutableLiveData

    fun getHomeCategoriesData(containerActivity: VillageActivity) {
        categoriesMutableLiveData.removeObservers(containerActivity)
        repo.getHomeCategories(categoriesMutableLiveData)
    }


    val profileMutableLiveData: SingleLiveEvent<ApiResponse<ProfileResponse>> by lazy {
        SingleLiveEvent<ApiResponse<ProfileResponse>>()
    }

    val profileLiveData: SingleLiveEvent<ApiResponse<ProfileResponse>> = profileMutableLiveData

    fun getProfileData(containerActivity: VillageActivity,deviceId : String) {
        profileMutableLiveData.removeObservers(containerActivity)
        repo.getProfile(deviceId,profileMutableLiveData)
    }

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