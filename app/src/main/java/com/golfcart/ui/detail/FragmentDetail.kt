package com.golfcart.ui.detail

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.FragmentDetailBinding
import com.golfcart.model.app_interface.PermissionListener
import com.golfcart.model.common.CommonModelResponse
import com.golfcart.model.detail_data.DetailDataModel
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.preview.FragmentPreview
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.geocoder.GeocoderHelper.getPostalCodeFromLatLng
import com.golfcart.utils.view_utils.loadGlide
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.APP_URL
import com.golfcart.utils.village_constant.VillageConstants.AUTO
import com.golfcart.utils.village_constant.VillageConstants.DETAIL_MODEL
import com.golfcart.utils.village_constant.VillageConstants.EMPTY
import com.golfcart.utils.village_constant.VillageConstants.GOLF_CART
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentDetail : VillageFragment<FragmentDetailBinding, DetailViewModel>(),
    OnMapReadyCallback {

    private var isFromGolfSubcategoryScreen: Boolean?=false
    private var isFromFavouriteScreen: Boolean?=false
    private var favPosition: Int?=-1
    private var favStatus: Boolean? = false
    private var title: String? = ""
    private var snippet: String? = ""
    private var mapbox: MapboxMap? = null
    private var destination_latitude: String = "0.0"
    private var destination_longitude: String = "0.0"
    private var type = ""
    private var eventId = "-1"
    private var favouriteStatus = "-1"
    private var detailDataModel : DetailDataModel? =null
    private var isPostalCodeMatch : Boolean=false

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentDetail
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, getString(R.string.details),
            false, -1, false, "",
            false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.detail
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_detail
    }

    override fun getViewModel(): DetailViewModel {
        val vm: DetailViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            getBundleData()
            fetchLocation()
            setViews()
            initiliase()
            setListiner()

        }
    }

    private fun setFavStatus() {
        if (favouriteStatus.equals("-1")) {
            if (isFromFavouriteScreen!!){
                favSelected()
            }else {
                checkFavStatus()
            }
        }else{
            if (favouriteStatus.equals("null") || favouriteStatus.equals("0")){ // not fav
                favNotSelected()
            }else{
                favSelected()
            }
        }
    }

    private fun setListiner() {
        getViewDataBinding()!!.apply {
            layoutCall.rlRoot.setOnClickListener {
                if (!detailDataModel?.contact_number.isNullOrEmpty() || !detailDataModel?.golf_number.isNullOrEmpty()) {
                    openPhoneDialPad(detailDataModel?.contact_number ?: detailDataModel?.golf_number)
                }
            }

            layoutFavourite.rlRoot.setOnClickListener {
                addFavourite()
            }

            layoutShare.rlRoot.setOnClickListener {
                shareDetail()
            }

            tvGolfcart.rlRoot.setOnClickListener {
                if (isPostalCodeMatch){
                    navigateToPreview(GOLF_CART)
                }else{
                    showErrorDialog(getString(R.string.please_return_to_the_villages_city_limits))
                }
            }

            tvAuto.rlRoot.setOnClickListener {
                if (isPostalCodeMatch){
                    navigateToPreview(AUTO)
                }else{
                    showErrorDialog(getString(R.string.please_return_to_the_villages_city_limits))
                }
            }
        }
    }

    private fun navigateToPreview(type:String){
        val bundle = Bundle()
        bundle.putString(VillageConstants.TOOLBAR_TITLE, snippet)
        bundle.putString(VillageConstants.DESITNATION_LATITUDE,destination_latitude)
        bundle.putString(VillageConstants.DESITNATION_LONGITUDE,destination_longitude)
        bundle.putString(VillageConstants.Direction, type)
        replaceFragment(setArguments(FragmentPreview(), bundle), true, true)
    }

    private fun addFavourite() {

        val map = HashMap<String,String?>()

        map.put("user_id", getViewModel().getAppRepository().getUserId().toString())
        map.put("event_id", eventId)
        map.put("status", if (favStatus!!) "0" else "1")
        map.put("address", detailDataModel?.address ?: EMPTY)
        map.put("location", detailDataModel?.location ?: EMPTY)
        map.put("latitude", destination_latitude)
        map.put("longitude",destination_longitude)
        map.put("image", detailDataModel?.image ?: EMPTY)
        map.put("type", type)
        map.put("contact_number", detailDataModel?.contact_number ?: EMPTY)
        map.put("golf_number", detailDataModel?.golf_number ?: EMPTY)
        map.put("submit", "submit")

        getViewModel().addFavourite(
            getContainerActivity(),map
        )
        getViewModel().addFavLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateAddFav()
                    removeObserver(getViewModel().addFavMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    fun shareDetail() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, APP_URL)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun updateAddFav() {
        if (favStatus!!){
            favNotSelected()
        }else{
            favSelected()
        }
    }

    fun favSelected(){
        favStatus=true
        loadGlide(R.drawable.ic_heart, getViewDataBinding()!!.layoutFavourite.ivLogo, getContainerActivity())
        ImageViewCompat.setImageTintList(getViewDataBinding()!!.layoutFavourite.ivLogo, ContextCompat.getColorStateList(getContainerActivity(), R.color.holo_red))
        hideLoader()
    }

    fun favNotSelected(){
        favStatus=false
        loadGlide(R.drawable.ic_heart, getViewDataBinding()!!.layoutFavourite.ivLogo, getContainerActivity())
        ImageViewCompat.setImageTintList(getViewDataBinding()!!.layoutFavourite.ivLogo, ContextCompat.getColorStateList(getContainerActivity(), R.color.white))
        hideLoader()
    }

    private fun checkFavStatus() {
        getViewModel().checkFavStatus(
            getContainerActivity(),
            getViewModel().getAppRepository().getUserId().toString(),
            eventId,
            type
        )
        getViewModel().checkStatusLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateFavStatus(it.data)
                    removeObserver(getViewModel().checkStatusMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun updateFavStatus(model: CommonModelResponse?) {
        if (model?.status == 200) {
            favSelected()
        }else{
            favNotSelected()
        }
    }

    private fun getBundleData() {
        try {
            title = requireArguments().getString(VillageConstants.TOOLBAR_TITLE, "")
            snippet = requireArguments().getString(VillageConstants.SNIPPET, "")
            type = requireArguments().getString(VillageConstants.TYPE, "")
            eventId = requireArguments().getString(VillageConstants.EVENT_ID, "")
            favouriteStatus = requireArguments().getString(VillageConstants.FAVOURITE_STATUS, "-1")
            isFromGolfSubcategoryScreen = requireArguments().getBoolean(VillageConstants.IS_FROM_GOLF_SUBCATEGORY, false)
            isFromFavouriteScreen = requireArguments().getBoolean(VillageConstants.IS_FROM_FAVOURITE, false)
            favPosition = requireArguments().getInt(VillageConstants.POSITION, -1)
            destination_latitude = requireArguments().getString(VillageConstants.DESITNATION_LATITUDE, "")
            destination_longitude = requireArguments().getString(VillageConstants.DESITNATION_LONGITUDE, "")
            detailDataModel = getViewModel().getReflectionUtils().getStringToPojo(requireArguments().getString(DETAIL_MODEL)!!,DetailDataModel::class.java)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var oneTime = true
    var latLng: LatLng? = null
    private fun fetchLocation() {
        locationPermission(object : PermissionListener {
            override fun onAccepted(lat: Any, lng: Any) {
                if (oneTime && lat != null) {
                    showLoader()
                    oneTime = false
                    latLng = LatLng(lat as Double, lng as Double)
                    isPostalCodeMatch = getPostalCodeFromLatLng(getContainerActivity(),latLng)
                    setFavStatus()
                }
            }
        })
    }

    private fun setViews() {
        getViewDataBinding()!!.apply {
            loadGlide(R.drawable.ic_simple_call, layoutCall.ivLogo, getContainerActivity())
            loadGlide(R.drawable.ic_share, layoutShare.ivLogo, getContainerActivity())
            loadGlide(R.drawable.ic_heart, layoutFavourite.ivLogo, getContainerActivity())
            layoutCall.tvTitle.text = getString(R.string.call)
            layoutShare.tvTitle.text = getString(R.string.share)
            layoutFavourite.tvTitle.text = getString(R.string.favorites)
        }
    }

    private fun initiliase() {
        getViewDataBinding()!!.mapView.getMapAsync(this@FragmentDetail)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapbox = mapboxMap
        mapboxMap.setStyle(Style.Builder().fromUrl(Style.OUTDOORS)) {
            mapboxMap.getUiSettings().setAttributionEnabled(false);
            mapboxMap.getUiSettings().setLogoEnabled(false);
            mapboxMap.uiSettings.isTiltGesturesEnabled = false
        }

        createMarker()
    }

    fun createMarker() {
        getViewDataBinding()!!.apply {
            tvTitle.setText(title)
            try {
                val marker = mapbox?.addMarker(
                    com.mapbox.mapboxsdk.annotations.MarkerOptions()
                        .position(
                            LatLng(
                                destination_latitude.toDouble(),
                                destination_longitude.toDouble()
                            )
                        )
                        .title(title)
                        .snippet(snippet)
                )
                mapbox?.selectMarker(marker!!)

                mapbox?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(destination_latitude.toDouble(), destination_longitude.toDouble()),
                        15.0
                    )
                )

                val cameraPosition = CameraPosition.Builder()
                    .target(
                        LatLng(
                            destination_latitude.toDouble(),
                            destination_longitude.toDouble()
                        )
                    ) // Sets the center of the map to location user
                    .zoom(15.0) // Sets the zoom
                    .bearing(90.0) // Sets the orientation of the camera to east
                    .tilt(40.0) // Sets the tilt of the camera to 30 degrees
                    .build() // Creates a CameraPosition from the builder


                mapbox?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            } catch (e: Exception) {
                e.printStackTrace()
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
        if (isFromGolfSubcategoryScreen!!) {
            getContainerActivity().onBackEventCallback!!.onBackTrigger(favStatus!!,favPosition)
        }
        if (isFromFavouriteScreen!!){
            getContainerActivity().onBackEventCallback!!.onBackTrigger(favStatus!!,favPosition)
        }
    }

}