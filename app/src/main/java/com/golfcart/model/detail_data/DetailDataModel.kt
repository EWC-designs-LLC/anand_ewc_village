package com.golfcart.model.detail_data

import java.io.Serializable

data class DetailDataModel(
    var address: String?,
    var location: String?,
    var image: String?,
    var contact_number: String?,
    var golf_number: String?
) : Serializable