package com.golfcart.model.notification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationResponse {
    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("data")
    @Expose
    var data: List<Data>? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    inner class Data {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("sender_id")
        @Expose
        var sender_id: String? = null

        @SerializedName("receiver_id")
        @Expose
        var receiver_id: String? = null

        @SerializedName("label")
        @Expose
        var label: String? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("read_status")
        @Expose
        var read_status: Int? = null

        @SerializedName("updated_at")
        @Expose
        var updated_at: String? = null

        @SerializedName("created_at")
        @Expose
        var created_at: String? = null
    }
}