package com.golfcart.ui.restaurant

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.FragmentRestaurantBinding
import com.golfcart.databinding.ItemRestaurantBinding
import com.golfcart.model.app_interface.EventsInterface
import com.golfcart.model.app_interface.PermissionListener
import com.golfcart.model.detail_data.DetailDataModel
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.restaurant_shopping_default.RestaurantDefaultResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.detail.FragmentDetail
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.pagination.PaginationListener
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.loadGlide
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants.CATEGORY_ID
import com.golfcart.utils.village_constant.VillageConstants.DESITNATION_LATITUDE
import com.golfcart.utils.village_constant.VillageConstants.DESITNATION_LONGITUDE
import com.golfcart.utils.village_constant.VillageConstants.DETAIL_MODEL
import com.golfcart.utils.village_constant.VillageConstants.EMPTY
import com.golfcart.utils.village_constant.VillageConstants.EVENT_ID
import com.golfcart.utils.village_constant.VillageConstants.SNIPPET
import com.golfcart.utils.village_constant.VillageConstants.TOOLBAR_TITLE
import com.golfcart.utils.village_constant.VillageConstants.TYPE
import com.golfcart.utils.village_constant.VillageConstants.TYPE_YELP
import com.golfcart.utils.village_constant.VillageConstants.YELP_SEARCH_BASE_URL
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.peachtreecity.model.yelp_model.YelpApiResponse
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Collections

