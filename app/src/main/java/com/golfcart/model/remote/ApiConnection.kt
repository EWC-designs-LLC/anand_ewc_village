package com.golfcart.model.remote

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.golfcart.R
import com.golfcart.utils.Logs.VillageLogs
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.golfcart.utils.connectivity.Connectivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

abstract class ApiConnection<ResultType>(
    val mutableStateFlow: MutableLiveData<ApiResponse<ResultType>>,
    val context: Context
) {

    abstract fun createCallAsync(): Single<ResultType>

    fun execute() {
        if (Connectivity.isConnected(context)) {
            mutableStateFlow.postValue(ApiResponse.loading())
            callNetworkData()
        } else {
            mutableStateFlow.postValue(
                ApiResponse.error(
                    ApiResponse.ApiError(
                        1001,
                        context.getString(R.string.please_check_your_internet_connectivity)
                    )
                )
            )
        }
    }

    fun callNetworkData() {
        CompositeDisposable().addAll(
            createCallAsync().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ResultType>() {
                    override fun onSuccess(t: ResultType) {
                        VillageLogs.printLog(">>>>>Respone", t.toString())
                        mutableStateFlow.postValue(ApiResponse.success(t))
                    }

                    override fun onError(e: Throwable) {
                        errorHandling(e)
                    }

                })
        )
    }

    fun errorHandling(e: Throwable) {
        try {
            val obj = JSONObject((e as HttpException).response().errorBody()!!.string())
            var errorMessage = obj.optString("error")

            if (errorMessage.equals("")) {
                errorMessage = obj.optString("detail")
            }
            if (errorMessage.equals("")) {
                errorMessage = obj.optString("message")
            }

            val errorCode = e.code()
            mutableStateFlow.postValue(
                ApiResponse.error(
                    ApiResponse.ApiError(
                        errorCode,
                        errorMessage,
                        obj.toString()
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            mutableStateFlow.postValue(
                ApiResponse.error(
                    ApiResponse.ApiError(
                        500,
                        "Internal server error"
                    )
                )
            )
        }

    }

}