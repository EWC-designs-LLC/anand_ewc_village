package com.golfcart.model.preferences

import android.content.Context
import android.preference.PreferenceManager

class PreferenceHelper(val context: Context) {

    /* Saving the user id in the shared preferneces */
    fun setUserId(key: String, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply()
    }

    /* fetching the user id from the shared preferneces */
    fun getUserId(key: String): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, -1)
    }

    /* Saving the status of the user id in the shared preferneces */
    fun setCustomerLogin(key: String, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply()
    }

    fun isCustomerLogin(key: String): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false)
    }

    fun setIsTermsConditions(key: String, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply()
    }

    fun isTermsConditions(key: String): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false)
    }

    fun setNotificationToogle(key: String, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply()
    }

    fun isNotificationToogle(key: String): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false)
    }

    fun setAppRateCount(key: String, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply()
    }

    fun getAppRateCount(key: String): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0)
    }

    fun setProfileModel(key: String, value: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply()
    }

    fun getProfileModel(key: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key,"")
    }

    fun setNavigationInstruction(key: String, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply()
    }

    fun getNavigationInstruction(key: String): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key,false)
    }
}