package com.golfcart.ui.address_place_search

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.address.AddressSearchModel
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.single_live_event.SingleLiveEvent
import org.json.JSONObject

class AddressPlaceViewModel (private val repo: MyRepository) : BaseViewModel() {

    val addressMutableLiveData: SingleLiveEvent<ApiResponse<AddressSearchModel>> by lazy {
        SingleLiveEvent<ApiResponse<AddressSearchModel>>()
    }

    val addressLiveData: SingleLiveEvent<ApiResponse<AddressSearchModel>> = addressMutableLiveData

    fun getAddressData(containerActivity: VillageActivity,url:String) {
        addressMutableLiveData.removeObservers(containerActivity)
        repo.searchAddress(addressMutableLiveData,url)
    }

    /*=============================================Place========================================================*/

    val placeMutableLiveData: SingleLiveEvent<ApiResponse<AddressSearchModel>> by lazy {
        SingleLiveEvent<ApiResponse<AddressSearchModel>>()
    }

    val placeLiveData: SingleLiveEvent<ApiResponse<AddressSearchModel>> = placeMutableLiveData

    fun getPlaceSearch(containerActivity: VillageActivity, url: String) {
        placeMutableLiveData.removeObservers(containerActivity)
        repo.placeSearch(placeMutableLiveData,url)
    }


    /*===========================================add search==============================================*/

    val addSeachMutableLiveData: SingleLiveEvent<ApiResponse<JSONObject>> by lazy {
        SingleLiveEvent<ApiResponse<JSONObject>>()
    }

    val addSearchLiveData: SingleLiveEvent<ApiResponse<JSONObject>> = addSeachMutableLiveData

    fun addSearch(containerActivity: VillageActivity, map : HashMap<String,String?>) {
        addSeachMutableLiveData.removeObservers(containerActivity)
        repo.addSearch(addSeachMutableLiveData,map)
    }



}