package com.golfcart.ui.detail

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.common.CommonModelResponse
import com.golfcart.model.home.CategoryResponse
import com.golfcart.model.profile.ProfileResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.single_live_event.SingleLiveEvent

class DetailViewModel (private val repo: MyRepository) : BaseViewModel() {

    val categoriesMutableLiveData: SingleLiveEvent<ApiResponse<CategoryResponse>> by lazy {
        SingleLiveEvent<ApiResponse<CategoryResponse>>()
    }

    val categoriesliveData: SingleLiveEvent<ApiResponse<CategoryResponse>> = categoriesMutableLiveData

    fun getHomeCategoriesData(containerActivity: VillageActivity) {
        categoriesMutableLiveData.removeObservers(containerActivity)
        repo.getHomeCategories(categoriesMutableLiveData)
    }

    val checkStatusMutableLiveData: SingleLiveEvent<ApiResponse<CommonModelResponse>> by lazy {
        SingleLiveEvent<ApiResponse<CommonModelResponse>>()
    }

    val checkStatusLiveData: SingleLiveEvent<ApiResponse<CommonModelResponse>> = checkStatusMutableLiveData

    fun checkFavStatus(containerActivity: VillageActivity,user_id: String?, yelp_id: String?, type: String?) {
        checkStatusMutableLiveData.removeObservers(containerActivity)
        repo.checkStatus(checkStatusMutableLiveData,user_id,yelp_id,type)
    }

    val addFavMutableLiveData: SingleLiveEvent<ApiResponse<CommonModelResponse>> by lazy {
        SingleLiveEvent<ApiResponse<CommonModelResponse>>()
    }

    val addFavLiveData: SingleLiveEvent<ApiResponse<CommonModelResponse>> = addFavMutableLiveData

    fun addFavourite(containerActivity: VillageActivity,map:HashMap<String,String?>) {
        addFavMutableLiveData.removeObservers(containerActivity)
        repo.addFavourite(addFavMutableLiveData,map)
    }
}