package com.golfcart.ui.find_property

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.DialogFindPropertyBinding
import com.golfcart.databinding.DialogSortbyBinding
import com.golfcart.databinding.FragmentFindPropertyBinding
import com.golfcart.databinding.ItemRestaurantBinding
import com.golfcart.model.app_interface.EventsInterface
import com.golfcart.model.app_interface.PermissionListener
import com.golfcart.model.detail_data.DetailDataModel
import com.golfcart.model.property.FindPropertyModel
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.detail.FragmentDetail
import com.golfcart.ui.village.BottomSheetBinding
import com.golfcart.ui.village.DialogBinding
import com.golfcart.ui.village.VillageBottomSheet
import com.golfcart.ui.village.VillageDialog
import com.golfcart.ui.village.VillageFragment
import com.golfcart.ui.webview.FragmentWebView
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.loadGlide
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.EMPTY
import com.google.android.material.textview.MaterialTextView
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
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


class FragmentFindProperty : VillageFragment<FragmentFindPropertyBinding, FindPropertyViewModel>(),
    OnMapReadyCallback, EventsInterface {

    var propertyAdapter: RecyclerViewGenericAdapter<FindPropertyModel.Data, ItemRestaurantBinding>? =
        null
    var saleList = ArrayList<FindPropertyModel.Data>()
    var rentList = ArrayList<FindPropertyModel.Data>()
    var mapbox: MapboxMap? = null
    var boundsBuilder = LatLngBounds.Builder()

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentFindProperty
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, requireArguments().getString(VillageConstants.TOOLBAR_TITLE, ""), false,
            -1, false, "", true, R.drawable.ic_box_location
        )
    }

    override fun getBindingVariable(): Int {
        return BR.home
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_find_property
    }

    override fun getViewModel(): FindPropertyViewModel {
        val vm: FindPropertyViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            setViews()
            setAdapter()
            fetchLocation()
            setListiner()
        }
    }

    private fun setViews() {
        getViewDataBinding()!!.apply {
            btnSale.tag=1
            btnRent.tag=0
        }
    }

    private fun initiliase() {
        getContainerActivity().onEventListener = this@FragmentFindProperty
        getViewDataBinding()!!.mapView.getMapAsync(this@FragmentFindProperty)
    }

    private fun setListiner() {
        getViewDataBinding()!!.apply {
            btnSale.setOnClickListener {
                btnSale.tag = 1
                btnRent.tag = 0
                btnSale.setBackground(resources.getDrawable(R.drawable.bg_button_brown_dark))
                btnRent.setBackground(resources.getDrawable(android.R.color.transparent))
                if (mapView.visibility == View.VISIBLE) {
                    showFilterMarker(saleList)
                } else {
                    rvProperty.adapter = propertyAdapter
                    propertyAdapter!!.updateAdapterList(saleList)
                }
            }

            btnRent.setOnClickListener {
                getViewDataBinding()!!.apply {
                    btnSale.tag = 0
                    btnRent.tag = 1

                    btnRent.setBackground(resources.getDrawable(R.drawable.bg_button_brown_dark))
                    btnSale.setBackground(resources.getDrawable(android.R.color.transparent))

                    if (mapView.visibility == View.VISIBLE) {
                        showFilterMarker(rentList)
                    } else {
                        rvProperty.adapter = propertyAdapter
                        propertyAdapter!!.updateAdapterList(rentList)
                    }
                }
            }

            btnSort.setOnClickListener {
                bottomSheetSortby()
            }
        }
    }

    var oneTime = true
    var latLng: LatLng? = null
    fun fetchLocation() {
        locationPermission(object : PermissionListener {
            override fun onAccepted(lat: Any, lng: Any) {
                if (oneTime && lat != null) {
                    oneTime = false
                    latLng = LatLng(lat as Double, lng as Double)
                    getFindProperty()
                }
            }
        })
    }

    private fun getFindProperty() {
        getViewModel().findProperty(getContainerActivity())
        getViewModel().propertyliveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateUi(it.data)
                    removeObserver(getViewModel().propertyMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun updateUi(response: FindPropertyModel?) {
        if (!response?.data.isNullOrEmpty()) {
            saleList.clear()
            rentList.clear()
            val filteredList = response?.data?.filter { !it.latitude.isNullOrBlank()  }
            filteredList?.forEachIndexed { index, model ->
                if (model._for.equals("Sale", true)) {
                    saleList.add(model)
                } else {
                    rentList.add(model)
                }
            }
            initiliase()
            propertyAdapter!!.updateAdapterList(saleList)
        }
    }

    private fun setAdapter() {
        getViewDataBinding()!!.apply {
            getContainerActivity().runOnUiThread {
                if (propertyAdapter == null) {

                    propertyAdapter = RecyclerViewGenericAdapter(saleList, R.layout.item_restaurant,
                        object : RecyclerCallback<ItemRestaurantBinding, FindPropertyModel.Data> {
                            @SuppressLint("SetTextI18n")
                            override fun bindData(
                                binder: ItemRestaurantBinding, model: FindPropertyModel.Data, position: Int, itemView: View?) {
                                binder.apply {

                                    tvTitle.text = model.title
                                    if (model.latitude?.isEmpty()!!) {
                                        tvSubTitle.text = model.address + "(0.00 mi)"
                                    } else {
                                        tvSubTitle.text = model.address + "(${
                                            findDistance(latLng!!.latitude, latLng!!.longitude, model.latitude!!.toDouble(), model.longitude!!.toDouble())} mi)"
                                    }
                                    loadGlide(model.thumbnail, ivLogo, getContainerActivity(), R.drawable.placeholder)

                                    tvPrice.show()
                                    tvPrice.text=model.price

                                    itemView?.setOnClickListener {
                                        showDirectionDialog(model)
                                    }

                                }
                            }
                        })
                    rvProperty.adapter = propertyAdapter
                }
            }
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapbox = mapboxMap

        mapbox = mapboxMap
        mapboxMap.setStyle(Style.Builder().fromUrl(Style.OUTDOORS)) {
            enableLocationComponent(it)

            mapbox!!.getUiSettings().setAttributionEnabled(false);
            mapbox!!.getUiSettings().setLogoEnabled(false);
            mapbox!!.uiSettings.isTiltGesturesEnabled = false

            mapboxMap.onInfoWindowClickListener =
                MapboxMap.OnInfoWindowClickListener { marker ->

                    true
                }
        }

        saleList.forEachIndexed { index, saleItem ->
            if (!saleItem.latitude.isNullOrBlank()) {
                createMarker(
                    saleItem.latitude!!.toFloat(),
                    saleItem.longitude!!.toFloat(),
                    saleItem.title,
                    saleItem.address ?: EMPTY,
                    index
                )
            }
        }

        boundsBuilder.include(
            LatLng(
                VillageConstants.VILLAGE_LAT_START,
                VillageConstants.VILLAGE_LNG_START
            )
        )
        boundsBuilder.include(
            LatLng(
                VillageConstants.VILLAGE_LAT_END,
                VillageConstants.VILLAGE_LNG_END
            )
        )
        val bounds = boundsBuilder.build()
        mapbox?.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70), 2000)
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
            var icon: Icon? = null
            marker = null

            if (latitude.toDouble() != 0.0 && calculateDistance(
                    latLng!!.latitude,
                    latLng!!.longitude,
                    latitude.toDouble(),
                    longitude.toDouble()
                ) < 15
            ) {
                boundsBuilder.include(LatLng(latitude.toDouble(), longitude.toDouble()))
            }

            val markerOptions = MarkerOptions()
            markerOptions.position(LatLng(latitude.toDouble(), longitude.toDouble())).title(title)
                .snippet(snippet)
            if (getViewDataBinding()!!.btnRent.tag == 1) {
                icon = IconFactory.getInstance(getContainerActivity())
                    .fromResource(com.golfcart.R.drawable.ic_greenmarker)
                val drawable = resources.getDrawable(com.golfcart.R.drawable.ic_greenmarker, null)
                val resizedDrawable = resizeDrawable(drawable, 75, 80)
                val markerIcon: Icon =
                    IconFactory.getInstance(getContainerActivity()).fromBitmap(resizedDrawable)
                markerOptions.icon(markerIcon)
            }
            marker = mapbox!!.addMarker(markerOptions)

        } catch (e: Exception) {
            e.printStackTrace()
            showToast("error")
        }
    }

    private fun resizeDrawable(drawable: Drawable, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createScaledBitmap(drawableToBitmap(drawable), width, height, false)
        return bitmap
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
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
                cameraMode = CameraMode.NONE

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        }
    }

    private fun showFilterMarker(filteredList: ArrayList<FindPropertyModel.Data>) {
        mapbox?.clear()
        mapbox?.removeAnnotations()
        filteredList.forEachIndexed { index, model ->
            if (!model.latitude!!.isEmpty()) {
                createMarker(
                    model.latitude!!.toFloat(),
                    model.longitude!!.toFloat(),
                    model.title,
                    model.address ?: "--------", index
                )
            }
        }
        val bounds = boundsBuilder.build()
        mapbox?.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 2000)
    }

    override fun onEventClick() {
        getViewDataBinding()!!.apply {
            if (mapView.visibility == View.VISIBLE) {
                rvProperty.show()
                mapView.hide()
                btnSort.show()
                loadGlide(
                    R.drawable.ic_box_location,
                    getContainerActivity().binding!!.ivRightIcon,
                    getContainerActivity()
                )
                loadPropertyList()
            } else {
                mapView.show()
                btnSort.hide()
                rvProperty.hide()
                loadGlide(
                    R.drawable.iv_listview,
                    getContainerActivity().binding!!.ivRightIcon,
                    getContainerActivity()
                )
            }
        }
    }

    fun loadPropertyList() {
        getViewDataBinding()!!.apply {
            if (btnSale.tag == 1) {
                propertyAdapter!!.updateAdapterList(saleList)
            } else {
                propertyAdapter!!.updateAdapterList(rentList)
            }
        }
    }

    private fun navigateToDetail(model: FindPropertyModel.Data) {
        val bundle = Bundle()
        bundle.putString(VillageConstants.TOOLBAR_TITLE, model.address)
        bundle.putString(VillageConstants.SNIPPET, model.title)
        bundle.putString(VillageConstants.TYPE, VillageConstants.TYPE_YELP)
        bundle.putString(VillageConstants.EVENT_ID, model.id.toString())
        bundle.putString(VillageConstants.DESITNATION_LATITUDE, model.latitude.toString())
        bundle.putString(VillageConstants.DESITNATION_LONGITUDE, model.longitude.toString())
        bundle.putString(VillageConstants.DETAIL_MODEL, getViewModel().getReflectionUtils().convertPojoToJson(getDetailModel(model)))
        replaceFragment(setArguments(FragmentDetail(), bundle), true, true)
    }

    private fun getDetailModel(model: FindPropertyModel.Data): DetailDataModel {
        val detailModel = DetailDataModel(model.address,model.title  ?: "__________",
            model.thumbnail,"" ,"")
        return detailModel
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

    private fun bottomSheetSortby() {
        VillageBottomSheet<DialogSortbyBinding>(getContainerActivity(),
            R.layout.dialog_sortby,
            object : BottomSheetBinding<DialogSortbyBinding> {
                override fun onBind(binder: DialogSortbyBinding, dialog: Dialog) {
                    binder.apply {
                        setBottomSheetViews(binder)

                        tvCancel.setOnClickListener {
                            if (getViewDataBinding()!!.btnSale.tag == 1){
                                if (binder.tvDistance.tag == 1) {
                                    AsscendingOrderListLowToHigh(saleList, false)
                                }
                                else if (binder.tvAlphabetically.tag == 1){
                                    AsscendingOrderListLowToHigh(saleList, true)
                                }
                            }else{
                                if (binder.tvDistance.tag == 1) {
                                    AsscendingOrderListLowToHigh(rentList, false)
                                }else if(binder.tvAlphabetically.tag == 1){
                                    AsscendingOrderListLowToHigh(rentList, true)
                                }
                            }

                            dialog.dismiss()
                        }

                        tvDistance.setOnClickListener {
                            setCommonBackground(tvDistance,tvAlphabetically)
                        }

                        tvAlphabetically.setOnClickListener {
                            setCommonBackground(tvAlphabetically,tvDistance)
                        }
                    }
                }
            }).show()
    }

    private fun setCommonBackground(button1: MaterialTextView, button2: MaterialTextView) {
        button1.tag = 1
        button1.setBackgroundResource(R.color.colorPrimary)
        button1.setTextColor(ContextCompat.getColor(getContainerActivity(), R.color.white))
        button2.tag = 0
        button2.setBackgroundResource(R.color.white)
        button2.setTextColor(ContextCompat.getColor(getContainerActivity(), R.color.black))
    }

    private fun setBottomSheetViews(binder: DialogSortbyBinding) {
        binder.apply {
            tvDistance.tag = 0
            tvDirectionBy.text = getString(R.string.sort_by)
            tvDistance.text = getString(R.string.price_low_to_high)
            tvDistance.setTextColor(ContextCompat.getColor(getContainerActivity(), R.color.black))
            tvAlphabetically.tag = 0
            tvAlphabetically.text = getString(R.string.price_high_to_low)
            tvAlphabetically.setTextColor(
                ContextCompat.getColor(
                    getContainerActivity(),
                    R.color.black
                )
            )
            tvCancel.text = getString(R.string.apply)
            tvCancel.setTextColor(
                ContextCompat.getColor(
                    getContainerActivity(),
                    R.color.colorPrimary
                )
            )

        }
    }

    fun AsscendingOrderListLowToHigh(propertyList: List<FindPropertyModel.Data>?,isReverse : Boolean) {
        Collections.sort(propertyList!!, { lhs, rhs ->
            java.lang.Double.compare(getPrice(lhs?.price!!), getPrice(rhs?.price!!));
        })
        if (isReverse){
            Collections.reverse(propertyList)
        }
        propertyAdapter!!.updateAdapterList(propertyList as ArrayList<FindPropertyModel.Data>)
    }

    fun getPrice(str: String): Double {
        return if (str.contains("Varies")) {
            "0.0".toDouble()
        } else {
            str.toDouble()
        }
    }

    private fun navigateToWebView(url: String?) {
        val bundle = Bundle()
        bundle.putString(VillageConstants.TOOLBAR_TITLE, getString(R.string.find_property))
        bundle.putString(VillageConstants.WEB_URL,url)
        replaceFragment(setArguments(FragmentWebView(), bundle), true, false)
    }

    fun showDirectionDialog(model: FindPropertyModel.Data) {
        VillageDialog<DialogFindPropertyBinding>(
            getContainerActivity(), R.layout.dialog_find_property,
            object : DialogBinding<DialogFindPropertyBinding> {

                override fun onBind(binder: DialogFindPropertyBinding, dialog: Dialog) {
                    binder.apply {
                        btnDetail.setOnClickListener {
                            navigateToWebView(model.url)
                            dialog.dismiss()
                        }
                        btnDirection.setOnClickListener {
                            navigateToDetail(model)
                            dialog.dismiss()
                        }
                        btnCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                    }
                }
            }, false, true
        ).show(getContainerActivity().supportFragmentManager, VillageDialog::class.java.simpleName)
    }

}