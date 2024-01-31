package com.golfcart.ui.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.ActivityNewNavigationBinding
import com.golfcart.databinding.ActivitySplashBinding
import com.golfcart.ui.base.BaseActivity
import com.golfcart.utils.Logs.VillageLogs
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.API_PARAM
import com.golfcart.utils.village_constant.VillageConstants.CURRENT_LATITUDE
import com.golfcart.utils.village_constant.VillageConstants.CURRENT_LONGITUDE
import com.golfcart.utils.village_constant.VillageConstants.DESITNATION_LATITUDE
import com.golfcart.utils.village_constant.VillageConstants.DESITNATION_LONGITUDE
import com.golfcart.utils.village_constant.VillageConstants.Direction
import com.golfcart.utils.village_constant.VillageConstants.STADIAMAP_KEY
import com.golfcart.utils.village_constant.VillageConstants.STADIA_MAP_BASE_URL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.Bearing
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.bindgen.Expected
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.TimeFormat
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.base.route.toNavigationRoutes
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import com.mapbox.navigation.core.reroute.NavigationRerouteController
import com.mapbox.navigation.core.reroute.RerouteController
import com.mapbox.navigation.core.reroute.RerouteState
import com.mapbox.navigation.core.routealternatives.NavigationRouteAlternativesObserver
import com.mapbox.navigation.core.routealternatives.RouteAlternativesError
import com.mapbox.navigation.core.routerefresh.RouteRefreshStateResult
import com.mapbox.navigation.core.routerefresh.RouteRefreshStatesObserver
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.ui.maneuver.model.ManeuverExitOptions
import com.mapbox.navigation.ui.maneuver.model.ManeuverPrimaryOptions
import com.mapbox.navigation.ui.maneuver.model.ManeuverSecondaryOptions
import com.mapbox.navigation.ui.maneuver.model.ManeuverSubOptions
import com.mapbox.navigation.ui.maneuver.model.ManeuverViewOptions
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView
import com.mapbox.navigation.ui.maps.NavigationStyles
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import com.mapbox.navigation.ui.speedlimit.api.MapboxSpeedLimitApi
import com.mapbox.navigation.ui.speedlimit.model.SpeedLimitFormatter
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.tripprogress.model.DistanceRemainingFormatter
import com.mapbox.navigation.ui.tripprogress.model.EstimatedTimeToArrivalFormatter
import com.mapbox.navigation.ui.tripprogress.model.PercentDistanceTraveledFormatter
import com.mapbox.navigation.ui.tripprogress.model.TimeRemainingFormatter
import com.mapbox.navigation.ui.tripprogress.model.TripProgressUpdateFormatter
import com.mapbox.navigation.ui.tripprogress.model.TripProgressViewOptions
import com.mapbox.navigation.ui.tripprogress.view.MapboxTripProgressView
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement
import com.mapbox.navigation.ui.voice.model.SpeechError
import com.mapbox.navigation.ui.voice.model.SpeechValue
import com.mapbox.navigation.ui.voice.model.SpeechVolume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.URL
import java.util.HashSet
import java.util.Locale
import javax.net.ssl.HttpsURLConnection

class NavigationActivity : BaseActivity<ActivityNewNavigationBinding, NavigationViewModel>() {

    private var binding: ActivityNewNavigationBinding? = null
    private var directionBy: String = ""
    private var mapStyle: String? = null
    private var current_latitude = 0.0
    private var current_longitude = 0.0
    private var destination_latitude = 0.0
    private var destination_longitude = 0.0
    private val layer_id = "road-label-navigation"
    private var isOverviewEnable = true
    private val MAP_STYLE = "x-map-style"
    private val BUTTON_ANIMATION_DURATION = 1500L
    private lateinit var directionsResponse: Deferred<List<DirectionsRoute>>

    companion object {
        private var timestamp: Long = 0
    }

