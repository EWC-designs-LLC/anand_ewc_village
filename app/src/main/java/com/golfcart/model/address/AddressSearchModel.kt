package com.golfcart.model.address

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddressSearchModel {

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("query")
    @Expose
    var query: List<String>? = null

    @SerializedName("features")
    @Expose
    var features: List<Feature>? = null

    @SerializedName("attribution")
    @Expose
    var attribution: String? = null

    class Feature {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("type")
        @Expose
        var type: String? = null

        @SerializedName("place_type")
        @Expose
        var placeType: List<String>? = null

        @SerializedName("relevance")
        @Expose
        var relevance: Double? = null

        @SerializedName("address")
        @Expose
        var address: String? = null

        @SerializedName("text")
        @Expose
        var text: String? = null

        @SerializedName("place_name")
        @Expose
        var placeName: String? = null

        @SerializedName("center")
        @Expose
        var center: List<Double>? = null

        @SerializedName("properties")
        @Expose
        var properties: Properties? = null

        @SerializedName("geometry")
        @Expose
        var geometry: Geometry? = null

        @SerializedName("context")
        @Expose
        var context: List<Context>? = null
    }

    class Properties {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("accuracy")
        @Expose
        var accuracy: String? = null

        @SerializedName("label")
        @Expose
        var label: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null
    }

    class Context {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("text")
        @Expose
        var text: String? = null

        @SerializedName("wikidata")
        @Expose
        var wikidata: String? = null

        @SerializedName("short_code")
        @Expose
        var shortCode: String? = null
    }

    class Geometry {
        @SerializedName("type")
        @Expose
        var type: String? = null

        @SerializedName("coordinates")
        @Expose
        var coordinates: List<Double>? = null

        @SerializedName("interpolated")
        @Expose
        var interpolated: Boolean? = null
    }

}