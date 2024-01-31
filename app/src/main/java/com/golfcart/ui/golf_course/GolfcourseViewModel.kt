package com.golfcart.ui.golf_course

import androidx.lifecycle.MutableLiveData
import com.golfcart.model.golf_course.GolfCourseModel
import com.golfcart.model.home.CategoryResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.repo.MyRepository
import com.golfcart.ui.base.BaseViewModel
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.single_live_event.SingleLiveEvent

class GolfcourseViewModel (private val repo: MyRepository) : BaseViewModel()  {

    val golfCourseMutableLiveData: SingleLiveEvent<ApiResponse<GolfCourseModel>> by lazy {
        SingleLiveEvent<ApiResponse<GolfCourseModel>>()
    }

    val golfCourseliveData: SingleLiveEvent<ApiResponse<GolfCourseModel>> = golfCourseMutableLiveData

    fun getGolfCourseCategories(containerActivity: VillageActivity,category_id:Int?) {
        golfCourseMutableLiveData.removeObservers(containerActivity)
        repo.getGolfCourseCategory(golfCourseMutableLiveData,category_id)
    }

}