    val routeOptions by lazy {
        RouteOptions.builder()
            .applyDefaultNavigationOptions(getProfileCriteria())
            .applyLanguageAndVoiceUnitOptions(this@NavigationActivity)
            .coordinatesList(
                listOf(
                    Point.fromLngLat(current_longitude, current_latitude),
                    Point.fromLngLat(destination_longitude, destination_latitude)
                )
            )
            .alternatives(true)
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

    /**
     * The SDK triggers [NavigationRouteAlternativesObserver] when available alternatives change.
     */
    private val alternativesObserver = object : NavigationRouteAlternativesObserver {
        override fun onRouteAlternatives(
            routeProgress: RouteProgress,
            alternatives: List<NavigationRoute>,
            routerOrigin: RouterOrigin
        ) {
            // Set the suggested alternatives
            val updatedRoutes = mutableListOf<NavigationRoute>()
            updatedRoutes.add(routeProgress.navigationRoute) // only primary route should persist
            updatedRoutes.addAll(alternatives) // all old alternatives should be replaced by the new ones
            mapboxNavigation.setNavigationRoutes(updatedRoutes)
        }

        override fun onRouteAlternativesError(error: RouteAlternativesError) {
            // no impl
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

    /**
     * Used to execute camera transitions based on the data generated by the [viewportDataSource].
     * This includes transitions from route overview to route following and continuously updating the camera as the location changes.
     */
    private lateinit var navigationCamera: NavigationCamera

    /**
     * Produces the camera frames based on the location and routing data for the [navigationCamera] to execute.
     */
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource


    // Define speed limit formatter options
    private val speedLimitFormatter: SpeedLimitFormatter by lazy {
        SpeedLimitFormatter(this)
    }

    // Create an instance of the Speed Limit API
    private val speedLimitApi: MapboxSpeedLimitApi by lazy {
        MapboxSpeedLimitApi(speedLimitFormatter)
    }

    /*
     * Below are generated camera padding values to ensure that the route fits well on screen while
     * other elements are overlaid on top of the map (including instruction view, buttons, etc.)
     */
    private val pixelDensity = Resources.getSystem().displayMetrics.density
    private val overviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            140.0 * pixelDensity,
            40.0 * pixelDensity,
            120.0 * pixelDensity,
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
     * Generates updates for the [MapboxManeuverView] to display the upcoming maneuver instructions
     * and remaining distance to the maneuver point.
     */
    private lateinit var maneuverApi: MapboxManeuverApi

    /**
     * Generates updates for the [MapboxTripProgressView] that include remaining time and distance to the destination.
     */
    private lateinit var tripProgressApi: MapboxTripProgressApi

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

    /**
     * Stores and updates the state of whether the voice instructions should be played as they come or muted.
     */
    private var isVoiceInstructionsMuted = false
        set(value) {
            field = value
            if (value) {
                binding?.soundButton?.muteAndExtend(BUTTON_ANIMATION_DURATION)
                voiceInstructionsPlayer.volume(SpeechVolume(0f))
            } else {
                binding?.soundButton?.unmuteAndExtend(BUTTON_ANIMATION_DURATION)
                voiceInstructionsPlayer.volume(SpeechVolume(1f))
            }
        }

    /**
     * Extracts message that should be communicated to the driver about the upcoming maneuver.
     * When possible, downloads a synthesized audio file that can be played back to the driver.
     */
    private lateinit var speechApi: MapboxSpeechApi

    /**
     * Plays the synthesized audio files with upcoming maneuver instructions
     * or uses an on-device Text-To-Speech engine to communicate the message to the driver.
     */
    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer

    /**
     * Observes when a new voice instruction should be played.
     */
    private val voiceInstructionsObserver = VoiceInstructionsObserver { voiceInstructions ->
        speechApi.generate(voiceInstructions, speechCallback)
    }

    /**
     * Based on whether the synthesized audio file is available, the callback plays the file
     * or uses the fall back which is played back using the on-device Text-To-Speech engine.
     */
    private val speechCallback =
        MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> { expected ->
            expected.fold(
                { error ->
                    // play the instruction via fallback text-to-speech engine
                    voiceInstructionsPlayer.play(
                        error.fallback,
                        voiceInstructionsPlayerCallback
                    )
                },
                { value ->
                    // play the sound file from the external generator
                    voiceInstructionsPlayer.play(
                        value.announcement,
                        voiceInstructionsPlayerCallback
                    )
                }
            )
        }

    /**
     * When a synthesized audio file was downloaded, this callback cleans up the disk after it was played.
     */
    private val voiceInstructionsPlayerCallback =
        MapboxNavigationConsumer<SpeechAnnouncement> { value ->
            // remove already consumed file to free-up space
            speechApi.clean(value)
        }

    /**
     * [NavigationLocationProvider] is a utility class that helps to provide location updates generated by the Navigation SDK
     * to the Maps SDK in order to update the user location indicator on the map.
     */
    private val navigationLocationProvider = NavigationLocationProvider()

    /**
     * Gets notified with location updates.
     *
     * Exposes raw updates coming directly from the location services
     * and the updates enhanced by the Navigation SDK (cleaned up and matched to the road).
     */
    var isUserStandingIdle: Boolean = false
    private val locationObserver = object : LocationObserver {
        var firstLocationUpdateReceived = false

        override fun onNewRawLocation(rawLocation: Location) {
            // not handled
        }

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            val enhancedLocation = locationMatcherResult.enhancedLocation
            // update location puck's position on the map
            navigationLocationProvider.changePosition(
                location = enhancedLocation,
                keyPoints = locationMatcherResult.keyPoints,
            )

            // update camera position to account for new location
            viewportDataSource.onLocationChanged(enhancedLocation)
            viewportDataSource.evaluate()

            // if this is the first location update the activity has received,
            // it's best to immediately move the camera to the current user location
            if (!firstLocationUpdateReceived) {
                firstLocationUpdateReceived = true
                navigationCamera.requestNavigationCameraToOverview(
                    stateTransitionOptions = NavigationCameraTransitionOptions.Builder()
                        .maxDuration(0) // instant transition
                        .build()
                )
            }

            val value = speedLimitApi.updateSpeedLimit(locationMatcherResult.speedLimit)
            binding?.speedLimitView?.render(value)

            getSpeed(enhancedLocation)
            if (isOverviewEnable) {
                updateCamera(
                    Point.fromLngLat(
                        enhancedLocation.longitude,
                        enhancedLocation.latitude
                    ), enhancedLocation.bearing.toDouble()
                )
            }
        }
    }

    private fun updateCamera(point: Point, bearing: Double? = null) {
        val mapAnimationOptions = MapAnimationOptions.Builder().duration(1500L).build()
        binding?.mapView?.camera?.easeTo(
            CameraOptions.Builder()
                .center(point)
                .bearing(bearing)
                .zoom(17.0)
                .pitch(45.0)
                .padding(EdgeInsets(1000.0, 0.0, 300.0, 0.0))
                .build(),
            mapAnimationOptions
        )
    }

    /**
     * Gets notified with progress along the currently active route.
     */
    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        // update the camera position to account for the progressed fragment of the route
        viewportDataSource.onRouteProgressChanged(routeProgress)
        viewportDataSource.evaluate()

        // draw the upcoming maneuver arrow on the map
        val style = binding?.mapView?.getMapboxMap()?.getStyle()
        if (style != null) {
            val maneuverArrowResult = routeArrowApi.addUpcomingManeuverArrow(routeProgress)
            routeArrowView.renderManeuverUpdate(style, maneuverArrowResult)
        }

        // update top banner with maneuver instructions
        val maneuvers = maneuverApi.getManeuvers(routeProgress)
        maneuvers.fold(
            { error ->
                VillageLogs.printLog(packageName, error.errorMessage!!)
            },
            {
                binding?.maneuverView?.visibility = View.VISIBLE
                binding?.maneuverView?.renderManeuvers(maneuvers)!!
            }
        )

        // update bottom trip progress summary
        binding?.tripProgressView?.render(
            tripProgressApi.getTripProgress(routeProgress)
        )

        routeLineApi.updateWithRouteProgress(routeProgress) { result ->
            binding?.mapView?.getMapboxMap()?.getStyle()?.apply {
                routeLineView.renderRouteLineUpdate(this, result)
            }
        }
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
                binding?.mapView!!.getMapboxMap().getStyle()?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }

            // update the camera position to account for the new route
            viewportDataSource.onRouteChanged(routeUpdateResult.navigationRoutes.first())
            viewportDataSource.evaluate()
        } else {
            // remove the route line and route arrow from the map
            val style = binding!!.mapView.getMapboxMap().getStyle()
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

    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    private val mapboxNavigation: MapboxNavigation by requireMapboxNavigation(
        onResumedObserver = object : MapboxNavigationObserver {
            @SuppressLint("MissingPermission")
            override fun onAttached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.registerRoutesObserver(routesObserver)
                mapboxNavigation.registerLocationObserver(locationObserver)
                mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.registerRouteAlternativesObserver(alternativesObserver)
                mapboxNavigation.registerVoiceInstructionsObserver(voiceInstructionsObserver)
                // start the trip session to being receiving location updates in free drive
                // and later when a route is set also receiving route progress updates
                mapboxNavigation.startTripSession()
            }

            override fun onDetached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.unregisterRoutesObserver(routesObserver)
                mapboxNavigation.unregisterLocationObserver(locationObserver)
                mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.unregisterVoiceInstructionsObserver(voiceInstructionsObserver)
                mapboxNavigation.unregisterRouteAlternativesObserver(alternativesObserver)
            }
        },
        onInitialize = this::initNavigation
    )


    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun getCurrentRunningActivity(): AppCompatActivity {
        return this@NavigationActivity
    }

    override fun getViewModel(): NavigationViewModel {
        val vm: NavigationViewModel by viewModel()
        return vm
    }

    override fun getBindingVariable(): Int {
        return BR.new_navigation
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keepScreenOn()
        performBinding()
        getBundleData()
        screenNotSleep()
        handleTripProgressView()
        initialseViewPortDataSource()
        initaliseNavigationCamera()
        initialiseRouteline()
        loadInstruments()
        loadMapStyle()
        setListiner()
        setManeuverColor()
        setMapboxRouteandArrowOptions()
        launchStadiaMapApi()
    }

    private fun launchStadiaMapApi() {
        lifecycleScope.launch {
            delay(3000)
            findRouteWithStadiaMap()
        }
    }

    private fun setListiner() {
        binding!!.apply {
            // initialize view interactions
            stop.setOnClickListener {
                clearRouteAndStopNavigation()
            }
            recenter.setOnClickListener {
                isOverviewEnable = true
                navigationCamera.requestNavigationCameraToFollowing()
                binding!!.routeOverview.showTextAndExtend(BUTTON_ANIMATION_DURATION)
            }
            routeOverview.setOnClickListener {
                isOverviewEnable = false
                navigationCamera.requestNavigationCameraToOverview()
                binding!!.recenter.showTextAndExtend(BUTTON_ANIMATION_DURATION)
            }
            soundButton.setOnClickListener {
                // mute/unmute voice instructions
                isVoiceInstructionsMuted = !isVoiceInstructionsMuted
            }
            stop.setOnClickListener {
                clearRouteAndStopNavigation()
                finish()
            }

            // set initial sounds button state
            soundButton.unmute()
        }
    }

    private fun loadMapStyle() {
        binding!!.apply {
            // load map style
            mapView.getMapboxMap().loadStyleUri(NavigationStyles.NAVIGATION_DAY_STYLE) {
                // add long click listener that search for a route to the clicked destination
                mapView.gestures.addOnMapLongClickListener { point ->
//                findRoute(point)
                    true
                }
            }
        }
    }

    private fun loadInstruments() {

        // make sure to use the same DistanceFormatterOptions across different features
        val distanceFormatterOptions = DistanceFormatterOptions.Builder(this).build()

        // initialize maneuver api that feeds the data to the top banner maneuver view
        maneuverApi = MapboxManeuverApi(
            MapboxDistanceFormatter(distanceFormatterOptions)
        )

        // initialize bottom progress view
        tripProgressApi = MapboxTripProgressApi(
            TripProgressUpdateFormatter.Builder(this)
                .distanceRemainingFormatter(
                    DistanceRemainingFormatter(distanceFormatterOptions)
                )
                .timeRemainingFormatter(
                    TimeRemainingFormatter(this)
                )
                .percentRouteTraveledFormatter(
                    PercentDistanceTraveledFormatter()
                )
                .estimatedTimeToArrivalFormatter(
                    EstimatedTimeToArrivalFormatter(this, TimeFormat.NONE_SPECIFIED)
                )
                .build()
        )

        // initialize voice instructions api and the voice instruction player
        speechApi = MapboxSpeechApi(
            this@NavigationActivity,
            getString(R.string.mapbox_access_token),
            Locale.US.language
        )
        voiceInstructionsPlayer = MapboxVoiceInstructionsPlayer(
            this@NavigationActivity,
            getString(R.string.mapbox_access_token),
            Locale.US.language
        )
    }

    private fun initialiseRouteline() {
        // initialize route line, the withRouteLineBelowLayerId is specified to place
        // the route line below road labels layer on the map
        // the value of this option will depend on the style that you are using
        // and under which layer the route line should be placed on the map layers stack
        val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(this)
            .withRouteLineBelowLayerId("road-label-navigation")
            .build()
        routeLineApi = MapboxRouteLineApi(mapboxRouteLineOptions)
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        // initialize maneuver arrow view to draw arrows on the map
        val routeArrowOptions = RouteArrowOptions.Builder(this).build()
        routeArrowView = MapboxRouteArrowView(routeArrowOptions)
    }

    private fun initaliseNavigationCamera() {
        binding!!.apply {
            // initialize Navigation Camera
            navigationCamera = NavigationCamera(
                mapView.getMapboxMap(),
                mapView.camera,
                viewportDataSource
            )
            // set the animations lifecycle listener to ensure the NavigationCamera stops
            // automatically following the user location when the map is interacted with
            mapView.camera.addCameraAnimationsLifecycleListener(
                NavigationBasicGesturesHandler(navigationCamera)
            )
            navigationCamera.registerNavigationCameraStateChangeObserver { navigationCameraState ->
                // shows/hide the recenter button depending on the camera state
                when (navigationCameraState) {
                    NavigationCameraState.TRANSITION_TO_FOLLOWING,
                    NavigationCameraState.FOLLOWING -> recenter.visibility =
                        View.INVISIBLE

                    NavigationCameraState.TRANSITION_TO_OVERVIEW,
                    NavigationCameraState.OVERVIEW,
                    NavigationCameraState.IDLE -> recenter.visibility = View.VISIBLE
                }
            }
            // set the padding values depending on screen orientation and visible view layout
            if (this@NavigationActivity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                viewportDataSource.overviewPadding = landscapeOverviewPadding
            } else {
                viewportDataSource.overviewPadding = overviewPadding
            }
            if (this@NavigationActivity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                viewportDataSource.followingPadding = landscapeFollowingPadding
            } else {
                viewportDataSource.followingPadding = followingPadding
            }
        }
    }

    private fun initialseViewPortDataSource() {
        viewportDataSource = MapboxNavigationViewportDataSource(binding!!.mapView.getMapboxMap())
    }

    private fun keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun performBinding() {
        binding =
            DataBindingUtil.setContentView(
                this@NavigationActivity,
                R.layout.activity_new_navigation
            )
        binding?.executePendingBindings()
    }

    private fun getBundleData() {
        val bundle = intent.extras
        directionBy = bundle!!.getString(Direction, "")
        mapStyle = bundle.getString(MAP_STYLE)
        current_latitude = intent.extras!!.getDouble(CURRENT_LATITUDE, 0.0)
        current_longitude = intent.extras!!.getDouble(CURRENT_LONGITUDE, 0.0)
        destination_latitude = intent.extras!!.getDouble(DESITNATION_LATITUDE, 0.0)
        destination_longitude = intent.extras!!.getDouble(DESITNATION_LONGITUDE, 0.0)
    }

    private fun setManeuverColor() {
        val maneuverViewOptions = ManeuverViewOptions.Builder()
            .primaryManeuverOptions(
                ManeuverPrimaryOptions.Builder().textAppearance(R.style.ManeuverTextAppearance)
                    .build()
            )
            .secondaryManeuverOptions(
                ManeuverSecondaryOptions.Builder().textAppearance(R.style.ManeuverTextAppearance)
                    .build()
            )
            .subManeuverOptions(
                ManeuverSubOptions.Builder()
                    .exitOptions(
                        ManeuverExitOptions.Builder().textAppearance(R.style.ManeuverTextAppearance)
                            .build()
                    ).textAppearance(R.style.ManeuverTextAppearance).build()
            )
            .maneuverBackgroundColor(R.color.egg_white)
            .stepDistanceTextAppearance(R.style.ManeuverTextAppearance)
            .turnIconManeuver(R.style.MapboxCustomManeuverTurnIconStyle)
            .subManeuverBackgroundColor(R.color.egg_white)
            .upcomingManeuverBackgroundColor(R.color.egg_white)
            // Add other customization options as needed
            .build()

        // Update the options for the ManeuverView
        binding!!.maneuverView.updateManeuverViewOptions(maneuverViewOptions)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setMapboxRouteandArrowOptions() {
        // initialize route line, the withRouteLineBelowLayerId is specified to place
        // the route line below road labels layer on the map
        // the value of this option will depend on the style that you are using
        // and under which layer the route line should be placed on the map layers stack

        val routeLineColorResources = RouteLineColorResources.Builder()
            .routeLineTraveledCasingColor(getColor(R.color.light_grey))
            .inActiveRouteLegsColor(getColor(R.color.light_grey))
            .build()

        val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(this)
            .withRouteLineBelowLayerId(layer_id)
            .withVanishingRouteLineEnabled(true)
            .withRouteLineResources(
                RouteLineResources.Builder()
                    .routeLineColorResources(routeLineColorResources)
                    .build()
            )
            .build()
        routeLineApi = MapboxRouteLineApi(mapboxRouteLineOptions)
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        // initialize maneuver arrow view to draw arrows on the map
        val routeArrowOptions = RouteArrowOptions.Builder(this).build()
        routeArrowView = MapboxRouteArrowView(routeArrowOptions)
    }

    suspend fun getDirections(routeOptions: RouteOptions): List<DirectionsRoute> {
        try {
            val locations = routeOptions.coordinatesList().map {
                mapOf("lat" to it.latitude(), "lon" to it.longitude(), "type" to "break")
            }

            // NOTE: We map Mapbox profiles to Stadia ones here. The app UI appears to support
            // 3 modes of transit: golf cart, bicycle, and walking. Mapbox has no concept of golf
            // carts so we fake driving -> golf cart
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
                URL(STADIA_MAP_BASE_URL + "api_key=" + STADIAMAP_KEY)

            return withContext(Dispatchers.IO) {
                val conn = url.openConnection() as HttpsURLConnection
                conn.requestMethod = "POST"

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
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun screenNotSleep() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag")
        wl.acquire()
    }

    private fun handleTripProgressView() {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        if (isNightMode) {
            val trip = TripProgressViewOptions.Builder()
                .timeRemainingTextAppearance(R.style.tripstyle_night)
                .distanceRemainingTextAppearance(R.style.tripstyle_night)
                .estimatedArrivalTimeTextAppearance(R.style.tripstyle_night)
                .build()
            binding?.tripProgressView?.updateOptions(trip)
        } else {
            val trip = TripProgressViewOptions.Builder()
                .timeRemainingTextAppearance(R.style.tripstyle_day)
                .distanceRemainingTextAppearance(R.style.tripstyle_day)
                .estimatedArrivalTimeTextAppearance(R.style.tripstyle_day)
                .build()
            binding?.tripProgressView?.updateOptions(trip)
        }
    }

    val onPositionChangedListener = OnIndicatorPositionChangedListener { point ->
        val result = routeLineApi.updateTraveledRouteLine(point)
        binding!!.mapView.getMapboxMap().getStyle()?.apply {
            // Render the result to update the map.
            routeLineView.renderRouteLineUpdate(this, result)
        }
    }

    @JvmName("getMapStyle1")
    private fun getMapStyle(): String {
        if (mapStyle.equals("standard")) {
            return Style.OUTDOORS
        } else {
            return Style.SATELLITE_STREETS
        }
    }

    private fun initNavigation() {
        MapboxNavigationApp.setup(
            NavigationOptions.Builder(this)
                .accessToken(getString(R.string.mapbox_access_token))
                // comment out the location engine setting block to disable simulation
                // .locationEngine(replayLocationEngine)
                .build()
        )

        // initialize location puck
        binding?.mapView!!.location.apply {
            setLocationProvider(navigationLocationProvider)
            this.locationPuck = LocationPuck2D(
                bearingImage = ContextCompat.getDrawable(
                    this@NavigationActivity,
                    R.drawable.mapbox_navigation_puck_icon
                )
            )
            addOnIndicatorPositionChangedListener(onPositionChangedListener)
            enabled = true
        }

    }

    private fun getSpeed(enhancedLocation: Location) {
        var speed = 0
        /*latitude = java.lang.String.valueOf(enhancedLocation.getLatitude())
        longitude = java.lang.String.valueOf(enhancedLocation.getLongitude())*/
        speed = (enhancedLocation.getSpeed() * 2.2369).toInt()

        val get_final_velocity =
            if (speed < 9) "$speed" else if (speed < 99) speed.toString() else "$speed"
        binding?.tvSpeedometer!!.text = get_final_velocity

    }

    private fun clearRouteAndStopNavigation() {
        // clear
        mapboxNavigation.setNavigationRoutes(listOf())

        // hide UI elements
        binding!!.soundButton.visibility = View.INVISIBLE
        binding!!.maneuverView.visibility = View.INVISIBLE
        binding!!.routeOverview.visibility = View.INVISIBLE
        binding!!.tripProgressCard.visibility = View.INVISIBLE
    }

    private fun findRouteWithStadiaMap() {
        lifecycleScope.launch {

            val routeOptions =
                RouteOptions.builder()
                    // FIXME: It is your responsibility to select the correct profile here!
                    .applyDefaultNavigationOptions(getProfileCriteria())
                    .applyLanguageAndVoiceUnitOptions(this@NavigationActivity)
                    .coordinatesList(
                        listOf(
                            Point.fromLngLat(current_longitude, current_latitude),
                            Point.fromLngLat(destination_longitude, destination_latitude)
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

            directionsResponse = async { getDirections(routeOptions) }
            setRouteAndStartNavigation(directionsResponse!!.await().toNavigationRoutes())
            navigationCamera.requestNavigationCameraToOverview()

        }

    }

    private fun setRouteAndStartNavigation(routes: List<NavigationRoute>) {
        val context = this
        lifecycleScope.launch {
            // save time stamp
            timestamp = System.currentTimeMillis();
            // set routes, where the first route in the list is the primary route that
            // will be used for active guidance
            mapboxNavigation.setRerouteController(CustomRerouteController(routeOptions, context))
            mapboxNavigation.setNavigationRoutes(routes)

            // show UI elements
//        binding.speedLimitView.visibility = View.VISIBLE
            binding!!.soundButton.visibility = View.VISIBLE
            binding!!.routeOverview.visibility = View.VISIBLE
            binding!!.recenter.visibility = View.VISIBLE
            binding!!.tripProgressCard.visibility = View.VISIBLE
            binding!!.layoutSpeedometer.visibility = View.VISIBLE

            // move the camera to overview when new route is available
            navigationCamera.requestNavigationCameraToOverview()
            binding!!.recenter.performClick()
            binding!!.mapView.getMapboxMap().loadStyleUri(getMapStyle())
            delay(3000)
            Handler().postDelayed(java.lang.Runnable {

            }, 3000)
        }
    }

    class CustomRerouteController(
        private val initialRouteOptions: RouteOptions,
        private val context: Context,
    ) : NavigationRerouteController {
        // There is essentially zero useful documentation from Mapbox on how to implement custom
        // rerouting logic on Android, so I have had to rely heavily on this part of the source code:
        // https://github.com/mapbox/mapbox-navigation-android/blob/a6ccb7a2e75ead09320be34f158e41694663b087/libnavigation-core/src/main/java/com/mapbox/navigation/core/reroute/MapboxRerouteController.kt
        private var rerouteJob: Job? = null
        private val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        override var state: RerouteState = RerouteState.Idle
            private set(value) {
                if (field == value) {
                    return
                }

                field = value
                rerouteStateObservers.forEach { it.onRerouteStateChanged(field) }
            }

        private var rerouteStateObservers = HashSet<RerouteController.RerouteStateObserver>()

        private fun onRequestInterrupted() {
            if (state == RerouteState.FetchingRoute) {
                state = RerouteState.Interrupted
                state = RerouteState.Idle
            }
        }

        override fun interrupt() {
            rerouteJob?.cancel()
            rerouteJob = null
            onRequestInterrupted()
        }

        override fun registerRerouteStateObserver(rerouteStateObserver: RerouteController.RerouteStateObserver): Boolean =
            if (rerouteStateObservers.add(rerouteStateObserver)) {
                rerouteStateObserver.onRerouteStateChanged(state)
                true
            } else {
                false
            }

        override fun reroute(callback: NavigationRerouteController.RoutesCallback) {
            // Docs are nonexistent, so guessing at the implementation via this snippet:
            // https://github.com/mapbox/mapbox-navigation-android/blob/a6ccb7a2e75ead09320be34f158e41694663b087/libnavigation-core/src/main/java/com/mapbox/navigation/core/reroute/LegacyRerouteControllerAdapter.kt#L10
            reroute { routes ->
                callback.onNewRoutes(
                    routes.toNavigationRoutes(RouterOrigin.Custom()),
                    RouterOrigin.Custom()
                )
            }
        }

        override fun reroute(routesCallback: RerouteController.RoutesCallback) {
            val currentTimestamp = System.currentTimeMillis()
            val timeDifference = currentTimestamp - timestamp
            VillageLogs.printLog(">>>>time", (timeDifference >= 3000).toString())
//            if (timeDifference >= 3000) {
            timestamp = currentTimestamp
            interrupt()
            state = RerouteState.FetchingRoute

            val fineLocationPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val coarseLocationPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            if (fineLocationPermission != PackageManager.PERMISSION_GRANTED && coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                state =
                    RerouteState.Failed(context.getString(R.string.no_location_services_permissions))
                return
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                rerouteJob = CoroutineScope(Dispatchers.IO).launch {
                    delay(3000)
                    val routeOptions = initialRouteOptions
                        .toBuilder()
                        .coordinatesList(
                            listOf(
                                Point.fromLngLat(location.longitude, location.latitude),
                                initialRouteOptions.coordinatesList().last()
                            )
                        )
                        .build()
                    val res = getDirections(routeOptions)
                    routesCallback.onNewRoutes(res)
                }
            }

        }

        override fun unregisterRerouteStateObserver(rerouteStateObserver: RerouteController.RerouteStateObserver): Boolean =
            rerouteStateObservers.remove(rerouteStateObserver)

    }

    override fun onDestroy() {
        super.onDestroy()

        maneuverApi.cancel()
        routeLineApi.cancel()
        routeLineView.cancel()
        speechApi.cancel()
        voiceInstructionsPlayer.shutdown()
    }

}

suspend fun getDirections(routeOptions: RouteOptions): List<DirectionsRoute> {
    try {
        val locations = routeOptions.coordinatesList().map {
            mapOf("lat" to it.latitude(), "lon" to it.longitude(), "type" to "break")
        }

        // NOTE: We map Mapbox profiles to Stadia ones here. The app UI appears to support
        // 3 modes of transit: golf cart, bicycle, and walking. Mapbox has no concept of golf
        // carts so we fake driving -> golf cart
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
            val conn = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "POST"

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
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return emptyList()
    }

}