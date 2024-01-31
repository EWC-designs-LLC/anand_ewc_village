package com.golfcart.model.favorite

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FavoriteResponse : Serializable {

    @SerializedName("status") @Expose
    val status: Int? = null

    @SerializedName("data") @Expose
    val data: List<Data>? = null

    @SerializedName("message") @Expose
    val message: String? = null

    class Data : Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("category_id")
        @Expose
        var categoryId: Int? = null

        @SerializedName("sub_category_id")
        @Expose
        var subCategoryId: Int? = null

        @SerializedName("address")
        @Expose
        var address: String? = null

        @SerializedName("location")
        @Expose
        var location: String? = null

        @SerializedName("latitude")
        @Expose
        var latitude = 0.0

        @SerializedName("longitude")
        @Expose
        var longitude = 0.0

        @SerializedName("contact_number")
        @Expose
        var contactNumber: String? = null

        @SerializedName("golf_number")
        @Expose
        var golfNumber: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("type")
        @Expose
        var type: String? = null
    }

}