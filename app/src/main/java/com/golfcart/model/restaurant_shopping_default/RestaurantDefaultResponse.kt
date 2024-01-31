package com.golfcart.model.restaurant_shopping_default

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RestaurantDefaultResponse {

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("businesses")
    @Expose
    var businesses: List<Business>? = null

    class Business {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("location")
        @Expose
        var location: Location? = null

        @SerializedName("latitude")
        @Expose
        var latitude: Float? = null

        @SerializedName("longitude")
        @Expose
        var longitude: Float? = null

        @SerializedName("phone")
        @Expose
        var phone: String? = null

        @SerializedName("image_url")
        @Expose
        var imageUrl: Any? = null

        @SerializedName("distance")
        @Expose
        var distance: Float? = null

        @SerializedName("coordinates")
        @Expose
        var coordinates: Coordinates? = null
    }

    class Location {
        @SerializedName("display_address")
        @Expose
        var displayAddress: List<String>? = null
    }

    class Coordinates {
        @SerializedName("latitude")
        @Expose
        var latitude: String? = null

        @SerializedName("longitude")
        @Expose
        var longitude: String? = null
    }

}