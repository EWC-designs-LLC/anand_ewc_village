package com.golfcart.ui.address_place_search

import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.FragmentAddressPlaceSearchBinding
import com.golfcart.databinding.ItemSearchBinding
import com.golfcart.model.address.AddressSearchModel
import com.golfcart.model.app_interface.PermissionListener
import com.golfcart.model.detail_data.DetailDataModel
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.detail.FragmentDetail
import com.golfcart.ui.point_to_point.FragmentPointToPoint
import com.golfcart.ui.search.FragmentSearch
import com.golfcart.ui.village.VillageActivity
import com.golfcart.ui.village.VillageActivity.Companion.point_des_address
import com.golfcart.ui.village.VillageActivity.Companion.point_source_address
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.EMPTY
import com.golfcart.utils.village_constant.VillageConstants.MAPBOX_URL
import com.golfcart.utils.village_constant.VillageConstants.SCREE_NAME
import com.golfcart.utils.village_constant.VillageConstants.STADIAMAP_KEY
import com.golfcart.utils.village_constant.VillageConstants.STADIA_MAP_URL
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_LAT_START
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_LNG_START
import com.mapbox.mapboxsdk.geometry.LatLng
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class FragmentAddressPlaceSearch :
    VillageFragment<FragmentAddressPlaceSearchBinding, AddressPlaceViewModel>() {

    private lateinit var screenName: String
    var addressAdapter: RecyclerViewGenericAdapter<AddressSearchModel.Feature, ItemSearchBinding>? = null
    var addressList = ArrayList<AddressSearchModel.Feature>()
    val ADDRESS_TEXT = "address"
    val PLACE_TEXT = "place"
    var isFromPoint = false
    var pointType = "1"
    var oneTime = true
    var latLng: LatLng? = null

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentAddressPlaceSearch
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, getString(R.string.search),
            false, -1, false, "", false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.address_place_search
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_address_place_search
    }

    override fun getViewModel(): AddressPlaceViewModel {
        val vm: AddressPlaceViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            getBundleData()
            setAdapter()
            editTextListiner()
            fetchLocation()
        }
    }

    /*
    * This method is used to fetch the location of the user
    * */
    fun fetchLocation() {
        locationPermission(object : PermissionListener {
            override fun onAccepted(lat: Any, lng: Any) {
                if (oneTime && lat != null) {
                    oneTime = false
                    latLng = LatLng(lat as Double, lng as Double)
                    getAddressGeocoder()
                }
            }
        })
    }

    /*
    * Fetching the Address and pincode details of the user
    * */
    private fun getAddressGeocoder() {
        try {
            val latitude = latLng!!.latitude
            val longitude = latLng!!.longitude
            val geocoder = Geocoder(getContainerActivity(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses!!.isEmpty()) {
                val address = addresses[0].getAddressLine(0)
                val city = addresses[0].locality
                val state = addresses[0].adminArea
                val country = addresses[0].countryName
                val postalCode = addresses[0].postalCode
                val knownName = addresses[0].featureName
                if (address != null) {
                    if (screenName.equals(ADDRESS_TEXT)) {
                        getAddressSearch(address)
                    } else {
                        getPlaceSearch(address)
                    }
                }
            } else {
                showErrorDialog("Location not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorDialog("Something went wrong")
        }
    }


    /*
    * On text change listner for filtering the response
    * applying search using the api
    * */
    private fun editTextListiner() {
        getViewDataBinding()!!.apply {
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().isEmpty()) {
                        getAddressGeocoder()
                    } else {
                        if (screenName.equals(ADDRESS_TEXT)) {
                            getAddressSearch(s.toString())
                        } else {
                            getPlaceSearch(s.toString())
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    /*
    *  fetching the data from the bundle which is pasisng from the previous screen
    * */
    private fun getBundleData() {
        try {
            screenName = requireArguments().getString(SCREE_NAME, "")!!
            isFromPoint = requireArguments().getBoolean(VillageConstants.IS_FROM_POINT_TO_POINT, false)
            pointType = requireArguments().getString(VillageConstants.POINT_TYPE,"1")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
    * Search Api for fetching the searched response
    * we are using the stadia map api for search
    * */
    private fun getAddressSearch(value: String) {
        val url = STADIA_MAP_URL +
                "text=" + value +
                "&focus.point.lat=" + latLng?.getLatitude() +
                "&focus.point.lon=" + latLng?.getLongitude() +
                "&boundary.country=" + "USA" +
                "&boundary.circle.lat=" + "28.90100436297728" +
                "&boundary.circle.lon=" + "-81.9809079394887" +
                "&boundary.circle.radius=" + "50" +
                "&size=5" +
                "&api_key=" + STADIAMAP_KEY

        getViewModel().getAddressData(getContainerActivity(), url)
        getViewModel().addressLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateAddress(it.data)
                    removeObserver(getViewModel().addressMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    /*
    * adding the data in the list
    * updating the adapter
    * */
    private fun updateAddress(response: AddressSearchModel?) {
        getViewDataBinding()!!.apply {
            if (!response?.features.isNullOrEmpty()) {
                addressList.clear()
                addressList.addAll(response?.features!!)
                addressAdapter!!.updateAdapterList(addressList)
            } else {
                addressList.clear()
                addressAdapter!!.updateAdapterList(addressList)
            }
        }
    }

    /*
     * Search Api for fetching the searched response
     * we are using the Mapbox map api for search
     * */
    private fun getPlaceSearch(value: String) {
        val url = "${MAPBOX_URL}${value}.json" +
                "?access_token=${getString(R.string.mapbox_access_token)}" +
                "&cachebuster=1617905472549" +
                "&autocomplete=true" +
                "&country=us" +
                "&worldview=us" +
                "&proximity=-82.0122324,28.9593506"

        getViewModel().getPlaceSearch(getContainerActivity(), url)
        getViewModel().placeLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateAddress(it.data)
                    removeObserver(getViewModel().placeMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    /*
    * Setting the Adapter and showing the listing
    * */
    private fun setAdapter() {
        getViewDataBinding()!!.apply {
            getContainerActivity().runOnUiThread {
                if (addressAdapter == null) {

                    addressAdapter = RecyclerViewGenericAdapter(addressList, R.layout.item_search,
                        object : RecyclerCallback<ItemSearchBinding, AddressSearchModel.Feature> {
                            override fun bindData(
                                binder: ItemSearchBinding,
                                model: AddressSearchModel.Feature,
                                position: Int,
                                itemView: View?
                            ) {
                                binder.apply {

                                    if (screenName.equals(ADDRESS_TEXT)) {
                                        tvTitle.text = model.properties?.label ?: EMPTY
                                    } else {
                                        tvTitle.text = model.placeName
                                    }

                                    itemView?.setOnClickListener {
                                        if (isFromPoint) {
                                           navigateToPoint(model)
                                        } else {
                                            addSearch(model)
                                        }
                                    }

                                }
                            }
                        })
                    rvSearch.adapter = addressAdapter
                }
            }
        }
    }


    /*
    * setting the Lat lng to the variables to access on the previous screen
    * */
    private fun navigateToPoint(model: AddressSearchModel.Feature) {
        if (pointType.equals("1")){
            VillageActivity.point_souce_lat = model.geometry?.coordinates?.get(1) ?: VILLAGE_LAT_START
            VillageActivity.point_souce_lng = model.geometry?.coordinates?.get(0) ?: VILLAGE_LNG_START
            point_source_address =  if (screenName.equals(ADDRESS_TEXT)) model.properties?.name.toString() else model.placeName.toString()
        }else{
            VillageActivity.point_des_lat = model.geometry?.coordinates?.get(1) ?: VILLAGE_LAT_START
            VillageActivity.point_des_lng = model.geometry?.coordinates?.get(0) ?: VILLAGE_LNG_START
            point_des_address = if (screenName.equals(ADDRESS_TEXT)) model.properties?.name.toString() else model.placeName.toString()
        }

        val bundle = Bundle()
        bundle.putBoolean(VillageConstants.IS_POINT_SEARCH,true)
        bundle.putString(VillageConstants.POINT_TYPE,pointType)
        replaceFragment(setArguments(FragmentPointToPoint(),bundle),true,false)
    }

    /*
    * adding the data in the Hashmap
    * hitting the apito save data on for recent search
    * */
    private fun addSearch(model: AddressSearchModel.Feature) {
        val map = HashMap<String, String?>()
        map.put("user_id", getViewModel().getAppRepository().getUserId().toString())
        map.put("address",  if (screenName.equals(ADDRESS_TEXT))  model.properties?.name else model.placeName ?: EMPTY)
        map.put("latitude", (model.geometry?.coordinates?.get(1) ?: VILLAGE_LAT_START).toString())
        map.put("longitude", (model.geometry?.coordinates?.get(0) ?: VILLAGE_LNG_START).toString())
        map.put("google_id", model.properties?.id ?: model.id)
        map.put("place",if (screenName.equals(ADDRESS_TEXT)) model.properties?.label else model.text ?: EMPTY)
        map.put("search_type", getSearchType())

        getViewModel().addSearch(getContainerActivity(), map)
        getViewModel().addSearchLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    navigateToDetail(model)
                    removeObserver(getViewModel().addSeachMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    /*
    *
    * */
    private fun getSearchType(): String {
        if (screenName.equals(ADDRESS_TEXT)) {
            return "1"
        } else {
            return "2"
        }
    }

    private fun navigateToDetail(model: AddressSearchModel.Feature) {
        val bundle = Bundle()
        bundle.putString(VillageConstants.TOOLBAR_TITLE, if (screenName.equals(ADDRESS_TEXT)) model.properties?.label else model.placeName ?: EMPTY)
        bundle.putString(VillageConstants.SNIPPET,if (screenName.equals(ADDRESS_TEXT)) model.properties?.name else model.text ?: EMPTY)
        bundle.putString(VillageConstants.TYPE, VillageConstants.TYPE_SEARCH)
        bundle.putString(VillageConstants.EVENT_ID, model.properties?.id)
        bundle.putString(VillageConstants.DESITNATION_LATITUDE, (model.geometry?.coordinates?.get(1) ?: VILLAGE_LAT_START).toString())
        bundle.putString(VillageConstants.DESITNATION_LONGITUDE, (model.geometry?.coordinates?.get(0) ?: VILLAGE_LNG_START).toString())
        bundle.putString(VillageConstants.DETAIL_MODEL, getViewModel().getReflectionUtils().convertPojoToJson(getDetailModel(model)))
        replaceFragment(setArguments(FragmentDetail(), bundle), true, true)
    }

    private fun getDetailModel(model: AddressSearchModel.Feature): DetailDataModel {
        val detailModel = DetailDataModel(
            if (screenName.equals(ADDRESS_TEXT)) model.properties?.label else model.placeName ?: EMPTY,
            if (screenName.equals(ADDRESS_TEXT)) model.properties?.name else model.text,
            EMPTY, EMPTY , ""
        )
        return detailModel
    }

}