class FragmentRestaurant : VillageFragment<FragmentRestaurantBinding, RestaurantViewModel>(),
    OnMapReadyCallback, EventsInterface {

    private var safteyCount: Int=0
    private var category_id: Int? = -1
    private val LIMIT = 50
    private var OFFSET = 0
    var restaurantAdapter: RecyclerViewGenericAdapter<YelpApiResponse.Business, ItemRestaurantBinding>? =
        null
    var restaurantList = ArrayList<YelpApiResponse.Business>()
    var title: String? = null

    var pageNo = 1
    var isLastPage = true
    var hasNext = false
    var mapbox: MapboxMap? = null
    var boundsBuilder = LatLngBounds.Builder()

    override fun showBottomBar(): Boolean? {
        return false
    }

    override fun getCurrentFragment(): Fragment? {
        return this@FragmentRestaurant
    }

    override fun showToolbar(): Boolean? {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration? {
        return ToolbarConfiguration(
            true, true, requireArguments().getString(TOOLBAR_TITLE, ""), false,
            -1, false, "", true, R.drawable.ic_box_location
        )
    }

    override fun getBindingVariable(): Int {
        return BR.restaurant
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_restaurant
    }

    override fun getViewModel(): RestaurantViewModel {
        val vm: RestaurantViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBundleData()
        setAdapter()
        addOnScrollListener()
        fetchLocation()
        setListiner()
    }

    private fun getBundleData() {
        title = requireArguments().getString(TOOLBAR_TITLE, "")
        category_id = requireArguments().getInt(CATEGORY_ID, -1)
    }

    private fun initiliase() {
        getContainerActivity().onEventListener = this@FragmentRestaurant
        getViewDataBinding()!!.mapView.getMapAsync(this@FragmentRestaurant)
    }

    private fun setListiner() {
        getViewDataBinding()!!.apply {

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(str: CharSequence, start: Int, before: Int, count: Int) {
                    if (str.length != 0) {
                        filterSearchList(str)
                    } else {
                        if (restaurantList.isNotEmpty()) {
                            getViewDataBinding()!!.layoutNoDataFound.tvNoDataFound.hide()
                        }
                        restaurantAdapter!!.updateAdapterList(restaurantList)
                        showFilterMarker(restaurantList)
                    }
                }
            })

            btnStandard.setOnClickListener {
                if (mapbox != null) {
                    mapbox?.setStyle("mapbox://styles/netset/cjnoa63kb12z62sp3r6eiigjs")
                }
            }

            btnHybrid.setOnClickListener {
                if (mapbox != null) {
                    mapbox?.setStyle("mapbox://styles/netset/cjno81pv310xd2rn3jumxrmr7")
                }
            }
        }
    }

    private fun filterSearchList(searchText: CharSequence) {
        getViewDataBinding()!!.apply {
            val filteredList = ArrayList<YelpApiResponse.Business>()

            restaurantList.forEachIndexed { index, model ->
                if (model.name!!.startsWith(searchText, true)) {
                    filteredList.add(model)
                }
            }

            if (mapView.visibility != View.VISIBLE) {
                if (filteredList.isEmpty()) {
                    layoutNoDataFound.tvNoDataFound.show()
                } else {
                    layoutNoDataFound.tvNoDataFound.hide()
                }
            }

            restaurantAdapter!!.updateAdapterList(filteredList)

            if (mapView.visibility == View.VISIBLE) {
                showFilterMarker(filteredList)
            }
        }
    }

    private fun showFilterMarker(filteredList: ArrayList<YelpApiResponse.Business>) {
        try {
            mapbox?.clear()
            safteyCount=0
            filteredList.forEachIndexed { index, model ->
                createMarker(
                    model.coordinates?.latitude!!,
                    model.coordinates?.longitude!!,
                    model.name,
                    model.location?.display_address?.get(0) ?: "--------", index
                )
            }
            if (safteyCount!=0) {
                val bounds = boundsBuilder.build()
                mapbox?.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 2000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addOnScrollListener() {
        getViewDataBinding()!!.apply {
            val linearLayoutManager = LinearLayoutManager(getContainerActivity())
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            rvRestaurant.layoutManager = linearLayoutManager
            rvRestaurant.adapter = restaurantAdapter

            rvRestaurant.addOnScrollListener(object :
                PaginationListener(linearLayoutManager) {
                override fun loadMoreItems() {
                    if (etSearch.text.toString().isEmpty() && hasNext) {
                        pageNo++
                        hasNext = false
                        showPageLoader()
                        Handler(Looper.getMainLooper()).postDelayed({
                            getRestaurantInfo()
                        }, 400)
                    }
                }

                override fun isLastPage(): Boolean {
                    return isLastPage
                }

                override fun isLoading(): Boolean {
                    return hasNext
                }

            })
        }
    }

    fun sortDistance(
        locations: kotlin.collections.ArrayList<YelpApiResponse.Business>,
        myLatitude: Double,
        myLongitude: Double
    ): kotlin.collections.ArrayList<YelpApiResponse.Business> {

        val comp = object : Comparator<YelpApiResponse.Business> {

            override fun compare(o: YelpApiResponse.Business, o2: YelpApiResponse.Business): Int {
                val result1 = FloatArray(3)
                Location.distanceBetween(
                    myLatitude,
                    myLongitude,
                    o.coordinates!!.latitude!!.toDouble(),
                    o.coordinates!!.longitude!!.toDouble(),
                    result1
                )
                val distance1 = result1[0]
                val result2 = FloatArray(3)
                Location.distanceBetween(
                    myLatitude,
                    myLongitude,
                    o2.coordinates!!.latitude!!.toDouble(),
                    o2.coordinates!!.longitude!!.toDouble(),
                    result2
                )
                val distance2 = result2[0]
                return distance1.compareTo(distance2)
            }
        }
        Collections.sort(locations, comp)
        return locations
    }

    private fun setAdapter() {
        getViewDataBinding()!!.apply {

            if (restaurantAdapter == null) {

                restaurantAdapter =
                    RecyclerViewGenericAdapter(restaurantList, R.layout.item_restaurant,
                        object : RecyclerCallback<ItemRestaurantBinding, YelpApiResponse.Business> {
                            override fun bindData(
                                binder: ItemRestaurantBinding,
                                model: YelpApiResponse.Business,
                                position: Int,
                                itemView: View?
                            ) {
                                binder.apply {

                                    tvTitle.text = model.name
                                    tvSubTitle.text = model.location?.display_address?.get(0) ?: "------------"
                                    tvDistance.text = getMiles(model.distance) + "miles"
                                    loadGlide(model.image_url, ivLogo, getContainerActivity(), R.drawable.placeholder)

                                    itemView?.setOnClickListener {
                                        navigateToCityHall(model)
                                    }
                                }
                            }
                        })
            }
        }
    }

    private fun navigateToCityHall(model: YelpApiResponse.Business) {
         val bundle = Bundle()
         bundle.putString(TOOLBAR_TITLE, model.location?.display_address?.get(0) ?: "------------")
         bundle.putString(SNIPPET, model.name)
         bundle.putString(TYPE, TYPE_YELP)
         bundle.putString(EVENT_ID, model.id)
         bundle.putString(DESITNATION_LATITUDE, model.coordinates?.latitude.toString())
         bundle.putString(DESITNATION_LONGITUDE, model.coordinates?.longitude.toString())
         bundle.putString(DETAIL_MODEL, getViewModel().getReflectionUtils().convertPojoToJson(getDetailModel(model)))
         replaceFragment(setArguments(FragmentDetail(), bundle), true, true)
    }

    private fun getDetailModel(model: YelpApiResponse.Business): DetailDataModel {
        val detailModel = DetailDataModel(model.name,model.location?.display_address?.get(0) ?: "__________",
            model.image_url,model.display_phone ?: model.phone,"")
        return detailModel
    }

    var oneTime = true
    var latLng: LatLng? = null
    fun fetchLocation() {
        locationPermission(object : PermissionListener {
            override fun onAccepted(lat: Any, lng: Any) {
                if (oneTime && lat != null) {
                    oneTime = false
                    latLng = LatLng(lat as Double, lng as Double)
                    getDefaultRestaurants()
                }
            }
        })
    }

    private fun getDefaultRestaurants() {

        getViewModel().getDefaultRestaurantInfo(
            getContainerActivity(),
            latLng?.latitude!!,
            latLng?.longitude!!,
            category_id
        )
        getViewModel().defaultLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()

                }

                ApiResponse.Status.SUCCESS -> {
                    addDefaultData(it.data)
                    removeObserver(getViewModel().defaultMutableLiveData)
                    hideLoader()
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun addDefaultData(data: RestaurantDefaultResponse?) {
        if (data?.businesses != null && !data.businesses.isNullOrEmpty()) {
            data.businesses!!.forEachIndexed { index, model ->
                val bussObj  = YelpApiResponse.Business()
                val bussLocation = YelpApiResponse.Location()
                val coordinates = YelpApiResponse.Coordinates()
                bussObj.id = model.id.toString()
                bussObj.name = model.name ?: EMPTY
                bussLocation.display_address = listOf(model.location?.displayAddress!!.get(0))
                bussObj.location = bussLocation
                bussObj.distance = model.distance?.toDouble()
                bussObj.image_url = EMPTY
                bussObj.phone = model.phone
                bussObj.display_phone = model.phone

                coordinates.latitude = model.latitude
                coordinates.longitude = model.longitude
                bussObj.coordinates = coordinates
                restaurantList.add(bussObj)
            }
            restaurantAdapter!!.updateAdapterList(restaurantList)
            getRestaurantInfo()
        }
    }

    private fun getRestaurantInfo() {

        val restaurant_url = YELP_SEARCH_BASE_URL +
                "latitude=${latLng?.latitude}&" +
                "longitude=${latLng?.longitude}&" +
                "sort_by=distance&" +
                "limit=${LIMIT}&" +
                "offset=${getOffsetValue()}&" +
                "radius=40000&" +
                "term=${getTermValue()}"

        getViewModel().getRestaurantInfo(getContainerActivity(), restaurant_url)
        getViewModel().restaurantLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    if (pageNo == 1) {
                        showLoader()
                    }
                }

                ApiResponse.Status.SUCCESS -> {
                    updateUi(it.data)
                    removeObserver(getViewModel().restaurantMutableLiveData)
                    hideLoader()
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun getOffsetValue(): Int {
        if (pageNo == 1) {
            return OFFSET
        } else {
            OFFSET = OFFSET + LIMIT
            return OFFSET
        }
    }

    private fun getTermValue(): String {
        getViewDataBinding()!!.apply {
            when (title) {
                getString(R.string.restaurants) -> {
                    return "restaurants"
                }

                getString(R.string.shopping) -> {
                    return "shopping"
                }
            }
        }
        return ""
    }

    private fun updateUi(yelpApiResponse: YelpApiResponse?) {
        getViewDataBinding()!!.apply {

            if (yelpApiResponse != null && yelpApiResponse.businesses!!.isNotEmpty()) {
                hidePageLoader()
                restaurantList.addAll(yelpApiResponse.businesses!!)
                val newlist = restaurantList.distinct()
                restaurantList.clear()
                restaurantList.addAll(newlist)
                restaurantAdapter!!.updateAdapterList(restaurantList)

                if (restaurantList.size <= yelpApiResponse.total!!) {
                    isLastPage = false
                    hasNext = true
                } else {
                    isLastPage = true
                    hasNext = false
                }
            }

            initiliase()
        }
    }

    private fun showPageLoader() {
        getViewDataBinding()!!.apply {
            if (pageNo != 1) {
                loader.show()
            }
        }
    }

    private fun hidePageLoader() {
        getViewDataBinding()!!.apply {
            loader.hide()
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapbox = mapboxMap
        try {
            mapboxMap.setStyle(Style.Builder().fromUrl(Style.OUTDOORS)) {
                enableLocationComponent(it)

                mapbox!!.getUiSettings().setAttributionEnabled(false);
                mapbox!!.getUiSettings().setLogoEnabled(false);
                mapbox!!.uiSettings.isTiltGesturesEnabled = false

                mapboxMap.onInfoWindowClickListener =
                    MapboxMap.OnInfoWindowClickListener { marker ->
                        val list = restaurantList.filter { it.name!!.contains(marker.title) }.toList()
                        if (list.isNotEmpty()) {
                            navigateToCityHall(list.get(0))
                        }
                        true
                    }

                safteyCount=0
                for (i in restaurantList.indices) {
                    createMarker(
                        restaurantList.get(i).coordinates?.latitude!!,
                        restaurantList.get(i).coordinates?.longitude!!,
                        restaurantList.get(i).name,
                        restaurantList.get(i).location?.display_address?.get(0) ?: "--------", i
                    )
                }

                if (safteyCount != 0) {
                    val bounds = boundsBuilder.build()
                    mapbox?.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 2000)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    var marker: Marker? = null
    fun createMarker(
        latitude: Float,
        longitude: Float,
        title: String?,
        snippet: String?,
        pos: Int?
    ) {
        try {
            if (latitude.toDouble() != 0.0 && calculateDistance(latLng!!.latitude, latLng!!.longitude, latitude.toDouble(), longitude.toDouble()) < 15) {
                safteyCount++
                boundsBuilder.include(LatLng(latitude.toDouble(), longitude.toDouble()))
            }
            marker = mapbox!!.addMarker(
                MarkerOptions()
                    .position(LatLng(latitude.toDouble(), longitude.toDouble()))
                    .title(title)
                    .snippet(snippet)
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContainerActivity())) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions =
                LocationComponentOptions.builder(getContainerActivity())
                    .trackingGesturesManagement(true)
                    .accuracyColor(
                        ContextCompat.getColor(
                            getContainerActivity(),
                            R.color.mapboxGreen
                        )
                    )
                    .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(getContainerActivity(), loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapbox!!.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        }
    }

    override fun onEventClick() {
        getViewDataBinding()!!.apply {
            if (mapView.visibility == View.VISIBLE) {
                rvRestaurant.show()
                mapView.hide()
                layoutButtons.hide()
                loadGlide(
                    R.drawable.ic_box_location,
                    getContainerActivity().binding!!.ivRightIcon,
                    getContainerActivity()
                )
            } else {
                mapView.show()
                layoutButtons.show()
                rvRestaurant.hide()
                loadGlide(
                    R.drawable.iv_listview,
                    getContainerActivity().binding!!.ivRightIcon,
                    getContainerActivity()
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getViewDataBinding()!!.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        getViewDataBinding()!!.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        getViewDataBinding()!!.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        getViewDataBinding()!!.mapView.onDestroy()
    }

}