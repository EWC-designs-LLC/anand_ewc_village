package com.golfcart.ui.more

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.home.CategoryResponse
import com.golfcart.model.profile.ProfileResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import org.json.JSONObject

class MoreViewModel (private val repo: MyRepository) : BaseViewModel() {

    val updateNotificationMutableLiveData: MutableLiveData<ApiResponse<JSONObject>> by lazy {
        MutableLiveData<ApiResponse<JSONObject>>()
    }

    val updateNotificationLiveData: MutableLiveData<ApiResponse<JSONObject>> = updateNotificationMutableLiveData

    fun updateNotification(containerActivity: VillageActivity,user_id: Int, status: Int) {
        updateNotificationMutableLiveData.removeObservers(containerActivity)
        repo.updateNotificationToogle(updateNotificationMutableLiveData,user_id,status)
    }

}