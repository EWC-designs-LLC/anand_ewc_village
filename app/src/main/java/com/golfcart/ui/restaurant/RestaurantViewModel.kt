package com.golfcart.ui.restaurant

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.model.restaurant_shopping_default.RestaurantDefaultResponse
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.single_live_event.SingleLiveEvent
import com.peachtreecity.model.yelp_model.YelpApiResponse

class RestaurantViewModel (private val repo: MyRepository) : BaseViewModel() {

    val restaurantMutableLiveData: SingleLiveEvent<ApiResponse<YelpApiResponse>> by lazy {
        SingleLiveEvent<ApiResponse<YelpApiResponse>>()
    }

    val restaurantLiveData: SingleLiveEvent<ApiResponse<YelpApiResponse>> = restaurantMutableLiveData

    fun getRestaurantInfo(containerActivity: VillageActivity, restaurant_url: String) {
        restaurantMutableLiveData.removeObservers(containerActivity)
        repo.getRestaurantInfo(restaurantMutableLiveData, restaurant_url)
    }

    val defaultMutableLiveData: SingleLiveEvent<ApiResponse<RestaurantDefaultResponse>> by lazy {
        SingleLiveEvent<ApiResponse<RestaurantDefaultResponse>>()
    }

    val defaultLiveData: SingleLiveEvent<ApiResponse<RestaurantDefaultResponse>> = defaultMutableLiveData

    fun getDefaultRestaurantInfo(containerActivity: VillageActivity, latitude: Double,
                                 longitude: Double,category_id: Int?) {
        restaurantMutableLiveData.removeObservers(containerActivity)
        repo.getRestaurantDefaultInfo(defaultMutableLiveData, latitude,longitude,category_id)
    }


}