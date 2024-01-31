package com.golfcart.model.property

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class FindPropertyModel  {

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("data")
    @Expose
    var data: List<Data>? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    class Data{
        @SerializedName("ad_id")
        @Expose
        var id: Int? = null

        @SerializedName("latitude")
        @Expose
        var latitude: String? = null

        @SerializedName("longitude")
        @Expose
        var longitude: String? = null

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("url")
        @Expose
        var url: String? = null

        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null

        @SerializedName("price")
        @Expose
        var price: String? = null

        @SerializedName("address")
        @Expose
        var address: String? = null

        @SerializedName("zip")
        @Expose
        var zip: String? = null

        @SerializedName("state")
        @Expose
        var state: String? = null

        @SerializedName("seller")
        @Expose
        var seller: String? = null

        @SerializedName("for")
        @Expose
        var _for: String? = null
    }

}