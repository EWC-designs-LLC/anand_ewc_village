package com.golfcart.model.subscription_param

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SubscriptionParam(
    @field:Expose @field:SerializedName("order_id") var order_id: String,
    @field:Expose @field:SerializedName(
        "package_name"
    ) var package_name: String,
    @field:Expose @field:SerializedName("product_id") var product_id: String,
    @field:Expose @field:SerializedName(
        "purchase_time"
    ) var purchase_time: String,
    @field:Expose @field:SerializedName("purchase_state") var purchase_state: String,
    @field:Expose @field:SerializedName(
        "purchase_token"
    ) var purchase_token: String,
    @field:Expose @field:SerializedName("quantity") var quantity: String,
    @field:Expose @field:SerializedName("auto_renewing") var auto_renewing: String,
    @field:Expose @field:SerializedName(
        "acknowledged"
    ) var acknowledged: String,
    var device_id: String,
    var plan_id: String,
    var platform: String
)