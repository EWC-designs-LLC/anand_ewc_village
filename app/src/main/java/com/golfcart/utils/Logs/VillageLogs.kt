package com.golfcart.utils.Logs

import android.util.Log
import com.golfcart.BuildConfig

class VillageLogs {
    companion object{

        val ERROR="error"

        fun printLog(key:String,value:String?){
            if (BuildConfig.ENABLE_LOGGING && BuildConfig.DEBUG) {
                Log.e(key,value!!)
            }
        }
    }
}