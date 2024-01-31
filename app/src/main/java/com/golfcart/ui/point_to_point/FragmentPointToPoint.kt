package com.golfcart.ui.point_to_point

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.DialogSortbyBinding
import com.golfcart.databinding.FragmentPointToPointBinding
import com.golfcart.model.app_interface.EventsInterface
import com.golfcart.model.app_interface.PermissionListener
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.more.FragmentMore
import com.golfcart.ui.search.FragmentSearch
import com.golfcart.ui.village.BottomSheetBinding
import com.golfcart.ui.village.VillageActivity.Companion.point_des_address
import com.golfcart.ui.village.VillageActivity.Companion.point_des_lat
import com.golfcart.ui.village.VillageActivity.Companion.point_des_lng
import com.golfcart.ui.village.VillageActivity.Companion.point_souce_lat
import com.golfcart.ui.village.VillageActivity.Companion.point_souce_lng
import com.golfcart.ui.village.VillageActivity.Companion.point_source_address
import com.golfcart.ui.village.VillageBottomSheet
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.IS_FROM_POINT_TO_POINT
import com.golfcart.utils.village_constant.VillageConstants.IS_POINT_SEARCH
import com.golfcart.utils.village_constant.VillageConstants.POINT_DESTINATION
import com.golfcart.utils.village_constant.VillageConstants.POINT_SOURCE
import com.golfcart.utils.village_constant.VillageConstants.POINT_TYPE
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_LAT_START
import com.golfcart.utils.village_constant.VillageConstants.VILLAGE_LNG_START
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.Bearing
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class FragmentPointToPoint : VillageFragment<FragmentPointToPointBinding, PointViewModel>(),
    EventsInterface {

    private val LAYER_ID = "road-label-navigation"
    private val API_TYPE = "POST"
    private val routeClickPadding = 30 * Resources.getSystem().displayMetrics.density
    private var directionBy: String = ""
    private val METERS_IN_MILE = 1609.344
    private var isFromSearch = false
    private var pointType = "1"

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
            30.0 * pixelDensity,
            40.0 * pixelDensity,
            30.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeOverviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            20.0 * pixelDensity
        )
    }
    private val followingPadding: EdgeInsets by lazy {
        EdgeInsets(
            180.0 * pixelDensity,
            40.0 * pixelDensity,
            150.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeFollowingPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
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

    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    private val mapboxNavigation: com.mapbox.navigation.core.MapboxNavigation by requireMapboxNavigation(
        onResumedObserver = object : MapboxNavigationObserver {
            @SuppressLint("MissingPermission")
            override fun onAttached(mapboxNavigation: com.mapbox.navigation.core.MapboxNavigation) {
                mapboxNavigation.registerRoutesObserver(routesObserver)
                mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.registerRouteAlternativesObserver(alternativesObserver)
                // start the trip session to being receiving location updates in free drive
                // and later when a route is set also receiving route progress updates
                mapboxNavigation.startTripSession()
            }

            override fun onDetached(mapboxNavigation: com.mapbox.navigation.core.MapboxNavigation) {
                mapboxNavigation.unregisterRoutesObserver(routesObserver)
                mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.unregisterRouteAlternativesObserver(alternativesObserver)
            }
        },
        onInitialize = this::initNavigation
    )

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
        return this@FragmentPointToPoint
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true,
            getString(R.string.point_to_point),
            false, -1, false,
            "", false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.point
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_point_to_point
    }

    override fun getViewModel(): PointViewModel {
        val vm: PointViewModel by viewModel()
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

    var one = true
    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getbundleData()
        setListiner()
        fetchLocation()
        initialseViewPortDataSource()
        initaliseNavigationCamera()
        initialiseRouteline()
        loadMapStyle()
        hideLoader()
        loadMarker()
    }

    private fun loadMarker() {
        if (isFromSearch) {
            if (pointType.equals("1")) {
                if (point_souce_lat != 0.0 || point_souce_lng != 0.0) {
                    setMapStyle(point_souce_lat, point_souce_lng)
                    return
                }
            } else {
                if (point_des_lat != 0.0 || point_des_lng != 0.0) {
                    setMapStyle(point_des_lat, point_des_lng)
                    return
                }
            }
        }
        setCameraBounds()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun findRouteByStadiaMap() {
        val routeOptions by lazy {
            RouteOptions.builder()
                .applyDefaultNavigationOptions(getProfileCriteria())
                .coordinatesList(
                    listOf(
                        Point.fromLngLat(point_souce_lng, point_souce_lat),
                        Point.fromLngLat(point_des_lng, point_des_lat)
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
            val response = async { getDirection(routeOptions) }
            setRouteAndStartNavigation(response.await().toNavigationRoutes())
            hideLoader()
        }
    }

    private fun setRouteAndStartNavigation(routes: List<NavigationRoute>) {
        lifecycleScope.launch {
            // set routes, where the first route in the list is the primary route that
            // will be used for active guidance

            if (!routes.isNullOrEmpty()) {
                showLoader()
                mapboxNavigation.setNavigationRoutes(routes)
                navigationCamera.requestNavigationCameraToOverview()
                setview()
                setSpeed(routes)
                Handler().postDelayed(Runnable {
                    hideLoader()
                }, 4000)
            }
        }
    }

    private fun setSpeed(routes: List<NavigationRoute>) {
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

    private fun setCameraBounds() {
        val targetPosition = Point.fromLngLat(latLng?.longitude ?: VILLAGE_LNG_START,latLng?.latitude ?: VILLAGE_LAT_START)
        // Set up the CameraOptions with the desired parameters
        val cameraOptions: CameraOptions = CameraOptions.Builder()
            .center(targetPosition)
            .zoom(16.0) // Specify the zoom level
            .build()

        // Move the camera to the specified position
        getViewDataBinding()!!.mapView.getMapboxMap().setCamera(cameraOptions)
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
            URL(VillageConstants.STADIA_MAP_BASE_URL + VillageConstants.API_PARAM + VillageConstants.STADIAMAP_KEY)

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
                showToast("No route for ${getProfileType()}")
                listOf<DirectionsRoute>()
            }
        }
    }

    fun getProfileType(): String {
        when (directionBy) {
            "1" -> {
                return "Golf Cart"
            }

            "0" -> {
                return "Auto"
            }
        }
        return "Golf Cart"
    }

    private fun convertMinutesToHoursAndMinutes(totalMinutes: Int): String {
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
        return DirectionsCriteria.PROFILE_DRIVING
    }

    private fun getbundleData() {
        getViewDataBinding()!!.apply {
            try {
                if (arguments != null) {
                    isFromSearch = requireArguments().getBoolean(IS_POINT_SEARCH, false)
                    pointType = requireArguments().getString(VillageConstants.POINT_TYPE, "1")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (isFromSearch) {
                if (!point_source_address.isNullOrBlank()) {
                    tvSource.text = point_source_address
                }
                if (!point_des_address.isNullOrBlank()) {
                    tvDestination.text = point_des_address
                }
            }
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
    }

    private fun setListiner() {
        getViewDataBinding()!!.apply {
            getContainerActivity().onEventListener = this@FragmentPointToPoint
            tvSource.setOnClickListener {
                val bundle = Bundle()
                bundle.putBoolean(IS_FROM_POINT_TO_POINT, true)
                bundle.putString(POINT_TYPE, POINT_SOURCE)
                replaceFragment(setArguments(FragmentSearch(), bundle), true, true)
            }

            tvDestination.setOnClickListener {
                val bundle = Bundle()
                bundle.putBoolean(IS_FROM_POINT_TO_POINT, true)
                bundle.putString(POINT_TYPE, POINT_DESTINATION)
                replaceFragment(setArguments(FragmentSearch(), bundle), true, true)
            }

            btnStart.setOnClickListener {
                if (point_souce_lat == 0.0 || point_souce_lng == 0.0) {
                    showErrorDialog("Please fill the source address.")
                } else if (point_des_lat == 0.0 || point_des_lng == 0.0) {
                    showErrorDialog("Please fill the destination address.")
                } else {
                    bottomSheetDirection()
                }

            }
        }
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
                enabled = false
            }
        }, 1500)
    }

    private fun setMapStyle(lat: Double, lng: Double) {
        getViewDataBinding()!!.apply {
            mapView.getMapboxMap().loadStyleUri(
                com.mapbox.maps.Style.MAPBOX_STREETS,
                object : com.mapbox.maps.Style.OnStyleLoaded {
                    override fun onStyleLoaded(style: com.mapbox.maps.Style) {
                        addAnnotationToMap(lat, lng)
                    }
                }
            )
        }
    }

    private fun addAnnotationToMap(lat: Double, lng: Double) {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            getContainerActivity(),
            R.drawable.map_default_map_marker
        )?.let {

            val annotationApi = getViewDataBinding()!!.mapView.annotations
            val pointAnnotationManager =
                annotationApi.createPointAnnotationManager(getViewDataBinding()!!.mapView)
// Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
// Define a geographic coordinate.
                .withPoint(Point.fromLngLat(lng, lat))
                .withIconImage(it)
// Add the resulting pointAnnotation to the map.
            pointAnnotationManager.create(pointAnnotationOptions)
        }

        updateCamera(Point.fromLngLat(lng, lat))
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun updateCamera(point: Point) {
        val mapAnimationOptions = MapAnimationOptions.Builder().build()
        getViewDataBinding()!!.mapView.camera.easeTo(
            CameraOptions.Builder()
                .center(point)
                .zoom(19.0)
                .build(),
            mapAnimationOptions
        )
    }

    private fun bottomSheetDirection() {
        VillageBottomSheet<DialogSortbyBinding>(getContainerActivity(),
            R.layout.dialog_sortby,
            object : BottomSheetBinding<DialogSortbyBinding> {
                override fun onBind(binder: DialogSortbyBinding, dialog: Dialog) {
                    binder.apply {
                        tvDistance.text = "View by Golf Cart"
                        tvAlphabetically.text = "View by Automobile"

                        tvCancel.setOnClickListener {
                            dialog.dismiss()
                        }

                        tvDistance.setOnClickListener {
                            directionBy = "1"
                            findRouteByStadiaMap()
                            dialog.dismiss()
                        }

                        tvAlphabetically.setOnClickListener {
                            directionBy = "0"
                            findRouteByStadiaMap()
                            dialog.dismiss()
                        }
                    }
                }
            }).show()
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

    override fun onEventClick() {
        replaceFragment(FragmentMore(), true, false)
    }
}