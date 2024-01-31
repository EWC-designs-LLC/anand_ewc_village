package com.peachtreecity.model.yelp_model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class YelpApiResponse : Serializable {
    @SerializedName("businesses")
    @Expose
    var businesses: List<Business>? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("total")
    @Expose
    var total: Int? = null

    class Business : Serializable {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("image_url")
        @Expose
        var image_url: String? = null

        @SerializedName("url")
        @Expose
        var url: String? = null

        @SerializedName("coordinates")
        @Expose
        var coordinates: Coordinates? = null

        @SerializedName("location")
        @Expose
        var location: Location? = null

        @SerializedName("phone")
        @Expose
        var phone: String? = null

        @SerializedName("display_phone")
        @Expose
        var display_phone: String? = null

        @SerializedName("distance")
        @Expose
        var distance: Double? = null
    }

    class Coordinates : Serializable {
        @SerializedName("latitude")
        @Expose
        var latitude: Float? = null

        @SerializedName("longitude")
        @Expose
        var longitude: Float? = null
    }

    class Location : Serializable {

        @SerializedName("address1")
        @Expose
        var address1:String?=null

        @SerializedName("city")
        @Expose
        var city:String?=null

        @SerializedName("display_address")
        @Expose
        var display_address: List<String>? = null
    }

    companion object {
        val instance = YelpApiResponse()
    }
}