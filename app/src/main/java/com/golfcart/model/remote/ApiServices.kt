package com.golfcart.model.remote

import com.golfcart.model.subscription_param.SubscriptionParam
import com.golfcart.model.address.AddressSearchModel
import com.golfcart.model.common.CommonModelResponse
import com.golfcart.model.favorite.FavoriteResponse
import com.golfcart.model.golf_course.GolfCourseModel
import com.golfcart.model.golf_course_sub.GolfCouseSubModel
import com.golfcart.model.home.CategoryResponse
import com.golfcart.model.notification.NotificationResponse
import com.golfcart.model.profile.ProfileResponse
import com.golfcart.model.promo_param.PromoParam
import com.golfcart.model.property.FindPropertyModel
import com.golfcart.model.restaurant_shopping_default.RestaurantDefaultResponse
import com.golfcart.model.search.AddressPlaceSearchModel
import com.golfcart.model.search.SearchCategoriesModel
import com.golfcart.model.signup.SignUpResponse
import com.golfcart.model.weather.WeatherResponse
import com.peachtreecity.model.yelp_model.YelpApiResponse
import io.reactivex.Single
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiServices {

    @FormUrlEncoded
    @POST("signup")
    fun signup( @Field("device_id") device_id: String?,
                @Field("device_token") deviceToken: String?) : Single<SignUpResponse>

    @GET("testCategories")
    fun getCategories(): Single<CategoryResponse>

    @FormUrlEncoded
    @POST("profile")
    fun getProfile(@Field("device_id") device_id: String?,
                   @Field("device_type") deviceToken: String?): Single<ProfileResponse>

    @GET
    fun getWeatherInfo(@Url url: String?): Single<WeatherResponse>

    @GET("favoriteList/{user_id}")
    fun getFavorites(@Path("user_id") uid: Int): Single<FavoriteResponse>

    @Headers( "Authorization: Bearer ze4a0ATk3f1265QYXHlveyqKvzHOfsj7ki5vr1o91EcWYKE4CQ3trP0-X1SaNlOtpaMAGQ6Z0tbXgKPKbT1CPFVr1NMsE60mHzUfRoffDHos_YRgtn_1GvBhpPyhW3Yx" )
    @GET
    fun getRestaurantApi(@Url url: String?): Single<YelpApiResponse>

    @FormUrlEncoded
    @POST("getShopping")
    fun getRestaurantAndShoppingDefault(
        @Field("latitude") latitude: Double,
        @Field("longitude") longtude: Double,
        @Field("category_id") category_id: Int?
    ): Single<RestaurantDefaultResponse>

    @GET("subCategories/{category_id}")
    fun getGolfCourseCategory(@Path("category_id") category_id: Int?): Single<GolfCourseModel>

    @FormUrlEncoded
    @POST("categorySearch")
    fun getGolfCourseSubCategory(
        @Field("category_id") category_id: Int?,
        @Field("sub_cat_id") sub_cat_id: Int?,
        @Field("user_id") user_id: Int?,
        @Field("latitude") latitude: Double?,
        @Field("longitude") longtude: Double?
    ): Single<GolfCouseSubModel>

    @GET
    fun findProperty(@Url url: String?): Single<FindPropertyModel>

    //===============================================================================recentSearchApi
    /*done*/
    @GET("recentSearch/{user_id}/{search_type}")
    fun recentSearch(
        @Path("user_id") user_id: String?,
        @Path("search_type") search_type: String?
    ): Single<AddressPlaceSearchModel>

    @FormUrlEncoded
    @POST("search")
    fun searchCategory(@Field("search_type") search_type: String?): Single<SearchCategoriesModel>

    @GET
    fun searchAddress(@Url url: String?): Single<AddressSearchModel>

    @GET
    fun searchPlace(@Url url: String?): Single<AddressSearchModel>

    @FormUrlEncoded
    @POST("addSearch")
    fun addRecentAddress(@FieldMap map : HashMap<String,String?>): Single<JSONObject>

    @FormUrlEncoded
    @POST("notificationOnOff")
    fun updateNotificationToogle(
        @Field("user_id") user_id: Int,
        @Field("status")  status: Int
    ): Single<JSONObject>

    @FormUrlEncoded
    @POST("notificationList")
    fun notificationList(@Field("user_id") user_id: Int): Single<NotificationResponse>

    @FormUrlEncoded
    @POST("badgeClear")
    fun badgeClear(@Field("user_id") user_id: Int):  Single<NotificationResponse>

    @FormUrlEncoded
    @POST("clearNotification")
    fun clearAllNotification(@Field("user_id") user_id: Int): Single<NotificationResponse>

    //==============================================================================NotificationList
    @FormUrlEncoded
    @POST("deleteNotification")
    fun deleteNotification(
        @Field("user_id") user_id: Int,
        @Field("notification_id") notification_id: Int
    ): Single<NotificationResponse>

    @FormUrlEncoded
    @POST("checkfavouriteStatus")
    fun checkFavoriteStatus(
        @Field("user_id") user_id: String?,
        @Field("yelp_id") yelp_id: String?,
        @Field("type")    type: String?
    ): Single<CommonModelResponse>

    @FormUrlEncoded
    @POST("favorite")
    fun addFavorite(
       @FieldMap map: HashMap<String, String?>
    ): Single<CommonModelResponse>

    @FormUrlEncoded
    @POST("updateEventName")
    fun updateName(
        @Field("user_id") user_id: Int,
        @Field("event_id") event_id: Int,
        @Field("title")    title: String?
    ): Single<CommonModelResponse>

    @FormUrlEncoded
    @POST("favoriteOrder")
    fun updateFavOrder(
        @Field("user_id") user_id: Int,
        @Field("event_id") event_id: String?,
        @Field("order") order: String?
    ): Single<CommonModelResponse>


    @POST("android/verify-receipt")
    fun verifyRecipt(@Body param: SubscriptionParam?): Single<CommonModelResponse>

    @POST("verify-coupon")
    fun checkCoupounCode(@Body param: PromoParam?): Single<CommonModelResponse>
}