package com.golfcart.model.common

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonModelResponse {

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
}