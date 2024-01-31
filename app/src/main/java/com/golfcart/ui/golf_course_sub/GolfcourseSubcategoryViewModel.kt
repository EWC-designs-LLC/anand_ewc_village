package com.golfcart.ui.golf_course_sub

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.golf_course.GolfCourseModel
import com.golfcart.model.golf_course_sub.GolfCouseSubModel
import com.golfcart.model.home.CategoryResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.single_live_event.SingleLiveEvent

class GolfcourseSubcategoryViewModel (private val repo: MyRepository) : BaseViewModel()  {

    val golfCourseSubMutableLiveData: SingleLiveEvent<ApiResponse<GolfCouseSubModel>> by lazy {
        SingleLiveEvent<ApiResponse<GolfCouseSubModel>>()
    }

    val golfCourseSubLiveData: SingleLiveEvent<ApiResponse<GolfCouseSubModel>> = golfCourseSubMutableLiveData

    fun getGolfCourseCategories(containerActivity: VillageActivity,category_id: Int?
                                ,sub_category_id: Int?,userId : Int?,latitude: Double , longitude: Double) {
        golfCourseSubMutableLiveData.removeObservers(containerActivity)
        repo.getGolfCourseSubCategory(golfCourseSubMutableLiveData,category_id,sub_category_id,userId,latitude,longitude)
    }

}