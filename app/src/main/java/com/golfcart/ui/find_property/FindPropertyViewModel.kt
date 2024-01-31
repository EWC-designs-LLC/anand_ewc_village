package com.golfcart.ui.find_property

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.home.CategoryResponse
import com.golfcart.model.profile.ProfileResponse
import com.golfcart.model.property.FindPropertyModel
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.single_live_event.SingleLiveEvent
import com.golfcart.utils.village_constant.VillageConstants.PROPERTY_URL

class FindPropertyViewModel (private val repo: MyRepository) : BaseViewModel() {

    val propertyMutableLiveData: SingleLiveEvent<ApiResponse<FindPropertyModel>> by lazy {
        SingleLiveEvent<ApiResponse<FindPropertyModel>>()
    }

    val propertyliveData: SingleLiveEvent<ApiResponse<FindPropertyModel>> = propertyMutableLiveData

    fun findProperty(containerActivity: VillageActivity) {
        propertyMutableLiveData.removeObservers(containerActivity)
        repo.findProperty(propertyMutableLiveData,PROPERTY_URL)
    }

}