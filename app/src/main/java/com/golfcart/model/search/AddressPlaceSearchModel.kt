package com.golfcart.model.search

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class AddressPlaceSearchModel {

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
        @SerializedName("address")
        @Expose
        var address: String? = null

        @SerializedName("place")
        @Expose
        var place: String? = null

        @SerializedName("latitude")
        @Expose
        var latitude: String? = null

        @SerializedName("longitude")
        @Expose
        var longitude: String? = null

        @SerializedName("google_id")
        @Expose
        var googleId: String? = null

        @SerializedName("search_type")
        @Expose
        var searchType: Int? = null
    }


}