package com.golfcart.model.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CategoryResponse : Serializable {
    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("data")
    @Expose
    var data: List<Data>? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("version_status")
    @Expose
    var version_status: Version_status? = null

    @SerializedName("notification_status")
    @Expose
    var notification_status: Int? = null

    inner class Version_status {
        @SerializedName("version_status")
        @Expose
        var version_status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null
    }

    inner class Data : Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("value")
        @Expose
        var value: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("sub_cat_status")
        @Expose
        var sub_cat_status: Int? = null
    }
}