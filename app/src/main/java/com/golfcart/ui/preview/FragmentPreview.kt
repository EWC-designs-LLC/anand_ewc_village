package com.golfcart.ui.preview

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.DialogErrorDoNotShowBinding
import com.golfcart.databinding.DialogGolfPathBinding
import com.golfcart.databinding.DialogRateUsBinding
import com.golfcart.databinding.DialogSubscriptionBinding
import com.golfcart.databinding.FragmentPreviewBinding
import com.golfcart.model.app_interface.PermissionListener
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.navigation.NavigationActivity
import com.golfcart.ui.subscription.SubscriptionActivity
import com.golfcart.ui.village.DialogBinding
import com.golfcart.ui.village.VillageDialog
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.API_PARAM
import com.golfcart.utils.village_constant.VillageConstants.CURRENT_LATITUDE
import com.golfcart.utils.village_constant.VillageConstants.CURRENT_LONGITUDE
import com.golfcart.utils.village_constant.VillageConstants.DESITNATION_LATITUDE
import com.golfcart.utils.village_constant.VillageConstants.DESITNATION_LONGITUDE
import com.golfcart.utils.village_constant.VillageConstants.Direction
import com.golfcart.utils.village_constant.VillageConstants.STADIAMAP_KEY
import com.golfcart.utils.village_constant.VillageConstants.STADIA_MAP_BASE_URL
import com.golfcart.utils.village_constant.VillageConstants.TYPE
import com.google.gson.Gson
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.Bearing
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.base.route.toNavigationRoutes
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import com.mapbox.navigation.core.routealternatives.NavigationRouteAlternativesObserver
import com.mapbox.navigation.core.routealternatives.RouteAlternativesError
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.NavigationStyles
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.URL
import java.text.DecimalFormat
import javax.net.ssl.HttpsURLConnection

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
class FragmentPreview : VillageFragment<FragmentPreviewBinding, PreviewViewModel>() {

    private val LAYER_ID = "road-label-navigation"
    private val API_TYPE = "POST"
    private val routeClickPadding = 30 * Resources.getSystem().displayMetrics.density
    private var mapBox: MapboxMap? = null
    private var directionBy: String = ""
    private var destLat = 0.0
    private var destLng = 0.0
    private val MAP_STYLE = "x-map-style"
    private val METERS_IN_MILE = 1609.344
    private val GEOJSON_SOURCE_ID = "line"
    private val LATITUDE = -122.486052
    private val LONGITUDE = 37.830348
    private val ZOOM = 14.0
    lateinit var directionsResponse: List<DirectionsRoute>


    /**
     * Used to execute camera transitions based on the data generated by the [viewportDataSource].
     * This includes transitions from route overview to route following and continuously updating the camera as the location changes.
     */
    private lateinit var navigationCamera: NavigationCamera

    /**
     * Produces the camera frames based on the location and routing data for the [navigationCamera] to execute.
     */
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource

    /**
     * [NavigationLocationProvider] is a utility class that helps to provide location updates generated by the Navigation SDK
     * to the Maps SDK in order to update the user location indicator on the map.
     */
    private val navigationLocationProvider = NavigationLocationProvider()

