package com.golfcart.model.golf_course

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GolfCourseModel {

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("data")
    @Expose
    var data: List<Data>? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    class Data {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("category_id")
        @Expose
        var categoryId: Int? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("value")
        @Expose
        var value: String? = null
    }

}