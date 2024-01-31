package com.golfcart.di

import com.golfcart.BuildConfig
import com.golfcart.model.preferences.PreferenceHelper
import com.golfcart.model.remote.ApiConstant
import com.golfcart.model.remote.ApiServices
import com.golfcart.model.remote.ReflectionUtil
import com.golfcart.utils.app_configuration.AppConfiguration.DEVICE_TYPE
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

var appModule = module {

    /** PROVIDE GSON SINGLETON */
    single<Gson> {
        val builder = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        builder.setLenient().create()
    }

    /** Provide Preference Helper singleton Object */
    single {
        PreferenceHelper(androidContext())
    }

    /** PROVIDE RETROFIT SINGLETON */
    single {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        // initliase the okhttpclient
        val httpClient = OkHttpClient.Builder()
        // configuring the api time out
        httpClient.addInterceptor(loggingInterceptor)
        httpClient.connectTimeout(ApiConstant.API_TIME_OUT, TimeUnit.SECONDS)
        httpClient.readTimeout(ApiConstant.API_TIME_OUT, TimeUnit.SECONDS)
        httpClient.writeTimeout(ApiConstant.API_TIME_OUT, TimeUnit.SECONDS)
        httpClient.addInterceptor { chain ->
            val request = chain.request().newBuilder().build()
            chain.proceed(request)
        }

        // passing the api headers
        httpClient.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val original: Request = chain.request()
                val requestBuilder: Request.Builder = original.newBuilder()
                    .addHeader("version", BuildConfig.VERSION_NAME)
                    .addHeader("device-type", DEVICE_TYPE)

                val request: Request = requestBuilder.build()
                return chain.proceed(request)
            }
        })

        // initialising the httpclient
        val okHttpClient = httpClient.build()

        // initialising the retrofit builder
        Retrofit.Builder()
            .baseUrl(ApiConstant.getBaseURL())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(get() as Gson))
            .build()
    }

    /*** Provide API Service Singleton*/
    single {
        (get<Retrofit>()).create<ApiServices>(ApiServices::class.java)
    }

    /**Provide ReflectionUtil class Singleton object
     * you can use it any KoinComponent class  below is declaration
     *  private val reflectionUtil: ReflectionUtil by inject()*/
    single { ReflectionUtil(get()) }

}