    /*
     * Below are generated camera padding values to ensure that the route fits well on screen while
     * other elements are overlaid on top of the map (including instruction view, buttons, etc.)
     */
    private val pixelDensity = Resources.getSystem().displayMetrics.density
    private val overviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            40.0 * pixelDensity,
            40.0 * pixelDensity,
            40.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeOverviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            40.0 * pixelDensity,
            40.0 * pixelDensity,
            40.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val followingPadding: EdgeInsets by lazy {
        EdgeInsets(
            40.0 * pixelDensity,
            40.0 * pixelDensity,
            40.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeFollowingPadding: EdgeInsets by lazy {
        EdgeInsets(
            40.0 * pixelDensity,
            40.0 * pixelDensity,
            40.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }

    /**
     * Generates updates for the [routeLineView] with the geometries and properties of the routes that should be drawn on the map.
     */
    private lateinit var routeLineApi: MapboxRouteLineApi

    /**
     * Draws route lines on the map based on the data from the [routeLineApi]
     */
    private lateinit var routeLineView: MapboxRouteLineView

    /**
     * Generates updates for the [routeArrowView] with the geometries and properties of maneuver arrows that should be drawn on the map.
     */
    private val routeArrowApi: MapboxRouteArrowApi = MapboxRouteArrowApi()

    /**
     * Draws maneuver arrows on the map based on the data [routeArrowApi].
     */
    private lateinit var routeArrowView: MapboxRouteArrowView

    private val mapboxNavigation: com.mapbox.navigation.core.MapboxNavigation by requireMapboxNavigation(
        onResumedObserver = object : MapboxNavigationObserver {
            @SuppressLint("MissingPermission")
            override fun onAttached(mapboxNavigation: com.mapbox.navigation.core.MapboxNavigation) {
                mapboxNavigation.registerRoutesObserver(routesObserver)
                mapboxNavigation.registerLocationObserver(locationObserver)
                mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.registerRouteAlternativesObserver(alternativesObserver)
                // start the trip session to being receiving location updates in free drive
                // and later when a route is set also receiving route progress updates
                mapboxNavigation.startTripSession()
            }

            override fun onDetached(mapboxNavigation: com.mapbox.navigation.core.MapboxNavigation) {
                mapboxNavigation.unregisterRoutesObserver(routesObserver)
                mapboxNavigation.unregisterLocationObserver(locationObserver)
                mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.unregisterRouteAlternativesObserver(alternativesObserver)
            }
        },
        onInitialize = this::initNavigation
    )

    /**
     * Gets notified with location updates.
     *
     * Exposes raw updates coming directly from the location services
     * and the updates enhanced by the Navigation SDK (cleaned up and matched to the road).
     */
    private val locationObserver = object : LocationObserver {
        var firstLocationUpdateReceived = false

        override fun onNewRawLocation(rawLocation: Location) {
            // not handled
        }

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            val enhancedLocation = locationMatcherResult.enhancedLocation
            if (latLng != null) {
                latLng!!.latitude = enhancedLocation.latitude
                latLng!!.longitude = enhancedLocation.longitude
            }
            // update location puck's position on the map
            navigationLocationProvider.changePosition(
                location = enhancedLocation,
                keyPoints = locationMatcherResult.keyPoints,
            )

            // update camera position to account for new location
            viewportDataSource.onLocationChanged(enhancedLocation)
            viewportDataSource.evaluate()

            // if this is the first location update the baseActivity has received,
            // it's best to immediately move the camera to the current user location
            if (!firstLocationUpdateReceived) {
                firstLocationUpdateReceived = true
                navigationCamera.requestNavigationCameraToOverview(
                    stateTransitionOptions = NavigationCameraTransitionOptions.Builder()
                        .maxDuration(0) // instant transition
                        .build()
                )

                navigationCamera.requestNavigationCameraToOverview()
            }
        }
    }

    /**
     * Gets notified with progress along the currently active route.
     */
    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        // update the camera position to account for the progressed fragment of the route
        viewportDataSource.onRouteProgressChanged(routeProgress)
        viewportDataSource.evaluate()

        // draw the upcoming maneuver arrow on the map
        val style = getViewDataBinding()!!.mapView.getMapboxMap().getStyle()
        if (style != null) {
            val maneuverArrowResult = routeArrowApi.addUpcomingManeuverArrow(routeProgress)
            routeArrowView.renderManeuverUpdate(style, maneuverArrowResult)
        }
    }

    /**
     * The SDK triggers [NavigationRouteAlternativesObserver] when available alternatives change.
     */
    private val alternativesObserver = object : NavigationRouteAlternativesObserver {
        override fun onRouteAlternatives(
            routeProgress: RouteProgress,
            alternatives: List<com.mapbox.navigation.base.route.NavigationRoute>,
            routerOrigin: RouterOrigin
        ) {
            // Set the suggested alternatives
            val updatedRoutes = mutableListOf<com.mapbox.navigation.base.route.NavigationRoute>()
            updatedRoutes.add(routeProgress.navigationRoute) // only primary route should persist
            updatedRoutes.addAll(alternatives) // all old alternatives should be replaced by the new ones
            mapboxNavigation.setNavigationRoutes(updatedRoutes)
        }

        override fun onRouteAlternativesError(error: RouteAlternativesError) {
            showToast(error.message)
        }
    }

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentPreview
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true,
            requireArguments().getString(VillageConstants.TOOLBAR_TITLE, ""),
            false, -1, false,
            "", false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.preview
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_preview
    }

