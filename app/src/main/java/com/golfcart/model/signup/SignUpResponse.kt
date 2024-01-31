package com.golfcart.model.signup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SignUpResponse {
    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("user_id")
    @Expose
    var user_id: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
}