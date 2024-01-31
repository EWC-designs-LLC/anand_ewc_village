package com.golfcart.model.repo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.golfcart.model.subscription_param.SubscriptionParam
import com.golfcart.model.address.AddressSearchModel
import com.golfcart.model.common.CommonModelResponse
import com.golfcart.model.favorite.FavoriteResponse
import com.golfcart.model.golf_course.GolfCourseModel
import com.golfcart.model.golf_course_sub.GolfCouseSubModel
import com.golfcart.model.home.CategoryResponse
import com.golfcart.model.notification.NotificationResponse
import com.golfcart.model.preferences.PreferenceHelper
import com.golfcart.model.profile.ProfileResponse
import com.golfcart.model.promo_param.PromoParam
import com.golfcart.model.property.FindPropertyModel
import com.golfcart.model.remote.ApiConnection
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.remote.ApiServices
import com.golfcart.model.restaurant_shopping_default.RestaurantDefaultResponse
import com.golfcart.model.search.AddressPlaceSearchModel
import com.golfcart.model.search.SearchCategoriesModel
import com.golfcart.model.signup.SignUpResponse
import com.golfcart.model.weather.WeatherResponse
import com.golfcart.utils.app_configuration.AppConfiguration.DEVICE_TYPE
import com.peachtreecity.model.yelp_model.YelpApiResponse
import io.reactivex.Single
import org.json.JSONObject
import org.koin.core.component.KoinComponent

