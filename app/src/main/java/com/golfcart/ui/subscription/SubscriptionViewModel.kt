package com.golfcart.ui.subscription

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.subscription_param.SubscriptionParam
import com.golfcart.model.common.CommonModelResponse
import com.golfcart.model.promo_param.PromoParam
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.ui.base.BaseViewModel

class SubscriptionViewModel(private val repo: MyRepository) : BaseViewModel() {

    val verifyReciptMutableLiveData: MutableLiveData<ApiResponse<CommonModelResponse>> by lazy {
        MutableLiveData<ApiResponse<CommonModelResponse>>()
    }

    val verifyliveData: MutableLiveData<ApiResponse<CommonModelResponse>> = verifyReciptMutableLiveData

    fun verifyReceipt(containerActivity: SubscriptionActivity,param: SubscriptionParam?) {
        verifyReciptMutableLiveData.removeObservers(containerActivity)
        repo.verifyReceipt(verifyReciptMutableLiveData,param)
    }

    val checkCoupounMutableLiveData: MutableLiveData<ApiResponse<CommonModelResponse>> by lazy {
        MutableLiveData<ApiResponse<CommonModelResponse>>()
    }

    val checkCoupounliveData: MutableLiveData<ApiResponse<CommonModelResponse>> = checkCoupounMutableLiveData

    fun checkCoupoun(containerActivity: SubscriptionActivity,param: PromoParam?) {
        checkCoupounMutableLiveData.removeObservers(containerActivity)
        repo.checkCoupoun(checkCoupounMutableLiveData,param)
    }


}