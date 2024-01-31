package com.golfcart.ui.notification

import com.golfcart.model.notification.NotificationResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.single_live_event.SingleLiveEvent

class NotificationViewModel (private val repo: MyRepository) : BaseViewModel() {

    val notificationMutableLiveData: SingleLiveEvent<ApiResponse<NotificationResponse>> by lazy {
        SingleLiveEvent<ApiResponse<NotificationResponse>>()
    }

    val notificationliveData: SingleLiveEvent<ApiResponse<NotificationResponse>> = notificationMutableLiveData

    fun getNotification(containerActivity: VillageActivity,userId:Int) {
        notificationMutableLiveData.removeObservers(containerActivity)
        repo.notificationList(notificationMutableLiveData,userId)
    }

    val badgeMutableLiveData: SingleLiveEvent<ApiResponse<NotificationResponse>> by lazy {
        SingleLiveEvent<ApiResponse<NotificationResponse>>()
    }

    val badgeLiveData: SingleLiveEvent<ApiResponse<NotificationResponse>> = badgeMutableLiveData

    fun badgeClear(containerActivity: VillageActivity,userId:Int) {
        badgeMutableLiveData.removeObservers(containerActivity)
        repo.badgeClear(badgeMutableLiveData,userId)
    }

    val clearMutableLiveData: SingleLiveEvent<ApiResponse<NotificationResponse>> by lazy {
        SingleLiveEvent<ApiResponse<NotificationResponse>>()
    }

    val clearLiveData: SingleLiveEvent<ApiResponse<NotificationResponse>> = clearMutableLiveData

    fun clearAllNotification(containerActivity: VillageActivity,userId:Int) {
        clearMutableLiveData.removeObservers(containerActivity)
        repo.clearAllNotification(clearMutableLiveData,userId)
    }

    val deleteMutableLiveData: SingleLiveEvent<ApiResponse<NotificationResponse>> by lazy {
        SingleLiveEvent<ApiResponse<NotificationResponse>>()
    }

    val deleteLiveData: SingleLiveEvent<ApiResponse<NotificationResponse>> = deleteMutableLiveData

    fun deleteNotification(containerActivity: VillageActivity,userId:Int,notificationId:Int) {
        deleteMutableLiveData.removeObservers(containerActivity)
        repo.deleteNotification(deleteMutableLiveData,userId,notificationId)
    }

}