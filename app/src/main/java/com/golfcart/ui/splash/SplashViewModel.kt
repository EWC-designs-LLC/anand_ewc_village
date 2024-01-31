package com.golfcart.ui.splash

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.model.signup.SignUpResponse
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.utils.single_live_event.SingleLiveEvent

class SplashViewModel(private val repo: MyRepository) : BaseViewModel() {

    val mutableLiveData: SingleLiveEvent<ApiResponse<SignUpResponse>> by lazy {
        SingleLiveEvent<ApiResponse<SignUpResponse>>()
    }

    private val liveData: SingleLiveEvent<ApiResponse<SignUpResponse>> = mutableLiveData

    fun getLiveData(): SingleLiveEvent<ApiResponse<SignUpResponse>> {
        return liveData
    }

    fun signup(containerActivity: SplashActivity, deviceUniqueId: String?, fcmToken: String) {
        mutableLiveData.removeObservers(containerActivity)
        repo.signup(mutableLiveData,deviceUniqueId,fcmToken)
    }

}