    override fun getViewModel(): PreviewViewModel {
        val vm: PreviewViewModel by viewModel()
        return vm
    }

    /**
     * Gets notified whenever the tracked routes change.
     *
     * A change can mean:
     * - routes get changed with [MapboxNavigation.setRoutes]
     * - routes annotations get refreshed (for example, congestion annotation that indicate the live traffic along the route)
     * - driver got off route and a reroute was executed
     */
    private val routesObserver = RoutesObserver { routeUpdateResult ->
        if (routeUpdateResult.navigationRoutes.isNotEmpty()) {
            // generate route geometries asynchronously and render them
            routeLineApi.setNavigationRoutes(
                routeUpdateResult.navigationRoutes
            ) { value ->
                getViewDataBinding()!!.mapView.getMapboxMap().getStyle()?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }

            // update the camera position to account for the new route
            viewportDataSource.onRouteChanged(routeUpdateResult.navigationRoutes.first())
            viewportDataSource.evaluate()
        } else {
            // remove the route line and route arrow from the map
            val style = getViewDataBinding()!!.mapView.getMapboxMap().getStyle()
            if (style != null) {
                routeLineApi.clearRouteLine { value ->
                    routeLineView.renderClearRouteLineValue(
                        style,
                        value
                    )
                }
                routeArrowView.render(style, routeArrowApi.clearArrows())
            }

            // remove the route reference from camera position evaluations
            viewportDataSource.clearRouteData()
            viewportDataSource.evaluate()
        }
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            getbundleData()
            setview()
            setListiner()
            fetchLocation()
            initialseViewPortDataSource()
            initaliseNavigationCamera()
            initialiseRouteline()
            loadMapStyle()
//            loadPolyLine()
        }
    }

    override fun onResume() {
        super.onResume()

        val routeOptions by lazy {
            RouteOptions.builder()
                // FIXME: It is your responsibility to select the correct profile here!
                .applyDefaultNavigationOptions(getProfileCriteria())
                .applyLanguageAndVoiceUnitOptions(getContainerActivity())
                .coordinatesList(
                    listOf(
                        Point.fromLngLat(latLng!!.longitude, latLng!!.latitude),
                        Point.fromLngLat(destLng, destLat)
                    )
                )
                .alternatives(true)
                // provide the bearing for the origin of the request to ensure
                // that the returned route faces in the direction of the current user movement
                .bearingsList(
                    listOf(
                        Bearing.builder()
                            //.angle(originLocation?.bearing!!.toDouble())
                            .degrees(45.0)
                            .build(),
                        null
                    )
                )
                .layersList(listOf(mapboxNavigation.getZLevel(), null))
                .build()
        }

        lifecycleScope.launch {
            showLoader()
            directionsResponse = async { getDirection(routeOptions) }.await()
            setRouteAndStartNavigation(directionsResponse!!.toNavigationRoutes())
            navigationCamera.requestNavigationCameraToOverview()
            hideLoader()
        }
    }

    private fun setRouteAndStartNavigation(routes: List<NavigationRoute>) {
        // set routes, where the first route in the list is the primary route that
        // will be used for active guidance
        mapboxNavigation.setNavigationRoutes(routes)

        // move the camera to overview when new route is available
        navigationCamera.requestNavigationCameraToOverview()

        if (!routes.isNullOrEmpty()) {

            val totalSeconds = routes.get(0).directionsResponse.routes().get(0).duration()

            val minute = totalSeconds / 60.0

            var time =
                "${if (minute.toInt() == 0) 1 else convertMinutesToHoursAndMinutes(minute.toInt())} " + getString(
                    R.string.min
                )

            val meter = df.format(
                routes.get(0).directionsResponse.routes().get(0).distance() / METERS_IN_MILE
            )
            time = time + " (${meter ?: "0.0"} miles)"

            getViewDataBinding()!!.tvTotalTime.text = time
        }
    }

    suspend fun getDirection(routeOptions: RouteOptions): List<DirectionsRoute> {
        val locations = routeOptions.coordinatesList().map {
            mapOf("lat" to it.latitude(), "lon" to it.longitude(), "type" to "break")
        }

        val costing = when (routeOptions.profile()) {
            DirectionsCriteria.PROFILE_DRIVING -> "auto"
            DirectionsCriteria.PROFILE_CYCLING -> "golf_cart"
            else -> "auto"
        }

        val options = mapOf(
            "locations" to locations,
            "costing" to costing,
            "directions_options" to mapOf("units" to "miles"),
            "costing_options" to listOf(
                mapOf(
                    "golf_cart" to mapOf(
                        "top_speed" to 32 // 24kph ~= 15mph
                    )
                )
            ),
        )

        val jsonOptions = JSONObject(options).toString(2)
        val postBody = jsonOptions.toByteArray()

        val url =
            URL(STADIA_MAP_BASE_URL + API_PARAM + STADIAMAP_KEY)

        return withContext(Dispatchers.IO) {
            try {
                val conn = url.openConnection() as HttpsURLConnection
                conn.requestMethod = API_TYPE

                conn.doOutput = true

                conn.outputStream.use {
                    it.write(postBody)
                }

                val responseString = conn.inputStream.bufferedReader().use {
                    it.readText()
                }

                conn.disconnect()

                val routes = DirectionsResponse.fromJson(responseString, routeOptions).routes()
                routes
            } catch (e: Exception) {
                e.printStackTrace()
                listOf<DirectionsRoute>()
            }
        }
    }

    fun convertMinutesToHoursAndMinutes(totalMinutes: Int): String {
        if (totalMinutes >= 60) {
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            return "$hours hr $minutes"
        } else {
            return "$totalMinutes"
        }
    }

    private fun getProfileCriteria(): String {
        when (directionBy) {
            "1" -> {
                return DirectionsCriteria.PROFILE_CYCLING
            }

            "0" -> {
                return DirectionsCriteria.PROFILE_DRIVING
            }
        }
        return DirectionsCriteria.PROFILE_CYCLING
    }

    private fun getbundleData() {
        try {
            if (arguments != null) {
                directionBy = requireArguments().getString(VillageConstants.Direction, "1")
                destLat = requireArguments().getString(DESITNATION_LATITUDE)!!.toDouble()
                destLng = requireArguments().getString(DESITNATION_LONGITUDE)!!.toDouble()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setview() {
        getViewDataBinding()!!.apply {
            if (directionBy.contains("1")) {
                ivPreview.setImageResource(R.drawable.ic_golf_cart)
            } else {
                ivPreview.setImageResource(R.drawable.ic_car)
            }
        }

        lifecycleScope.launch {
            showLoader()
            delay(3500)
            hideLoader()
            if (directionBy.equals("1")) {
                //dialogGolfPath()
            }
        }
    }

    private fun setListiner() {
        getViewDataBinding()!!.apply {

            btnStandard.tag = 1
            btnHybrid.tag = 0

            /*standard map */
            btnStandard.setOnClickListener {
                if (btnStandard.tag != 1) {
                    btnStandard.tag = 1
                    btnHybrid.tag = 0

                    mapBox?.setStyle(Style.MAPBOX_STREETS) { style ->
                        mapBox!!.getUiSettings().setAttributionEnabled(false);
                        mapBox!!.getUiSettings().setLogoEnabled(false)
                    }

                    mapView.getMapboxMap().loadStyleUri(com.mapbox.maps.Style.MAPBOX_STREETS)
                }
            }

            /*hybrid map*/
            btnHybrid.setOnClickListener {
                if (btnHybrid.tag != 1) {

                    btnStandard.tag = 0
                    btnHybrid.tag = 1

                    mapBox?.setStyle(Style.SATELLITE_STREETS) { style ->
                        mapBox!!.getUiSettings().setAttributionEnabled(false);
                        mapBox!!.getUiSettings().setLogoEnabled(false);
                    }

                    mapView.getMapboxMap().loadStyleUri(com.mapbox.maps.Style.SATELLITE_STREETS)
                }
            }

            btnStart.setOnClickListener {
                if (directionBy.contains("1") && !getViewModel().getAppRepository().getNavigationInstruction()) {  // 1 means its a golf cart
                    showNavigationCheckboxDialog()
                } else {
                    navigateToNavigation()
                }
            }
        }
    }
    private fun showNavigationCheckboxDialog() {

        VillageDialog<DialogErrorDoNotShowBinding>(
            getContainerActivity(), R.layout.dialog_error_do_not_show,
            object : DialogBinding<DialogErrorDoNotShowBinding> {

                override fun onBind(binder: DialogErrorDoNotShowBinding, dialog: Dialog) {
                    binder.apply {
                        btnOk.setOnClickListener {
                            if (cbAgain.isChecked) {
                                getViewModel().getAppRepository().setNavigationInstruction(true)
                            } else {
                                getViewModel().getAppRepository().setNavigationInstruction(false)
                            }
                            dialog.dismiss()
                            navigateToNavigation()
                        }

                        ivCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                    }
                }
            }, true, true
        ).show(getContainerActivity().supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    private fun navigateToNavigation() {
        saveToSharedPreference()
        val intent = Intent(getContainerActivity(), NavigationActivity::class.java)
        intent.apply {
            putExtra(CURRENT_LATITUDE, latLng?.latitude)
            putExtra(CURRENT_LONGITUDE, latLng?.longitude)
            putExtra(DESITNATION_LATITUDE, destLat)
            putExtra(DESITNATION_LONGITUDE, destLng)
            putExtra(MAP_STYLE, getMapStyle())
            putExtra(Direction, directionBy)
        }
        startActivity(intent)
    }

    private fun getMapStyle(): String? {
        getViewDataBinding()!!.apply {
            if (btnStandard.tag == 1) {
                return "standard"
            } else {
                return "hybrid"
            }
        }
    }

    private fun saveToSharedPreference() {
        if (getViewModel().getAppRepository().getAppRateCount() > 8) {
            getViewModel().getAppRepository().setAppRateCount(9)
        } else {
            var count = getViewModel().getAppRepository().getAppRateCount()
            count += 1
            getViewModel().getAppRepository().setAppRateCount(count)
        }
    }

    private fun dialogGolfPath() {
        VillageDialog<DialogGolfPathBinding>(
            getContainerActivity(), R.layout.dialog_golf_path,
            object : DialogBinding<DialogGolfPathBinding> {

                override fun onBind(binder: DialogGolfPathBinding, dialog: Dialog) {
                    binder.apply {
                        btnOk.setOnClickListener {
                            dialog.dismiss()
                        }
                    }
                }
            }, false, true
        ).show(getContainerActivity().supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    var oneTime = true
    var latLng: LatLng? = null
    fun fetchLocation() {
        locationPermission(object : PermissionListener {
            override fun onAccepted(lat: Any, lng: Any) {
                oneTime = false
                latLng = LatLng(lat as Double, lng as Double)
            }
        })
    }

    private fun initialseViewPortDataSource() {
        viewportDataSource =
            MapboxNavigationViewportDataSource(getViewDataBinding()!!.mapView.getMapboxMap())
    }

    private fun initaliseNavigationCamera() {
        // initialize Navigation Camera

        navigationCamera = NavigationCamera(
            getViewDataBinding()!!.mapView.getMapboxMap(),
            getViewDataBinding()!!.mapView.camera,
            viewportDataSource
        )
        // set the animations lifecycle listener to ensure the NavigationCamera stops
        // automatically following the user location when the map is interacted with
        getViewDataBinding()!!.mapView.camera.addCameraAnimationsLifecycleListener(
            NavigationBasicGesturesHandler(navigationCamera)
        )
        navigationCamera.registerNavigationCameraStateChangeObserver { navigationCameraState ->

        }
        // set the padding values depending on screen orientation and visible view layout
        if (getContainerActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewportDataSource.overviewPadding = landscapeOverviewPadding
        } else {
            viewportDataSource.overviewPadding = overviewPadding
        }
        if (getContainerActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewportDataSource.followingPadding = landscapeFollowingPadding
        } else {
            viewportDataSource.followingPadding = followingPadding
        }
    }

    private fun initialiseRouteline() {
        val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(getContainerActivity())
            .withRouteLineBelowLayerId(LAYER_ID)
            .build()
        routeLineApi = MapboxRouteLineApi(mapboxRouteLineOptions)
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        // initialize maneuver arrow view to draw arrows on the map
        val routeArrowOptions = RouteArrowOptions.Builder(getContainerActivity()).build()
        routeArrowView = MapboxRouteArrowView(routeArrowOptions)
    }

    private fun loadMapStyle() {
        getViewDataBinding()!!.mapView.getMapboxMap()
            .loadStyleUri(NavigationStyles.NAVIGATION_DAY_STYLE) {
                getViewDataBinding()!!.mapView.gestures.addOnMapLongClickListener { point ->
                    true
                }
            }
        getViewDataBinding()!!.mapView.gestures.addOnMapClickListener(mapClickListener)
    }

    private val mapClickListener = OnMapClickListener { point ->
        lifecycleScope.launch {
            routeLineApi.findClosestRoute(
                point,
                getViewDataBinding()!!.mapView.getMapboxMap(),
                routeClickPadding
            ) {
                val routeFound = it.value?.navigationRoute
                // if we clicked on some route that is not primary,
                // we make this route primary and all the others - alternative
                if (routeFound != null && routeFound != routeLineApi.getPrimaryNavigationRoute()) {
                    val reOrderedRoutes = routeLineApi.getNavigationRoutes()
                        .filter { navigationRoute -> navigationRoute != routeFound }
                        .toMutableList()
                        .also { list ->
                            list.add(0, routeFound)
                        }
                    mapboxNavigation.setNavigationRoutes(reOrderedRoutes)
                }
            }
        }
        false
    }

    private fun initNavigation() {
        MapboxNavigationApp.setup(
            NavigationOptions.Builder(getContainerActivity())
                .accessToken(getString(R.string.mapbox_access_token))
                .build()
        )

        // initialize location puck
        Handler().postDelayed({
            getViewDataBinding()!!.mapView.location.apply {
                setLocationProvider(navigationLocationProvider)
                this.locationPuck = LocationPuck2D(
                    bearingImage = ContextCompat.getDrawable(
                        getContainerActivity(),
                        R.drawable.mapbox_navigation_puck_icon
                    )
                )
                enabled = true
            }
        }, 1500)
    }

    private fun loadPolyLine() {
        getViewDataBinding()!!.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder().center(Point.fromLngLat(LATITUDE, LONGITUDE)).zoom(ZOOM).build()
        )

        getViewDataBinding()!!.mapView.getMapboxMap().loadStyle(
            (
                    style(styleUri = Style.MAPBOX_STREETS) {
                        +geoJsonSource(GEOJSON_SOURCE_ID) {
                            url("asset://not_allowed_path.txt")
                        }
                        +lineLayer("linelayer", GEOJSON_SOURCE_ID) {
                            lineCap(LineCap.ROUND)
                            lineJoin(LineJoin.ROUND)
                            lineOpacity(0.7)
                            lineWidth(5.0)
                            lineColor("#ff0000")
                        }
                    }
                    )
        )
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
        //maneuverApi.cancel()
        routeLineApi.cancel()
        routeLineView.cancel()
    }
}