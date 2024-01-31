package com.golfcart.ui.favourite

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.common.CommonModelResponse
import com.golfcart.model.favorite.FavoriteResponse
import com.golfcart.model.home.CategoryResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.single_live_event.SingleLiveEvent

class FavouriteViewModel (private val repo: MyRepository) : BaseViewModel() {

    val favoritesMutableLiveData: SingleLiveEvent<ApiResponse<FavoriteResponse>> by lazy {
        SingleLiveEvent<ApiResponse<FavoriteResponse>>()
    }

    val favouriteLiveData: SingleLiveEvent<ApiResponse<FavoriteResponse>> = favoritesMutableLiveData

    fun getFavourites(containerActivity: VillageActivity,user_id:Int) {
        favoritesMutableLiveData.removeObservers(containerActivity)
        repo.getFavorites(favoritesMutableLiveData,user_id)
    }

    val removeFavMutableLiveData: SingleLiveEvent<ApiResponse<CommonModelResponse>> by lazy {
        SingleLiveEvent<ApiResponse<CommonModelResponse>>()
    }

    val removeLiveData: SingleLiveEvent<ApiResponse<CommonModelResponse>> = removeFavMutableLiveData

    fun addFavourite(containerActivity: VillageActivity,map:HashMap<String,String?>) {
        removeFavMutableLiveData.removeObservers(containerActivity)
        repo.addFavourite(removeFavMutableLiveData,map)
    }

    val updateOrderMutableLiveData: SingleLiveEvent<ApiResponse<CommonModelResponse>> by lazy {
        SingleLiveEvent<ApiResponse<CommonModelResponse>>()
    }

    val updateOrderLiveData: SingleLiveEvent<ApiResponse<CommonModelResponse>> = updateOrderMutableLiveData

    fun updateOrder(containerActivity: VillageActivity,userId:Int,eventId:String,order:String) {
        updateOrderMutableLiveData.removeObservers(containerActivity)
        repo.updateFavOrder(updateOrderMutableLiveData,userId,eventId,order)
    }

    val nameUpdateMutableLiveData: SingleLiveEvent<ApiResponse<CommonModelResponse>> by lazy {
        SingleLiveEvent<ApiResponse<CommonModelResponse>>()
    }

    val nameUpdateLiveData: SingleLiveEvent<ApiResponse<CommonModelResponse>> = nameUpdateMutableLiveData

    fun nameUpdate(containerActivity: VillageActivity,userId:Int,eventId:Int,title:String) {
        nameUpdateMutableLiveData.removeObservers(containerActivity)
        repo.updateName(nameUpdateMutableLiveData,userId,eventId,title)
    }

}