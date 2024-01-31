package com.golfcart.model.remote

import com.golfcart.BuildConfig


class ApiConstant {

    companion object {
        /*********API BASE URL************/
        private const val BASE_URL = BuildConfig.BASE_URL
        /*      private const val BASE_URL = "https://www.google.com"*/
        const val API_TIME_OUT: Long = 10000

        fun getBaseURL():String{
            return BASE_URL
        }

    }

}