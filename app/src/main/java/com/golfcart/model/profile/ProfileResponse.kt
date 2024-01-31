package com.golfcart.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProfileResponse : Serializable {
    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    inner class Data {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("device_id")
        @Expose
        var deviceId: String? = null

        @SerializedName("device_id_old")
        @Expose
        var deviceIdOld: Any? = null

        @SerializedName("device_type")
        @Expose
        var deviceType: String? = null

        @SerializedName("notification_status")
        @Expose
        var notificationStatus: Int? = null

        @SerializedName("device_token")
        @Expose
        var deviceToken: String? = null

        @SerializedName("account_type")
        @Expose
        var accountType: Int? = null

        @SerializedName("subscription_type")
        @Expose
        var subscriptionType: Int? = null

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

        @SerializedName("current_version")
        @Expose
        var current_version: String? = null

        @SerializedName("unread_notification")
        @Expose
        var unread_notification = 0
    }
}