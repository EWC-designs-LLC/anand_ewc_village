package com.golfcart.model.golf_course_sub

import android.provider.ContactsContract.CommonDataKinds.Phone
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GolfCouseSubModel {

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("data")
    @Expose
    var data: List<Data>? = null

    class Data {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("location")
        @Expose
        var location: String? = null

        @SerializedName("address")
        @Expose
        var address: String? = null

        @SerializedName("latitude")
        @Expose
        var latitude: Float? = null

        @SerializedName("longitude")
        @Expose
        var longitude: Float? = null

        @SerializedName("contact_number")
        @Expose
        var contactNumber: String? = null

        @SerializedName("golf_number")
        @Expose
        var golfNumber: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("favorite_status")
        @Expose
        var favoriteStatus: Int? = null

        @SerializedName("phone")
        @Expose
        var phone: List<Phone>? = null
    }

    class Phone {
        @SerializedName("contact_number")
        @Expose
        var contactNumber: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null
    }

}