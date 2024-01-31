package com.golfcart.model.repo

import android.content.Context
import com.golfcart.model.preferences.PreferenceConstants.Companion.IS_CUSTOMER_LOGIN
import com.golfcart.model.preferences.PreferenceConstants.Companion.TERMS_AND_CONDTIONS
import com.golfcart.model.preferences.PreferenceConstants.Companion.USER_ID
import com.golfcart.model.preferences.PreferenceHelper
import com.golfcart.utils.village_constant.VillageConstants.APP_RATE_COUNT
import com.golfcart.utils.village_constant.VillageConstants.HOME_PROFILE_MODEL
import com.golfcart.utils.village_constant.VillageConstants.IS_NOTIFICATION
import com.golfcart.utils.village_constant.VillageConstants.NAVIGATION_INSTRUCTION

class AppRepository(val preferences: PreferenceHelper, val androidContext: Context) {

    fun setUserId(value:Int){
        preferences.setUserId(USER_ID,value)
    }

    fun getUserId():Int{
        return preferences.getUserId(USER_ID)
    }

    fun setCustomerLogin(value:Boolean){
        preferences.setCustomerLogin(IS_CUSTOMER_LOGIN,value)
    }

    fun isCustomerLogin():Boolean{
        return preferences.isCustomerLogin(IS_CUSTOMER_LOGIN)
    }

    fun setIsTermsConditions(value:Boolean){
        preferences.setCustomerLogin(TERMS_AND_CONDTIONS,value)
    }

    fun isTermsConditions():Boolean{
        return preferences.isCustomerLogin(TERMS_AND_CONDTIONS)
    }

    fun setNotificationToogle(value:Boolean){
        preferences.setNotificationToogle(IS_NOTIFICATION,value)
    }

    fun isNotificationToogle():Boolean{
        return preferences.isNotificationToogle(IS_NOTIFICATION)
    }

    fun setAppRateCount(value:Int){
        preferences.setAppRateCount(APP_RATE_COUNT,value)
    }

    fun getAppRateCount():Int{
        return preferences.getAppRateCount(APP_RATE_COUNT)
    }

    fun setProfileModel(value:String){
        preferences.setProfileModel(HOME_PROFILE_MODEL,value)
    }

    fun getProfileModel(): String? {
        return preferences.getProfileModel(HOME_PROFILE_MODEL)
    }

    fun setNavigationInstruction(value:Boolean){
        preferences.setNavigationInstruction(NAVIGATION_INSTRUCTION,value)
    }

    fun getNavigationInstruction(): Boolean {
        return preferences.getNavigationInstruction(NAVIGATION_INSTRUCTION)
    }

}