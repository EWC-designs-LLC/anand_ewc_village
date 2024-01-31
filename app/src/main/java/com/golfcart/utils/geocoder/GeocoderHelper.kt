package com.golfcart.utils.geocoder

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_PINCODE_1
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_PINCODE_10
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_PINCODE_2
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_PINCODE_3
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_PINCODE_4
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_PINCODE_5
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_PINCODE_6
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_PINCODE_7
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_PINCODE_9
import com.mapbox.mapboxsdk.geometry.LatLng
import java.util.Locale

object GeocoderHelper {
    fun getPostalCodeFromLatLng(context: Context, latLng: LatLng?): Boolean {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: MutableList<Address>? =
                geocoder.getFromLocation(latLng!!.latitude, latLng!!.longitude, 1)
            if (addresses!!.isNotEmpty()) {
                val postalCode = addresses[0].postalCode

                if (postalCode.equals(VILLAGE_PINCODE_1) ||
                    postalCode.equals(VILLAGE_PINCODE_2) ||
                    postalCode.equals(VILLAGE_PINCODE_3) ||
                    postalCode.equals(VILLAGE_PINCODE_4) ||
                    postalCode.equals(VILLAGE_PINCODE_5) ||
                    postalCode.equals(VILLAGE_PINCODE_6) ||
                    postalCode.equals(VILLAGE_PINCODE_7) ||
                    postalCode.equals(VILLAGE_PINCODE_9) ||
                    postalCode.equals(VILLAGE_PINCODE_10)
                ) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

}