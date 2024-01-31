package com.golfcart.model.search

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class SearchCategoriesModel {

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

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("value")
        @Expose
        var value: String? = null

        @SerializedName("service_type")
        @Expose
        var serviceType: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("sub_cat_status")
        @Expose
        var subCatStatus: Int? = null
    }

}