class MyRepository(
    val apiservice: ApiServices,
    val preferences: PreferenceHelper,
    val androidContext: Context
) :
    KoinComponent {

    fun signup(
        mutableLiveData: MutableLiveData<ApiResponse<SignUpResponse>>,
        deviceUniqueId: String?,
        fcmToken: String,
    ) {
        object : ApiConnection<SignUpResponse>(mutableLiveData, androidContext) {
            override fun createCallAsync(): Single<SignUpResponse> {
                return apiservice.signup(deviceUniqueId, fcmToken)
            }
        }.execute()
    }

    fun getHomeCategories(mutableLiveData: MutableLiveData<ApiResponse<CategoryResponse>>) {
        object : ApiConnection<CategoryResponse>(mutableLiveData, androidContext) {
            override fun createCallAsync(): Single<CategoryResponse> {
                return apiservice.getCategories()
            }
        }.execute()
    }

    fun getProfile(
        deviceId: String?,
        mutableLiveData: MutableLiveData<ApiResponse<ProfileResponse>>
    ) {
        object : ApiConnection<ProfileResponse>(mutableLiveData, androidContext) {
            override fun createCallAsync(): Single<ProfileResponse> {
                return apiservice.getProfile(deviceId, DEVICE_TYPE)
            }
        }.execute()
    }

    fun getWeatherInfo(
        mutableLiveData: MutableLiveData<ApiResponse<WeatherResponse>>,url: String
    ) {
        object : ApiConnection<WeatherResponse>(mutableLiveData, androidContext) {
            override fun createCallAsync(): Single<WeatherResponse> {
                return apiservice.getWeatherInfo(url)
            }
        }.execute()
    }

    fun getFavorites(
        mutableLiveData: MutableLiveData<ApiResponse<FavoriteResponse>>,userId: Int
    ) {
        object : ApiConnection<FavoriteResponse>(mutableLiveData, androidContext) {
            override fun createCallAsync(): Single<FavoriteResponse> {
                return apiservice.getFavorites(userId)
            }
        }.execute()
    }

    fun getRestaurantInfo(mutableLiveData: MutableLiveData<ApiResponse<YelpApiResponse>>, restaurant_url: String){
        object : ApiConnection<YelpApiResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<YelpApiResponse> {
                return apiservice.getRestaurantApi(restaurant_url)
            }
        }.execute()
    }

    fun getRestaurantDefaultInfo(mutableLiveData: MutableLiveData<ApiResponse<RestaurantDefaultResponse>>, latitude: Double,
                                 longitude: Double,category_id: Int?){
        object : ApiConnection<RestaurantDefaultResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<RestaurantDefaultResponse> {
                return apiservice.getRestaurantAndShoppingDefault(latitude,longitude,category_id)
            }
        }.execute()
    }

    fun getGolfCourseCategory(mutableLiveData: MutableLiveData<ApiResponse<GolfCourseModel>>, category_id: Int?){
        object : ApiConnection<GolfCourseModel>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<GolfCourseModel> {
                return apiservice.getGolfCourseCategory(category_id)
            }
        }.execute()
    }

    fun getGolfCourseSubCategory(mutableLiveData: MutableLiveData<ApiResponse<GolfCouseSubModel>>, category_id: Int?
    ,sub_category_id: Int?,userId : Int?,latitude: Double , longitude: Double){
        object : ApiConnection<GolfCouseSubModel>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<GolfCouseSubModel> {
                return apiservice.getGolfCourseSubCategory(category_id,sub_category_id,userId,latitude,longitude)
            }
        }.execute()
    }

    fun findProperty(mutableLiveData: MutableLiveData<ApiResponse<FindPropertyModel>>, url:String){
        object : ApiConnection<FindPropertyModel>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<FindPropertyModel> {
                return apiservice.findProperty(url)
            }
        }.execute()
    }

    fun recentSearch(mutableLiveData: MutableLiveData<ApiResponse<AddressPlaceSearchModel>>,userId: String,searchType:String){
        object : ApiConnection<AddressPlaceSearchModel>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<AddressPlaceSearchModel> {
                return apiservice.recentSearch(userId,searchType)
            }
        }.execute()
    }

    fun searchCategories(mutableLiveData: MutableLiveData<ApiResponse<SearchCategoriesModel>>,  searchType:String){
        object : ApiConnection<SearchCategoriesModel>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<SearchCategoriesModel> {
                return apiservice.searchCategory(searchType)
            }
        }.execute()
    }

    fun searchAddress(mutableLiveData: MutableLiveData<ApiResponse<AddressSearchModel>>, url:String){
        object : ApiConnection<AddressSearchModel>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<AddressSearchModel> {
                return apiservice.searchAddress(url)
            }
        }.execute()
    }

    fun addSearch(mutableLiveData: MutableLiveData<ApiResponse<JSONObject>>, map : HashMap<String,String?>){
        object : ApiConnection<JSONObject>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<JSONObject> {
                return apiservice.addRecentAddress(map)
            }
        }.execute()
    }

    fun placeSearch(mutableLiveData: MutableLiveData<ApiResponse<AddressSearchModel>>,url: String){
        object : ApiConnection<AddressSearchModel>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<AddressSearchModel> {
                return apiservice.searchPlace(url)
            }
        }.execute()
    }

    fun updateNotificationToogle(mutableLiveData: MutableLiveData<ApiResponse<JSONObject>>,user_id: Int, status: Int){
        object : ApiConnection<JSONObject>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<JSONObject> {
                return apiservice.updateNotificationToogle(user_id,status)
            }
        }.execute()
    }

    fun notificationList(mutableLiveData: MutableLiveData<ApiResponse<NotificationResponse>>,user_id: Int){
        object : ApiConnection<NotificationResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<NotificationResponse> {
                return apiservice.notificationList(user_id)
            }
        }.execute()
    }

    fun badgeClear(mutableLiveData: MutableLiveData<ApiResponse<NotificationResponse>>,user_id: Int){
        object : ApiConnection<NotificationResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<NotificationResponse> {
                return apiservice.badgeClear(user_id)
            }
        }.execute()
    }

    fun clearAllNotification(mutableLiveData: MutableLiveData<ApiResponse<NotificationResponse>>,user_id: Int){
        object : ApiConnection<NotificationResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<NotificationResponse> {
                return apiservice.clearAllNotification(user_id)
            }
        }.execute()
    }

    fun deleteNotification(mutableLiveData: MutableLiveData<ApiResponse<NotificationResponse>>,user_id: Int,notification_id: Int){
        object : ApiConnection<NotificationResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<NotificationResponse> {
                return apiservice.deleteNotification(user_id,notification_id)
            }
        }.execute()
    }

    fun checkStatus(mutableLiveData: MutableLiveData<ApiResponse<CommonModelResponse>>, user_id: String?, yelp_id: String?, type: String?){
        object : ApiConnection<CommonModelResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<CommonModelResponse> {
                return apiservice.checkFavoriteStatus(user_id,yelp_id,type)
            }
        }.execute()
    }

    fun addFavourite(mutableLiveData: MutableLiveData<ApiResponse<CommonModelResponse>>,map: HashMap<String, String?>){
        object : ApiConnection<CommonModelResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<CommonModelResponse> {
                return apiservice.addFavorite(map)
            }
        }.execute()
    }

    fun updateName(mutableLiveData: MutableLiveData<ApiResponse<CommonModelResponse>>,userId:Int,eventId:Int,title:String){
        object : ApiConnection<CommonModelResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<CommonModelResponse> {
                return apiservice.updateName(userId,eventId,title)
            }
        }.execute()
    }

    fun updateFavOrder(mutableLiveData: MutableLiveData<ApiResponse<CommonModelResponse>>,userId:Int,eventId:String,order:String){
        object : ApiConnection<CommonModelResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<CommonModelResponse> {
                return apiservice.updateFavOrder(userId,eventId,order)
            }
        }.execute()
    }

    fun verifyReceipt(mutableLiveData: MutableLiveData<ApiResponse<CommonModelResponse>>,param: SubscriptionParam?){
        object : ApiConnection<CommonModelResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<CommonModelResponse> {
                return apiservice.verifyRecipt(param)
            }
        }.execute()
    }

    fun checkCoupoun(mutableLiveData: MutableLiveData<ApiResponse<CommonModelResponse>>,param: PromoParam?){
        object : ApiConnection<CommonModelResponse>(mutableLiveData,androidContext){
            override fun createCallAsync(): Single<CommonModelResponse> {
                return apiservice.checkCoupounCode(param)
            }
        }.execute()
    }
}