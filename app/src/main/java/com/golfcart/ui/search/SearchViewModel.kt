package com.golfcart.ui.search

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.home.CategoryResponse
import com.golfcart.model.profile.ProfileResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.model.search.AddressPlaceSearchModel
import com.golfcart.model.search.SearchCategoriesModel
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.single_live_event.SingleLiveEvent

class SearchViewModel (private val repo: MyRepository) : BaseViewModel() {

    val categoriesMutableLiveData: SingleLiveEvent<ApiResponse<SearchCategoriesModel>> by lazy {
        SingleLiveEvent<ApiResponse<SearchCategoriesModel>>()
    }

    val categoriesliveData: SingleLiveEvent<ApiResponse<SearchCategoriesModel>> = categoriesMutableLiveData

    fun getSearchCategories(containerActivity: VillageActivity,searchType:String) {
        categoriesMutableLiveData.removeObservers(containerActivity)
        repo.searchCategories(categoriesMutableLiveData,searchType)
    }


    val searchMutableLiveData: SingleLiveEvent<ApiResponse<AddressPlaceSearchModel>> by lazy {
        SingleLiveEvent<ApiResponse<AddressPlaceSearchModel>>()
    }

    val searchLiveData: SingleLiveEvent<ApiResponse<AddressPlaceSearchModel>> = searchMutableLiveData

    fun getAddressPlace(containerActivity: VillageActivity,userId: String,searchType:String) {
        searchMutableLiveData.removeObservers(containerActivity)
        repo.recentSearch(searchMutableLiveData,userId,searchType)
    }

    val placeMutableLiveData: SingleLiveEvent<ApiResponse<AddressPlaceSearchModel>> by lazy {
        SingleLiveEvent<ApiResponse<AddressPlaceSearchModel>>()
    }

    val placeLiveData: SingleLiveEvent<ApiResponse<AddressPlaceSearchModel>> = placeMutableLiveData

    fun getPlaceSearch(containerActivity: VillageActivity,userId: String,searchType:String) {
        placeMutableLiveData.removeObservers(containerActivity)
        repo.recentSearch(placeMutableLiveData,userId,searchType)
    }

}