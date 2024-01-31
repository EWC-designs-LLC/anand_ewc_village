package com.golfcart.ui.golf_course_sub

import android.annotation.SuppressLint
import android.app.Dialog
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.DialogSortbyBinding
import com.golfcart.databinding.FragmentGolfCourseSubCategoryBinding
import com.golfcart.databinding.ItemRestaurantBinding
import com.golfcart.model.app_interface.EventsInterface
import com.golfcart.model.app_interface.OnBackEventCallBack
import com.golfcart.model.app_interface.PermissionListener
import com.golfcart.model.detail_data.DetailDataModel
import com.golfcart.model.golf_course_sub.GolfCouseSubModel
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.detail.FragmentDetail
import com.golfcart.ui.point_to_point.FragmentPointToPoint
import com.golfcart.ui.village.BottomSheetBinding
import com.golfcart.ui.village.VillageActivity
import com.golfcart.ui.village.VillageBottomSheet
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.loadGlide
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.TYPE_GOLF
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_LAT_END
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_LAT_START
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_LNG_END
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_LNG_START
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Collections
import java.util.Comparator

class FragmentGolfCourseSubcategory :
    VillageFragment<FragmentGolfCourseSubCategoryBinding, GolfcourseSubcategoryViewModel>(),
    OnMapReadyCallback, EventsInterface, OnBackEventCallBack {

    var golfCouseAdapter: RecyclerViewGenericAdapter<GolfCouseSubModel.Data, ItemRestaurantBinding>? = null
    var catgoriesList = ArrayList<GolfCouseSubModel.Data>()
    var category_id = -1
    var sub_category_id = -1
    var mapbox: MapboxMap? = null
    var boundsBuilder = LatLngBounds.Builder()
    var isFromPoint = false
    var pointType = "1"

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentGolfCourseSubcategory
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, requireArguments().getString(VillageConstants.TOOLBAR_TITLE, ""), true,
            -1, false, "", true, R.drawable.ic_box_location
        )
    }

    override fun getBindingVariable(): Int {
        return BR.golf_course_sub
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_golf_course_sub_category
    }

    override fun getViewModel(): GolfcourseSubcategoryViewModel {
        val vm: GolfcourseSubcategoryViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            getBundleData()
            initiliase()
            setAdapter()
            setListiner()
            fetchLocation()
        }
    }

    private fun initiliase() {
        getContainerActivity().onEventListener = this@FragmentGolfCourseSubcategory
        getContainerActivity().onBackEventCallback = this@FragmentGolfCourseSubcategory
        getViewDataBinding()!!.mapView.getMapAsync(this@FragmentGolfCourseSubcategory)
    }

    private fun getBundleData() {
        category_id = requireArguments().getInt(VillageConstants.CATEGORY_ID, -1)
        sub_category_id = requireArguments().getInt(VillageConstants.SUB_CATEGORY_ID, -1)
        isFromPoint = requireArguments().getBoolean(VillageConstants.IS_FROM_POINT_TO_POINT, false)
        pointType = requireArguments().getString(VillageConstants.POINT_TYPE,"1")
    }

    var oneTime = true
    var latLng: LatLng? = null
    fun fetchLocation() {
        locationPermission(object : PermissionListener {
            override fun onAccepted(lat: Any, lng: Any) {
                if (oneTime && lat != null) {
                    oneTime = false
                    latLng = LatLng(lat as Double, lng as Double)
                    getGolfCourseSubCategories()
                }
            }
        })
    }

    private fun getGolfCourseSubCategories() {
        getViewModel().getGolfCourseCategories(
            getContainerActivity(),
            category_id,
            sub_category_id,
            getViewModel().getAppRepository().getUserId(),
            latLng!!.latitude,
            latLng!!.longitude
        )
        getViewModel().golfCourseSubLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateUi(it.data)
                    removeObserver(getViewModel().golfCourseSubMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
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
                        if (catgoriesList.isNotEmpty()) {
                            getViewDataBinding()!!.layoutNoDataFound.tvNoDataFound.hide()
                            golfCouseAdapter!!.updateAdapterList(catgoriesList)
                            showFilterMarker(catgoriesList)
                        }
                    }
                }
            })

            tvSortBy.setOnClickListener {
                dialogSortby()
            }

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

    private fun dialogSortby() {

        VillageBottomSheet<DialogSortbyBinding>(getContainerActivity(),
            R.layout.dialog_sortby,
            object : BottomSheetBinding<DialogSortbyBinding> {
                override fun onBind(binder: DialogSortbyBinding, dialog: Dialog) {
                    binder.apply {
                        tvCancel.setOnClickListener {
                            dialog.dismiss()
                        }

                        tvDistance.setOnClickListener {
                            filterByDistance()
                            dialog.dismiss()
                        }

                        tvAlphabetically.setOnClickListener {
                            sortByAlphabet()
                            dialog.dismiss()
                        }
                    }
                }
            }).show()
    }


    private fun filterByDistance() {
        if (golfCouseAdapter?.itemCount != 0) {
            val list = golfCouseAdapter?.items as java.util.ArrayList<GolfCouseSubModel.Data>
            val sortedList = sortDistance(list, latLng!!.latitude, latLng!!.longitude)
            golfCouseAdapter!!.updateAdapterList(sortedList)
        }
    }

    private fun sortByAlphabet() {
        if (golfCouseAdapter?.itemCount != 0) {
            val alphabetList = golfCouseAdapter?.items as java.util.ArrayList<GolfCouseSubModel.Data>
            Collections.sort(alphabetList, object : Comparator<GolfCouseSubModel.Data> {
                override fun compare(
                    model1: GolfCouseSubModel.Data?,
                    model2: GolfCouseSubModel.Data?
                ): Int {
                    return model1?.location!!.compareTo(model2?.location!!, ignoreCase = true)
                }
            })
            golfCouseAdapter!!.updateAdapterList(alphabetList)
        }
    }

    fun sortDistance(
        locations: kotlin.collections.ArrayList<GolfCouseSubModel.Data>,
        myLatitude: Double,
        myLongitude: Double
    ): kotlin.collections.ArrayList<GolfCouseSubModel.Data> {

        val comp = object : Comparator<GolfCouseSubModel.Data> {

            override fun compare(o: GolfCouseSubModel.Data, o2: GolfCouseSubModel.Data): Int {
                val result1 = FloatArray(3)
                Location.distanceBetween(
                    myLatitude,
                    myLongitude,
                    o.latitude!!.toDouble(),
                    o.longitude!!.toDouble(),
                    result1
                )
                val distance1 = result1[0]
                val result2 = FloatArray(3)
                Location.distanceBetween(
                    myLatitude,
                    myLongitude,
                    o2.latitude!!.toDouble(),
                    o2.longitude!!.toDouble(),
                    result2
                )
                val distance2 = result2[0]
                return distance1.compareTo(distance2)
            }
        }
        Collections.sort(locations, comp)
        return locations
    }

    private fun filterSearchList(searchText: CharSequence) {
        getViewDataBinding()!!.apply {
            val filteredList = ArrayList<GolfCouseSubModel.Data>()

            catgoriesList.forEachIndexed { index, model ->
                if (model.location!!.startsWith(searchText, true)) {
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

            golfCouseAdapter!!.updateAdapterList(filteredList)

            if (mapView.visibility == View.VISIBLE) {
                showFilterMarker(filteredList)
            }
        }
    }


    private fun showFilterMarker(filteredList: ArrayList<GolfCouseSubModel.Data>) {
        try {
            mapbox?.clear()
            filteredList.forEachIndexed { index, model ->
                createMarker(
                    model.latitude!!,
                    model.longitude!!,
                    model.location,
                    model.address ?: "--------", index
                )
            }
            val bounds = boundsBuilder.build()
            mapbox?.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun updateUi(response: GolfCouseSubModel?) {
        if (!response?.data.isNullOrEmpty()) {
            catgoriesList.clear()
            catgoriesList.addAll(response?.data!!)
            golfCouseAdapter!!.updateAdapterList(catgoriesList)
        }
    }

    private fun setAdapter() {
        getViewDataBinding()!!.apply {
            getContainerActivity().runOnUiThread {
                if (golfCouseAdapter == null) {

                    golfCouseAdapter =
                        RecyclerViewGenericAdapter(catgoriesList, R.layout.item_restaurant,
                            object : RecyclerCallback<ItemRestaurantBinding, GolfCouseSubModel.Data> {
                                override fun bindData(
                                    binder: ItemRestaurantBinding,
                                    model: GolfCouseSubModel.Data,
                                    position: Int,
                                    itemView: View?
                                ) {
                                    binder.apply {

                                        tvTitle.text = model.location
                                        tvSubTitle.text = model.address
                                        loadGlide(model.image, ivLogo, getContainerActivity())
                                        if (model.latitude != null) {
                                            tvDistance.text = "${findDistance(latLng!!.latitude, latLng!!.longitude, model.latitude!!.toDouble(), model.longitude!!.toDouble())}" + "miles"
                                        }
                                        itemView?.setOnClickListener {
                                            if (isFromPoint){
                                                navigateToPoint(model)
                                            }else{
                                                navigateToCityHall(model,position)
                                            }
                                        }
                                    }
                                }
                            })
                    rvGolfCourseSub.adapter = golfCouseAdapter
                }
            }
        }
    }

    private fun navigateToPoint(model: GolfCouseSubModel.Data) {
        if (pointType.equals("1")){
            VillageActivity.point_souce_lat = model.latitude?.toDouble() ?: VillageConstants.VILLAGE_LAT_START
            VillageActivity.point_souce_lng = model.longitude?.toDouble() ?: VillageConstants.VILLAGE_LNG_START
            VillageActivity.point_source_address = (model.address ?: model.location).toString()
        }else{
            VillageActivity.point_des_lat = model.latitude?.toDouble() ?: VillageConstants.VILLAGE_LAT_START
            VillageActivity.point_des_lng = model.longitude?.toDouble() ?: VillageConstants.VILLAGE_LNG_START
            VillageActivity.point_des_address = (model.address ?: model.location).toString()
        }

        val bundle = Bundle()
        bundle.putBoolean(VillageConstants.IS_POINT_SEARCH,true)
        bundle.putString(VillageConstants.POINT_TYPE,pointType)
        replaceFragment(setArguments(FragmentPointToPoint(),bundle),true,false)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        try {
            mapbox = mapboxMap
            mapboxMap.setStyle(Style.Builder().fromUrl(Style.OUTDOORS)) {
                enableLocationComponent(it)

                mapbox!!.getUiSettings().setAttributionEnabled(false);
                mapbox!!.getUiSettings().setLogoEnabled(false);
                mapbox!!.uiSettings.isTiltGesturesEnabled = false

                mapboxMap.onInfoWindowClickListener =
                    MapboxMap.OnInfoWindowClickListener { marker ->
                        navigateToCityHall(catgoriesList.get(marker.id.toInt()),marker.id.toInt())
                        true
                    }

                for (i in catgoriesList.indices) {
                    createMarker(
                        catgoriesList.get(i).latitude!!,
                        catgoriesList.get(i).longitude!!,
                        catgoriesList.get(i).location,
                        catgoriesList.get(i).address,
                        i
                    )
                }

                boundsBuilder.include(LatLng(VILLAGE_LAT_START,VILLAGE_LNG_START))
                boundsBuilder.include(LatLng(VILLAGE_LAT_END,VILLAGE_LNG_END))
                val bounds = boundsBuilder.build()
                mapbox?.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70), 2000)
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

    private fun navigateToCityHall(model: GolfCouseSubModel.Data,position:Int) {
        val bundle = Bundle()
        bundle.putString(VillageConstants.TOOLBAR_TITLE, model.address)
        bundle.putString(VillageConstants.SNIPPET, model.location)
        bundle.putString(VillageConstants.TYPE, TYPE_GOLF)
        bundle.putString(VillageConstants.EVENT_ID, model.id.toString())
        bundle.putString(VillageConstants.FAVOURITE_STATUS, model.favoriteStatus.toString())
        bundle.putBoolean(VillageConstants.IS_FROM_GOLF_SUBCATEGORY, true)
        bundle.putInt(VillageConstants.POSITION, position)
        bundle.putString(VillageConstants.DESITNATION_LATITUDE, model.latitude.toString())
        bundle.putString(VillageConstants.DESITNATION_LONGITUDE, model.longitude.toString())
        bundle.putString(VillageConstants.DETAIL_MODEL, getViewModel().getReflectionUtils().convertPojoToJson(getDetailModel(model)))
        replaceFragment(setArguments(FragmentDetail(), bundle), true, true)
    }

    private fun getDetailModel(model: GolfCouseSubModel.Data): DetailDataModel {
        val detailModel = DetailDataModel(model.location,model.address ,
            model.image,model.contactNumber ?: model.golfNumber,model.golfNumber)
        return detailModel
    }

    override fun onEventClick() {
        getViewDataBinding()!!.apply {
            if (mapView.visibility == View.VISIBLE) {
                rvGolfCourseSub.show()
                tvSortBy.show()
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
                rvGolfCourseSub.hide()
                tvSortBy.hide()
                loadGlide(
                    R.drawable.iv_listview,
                    getContainerActivity().binding!!.ivRightIcon,
                    getContainerActivity()
                )
            }
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

    override fun onBackTrigger(favStatus: Boolean, favPosition: Int?) {
        if (favStatus){
            catgoriesList[favPosition!!].favoriteStatus = 1
        }else{
            catgoriesList[favPosition!!].favoriteStatus = 0
        }
        golfCouseAdapter!!.updateAdapterList(catgoriesList)
    }

}