package com.golfcart.ui.village

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.ActivityVillageBinding
import com.golfcart.databinding.DialogErrorBinding
import com.golfcart.databinding.DialogExitBinding
import com.golfcart.model.app_interface.AppNavigationInterface
import com.golfcart.model.app_interface.EventsInterface
import com.golfcart.model.app_interface.OnBackEventCallBack
import com.golfcart.model.app_interface.OnMessageButtonClickListener
import com.golfcart.model.app_interface.PermissionListener
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.base.BaseActivity
import com.golfcart.ui.favourite.FragmentFavourite
import com.golfcart.ui.find_property.FragmentFindProperty
import com.golfcart.ui.golf_course_sub.FragmentGolfCourseSubcategory
import com.golfcart.ui.home.FragmentHome
import com.golfcart.ui.more.FragmentMore
import com.golfcart.ui.notification.FragmentNotification
import com.golfcart.ui.point_to_point.FragmentPointToPoint
import com.golfcart.ui.restaurant.FragmentRestaurant
import com.golfcart.ui.search.FragmentSearch
import com.golfcart.ui.speedometer.FragmentSpeedoMeter
import com.golfcart.ui.weather.FragmentWeather
import com.golfcart.utils.Logs.VillageLogs
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.loadGlide
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.IS_FROM_POINT
import com.google.android.gms.ads.AdRequest
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class VillageActivity : BaseActivity<ActivityVillageBinding, VillageViewModel>(),
    AppNavigationInterface, FragmentManager.OnBackStackChangedListener {

    var binding: ActivityVillageBinding? = null
    var locationManager: LocationManager? = null
    var onEventListener: EventsInterface? = null
    var onBackEventCallback: OnBackEventCallBack? = null

    companion object {
        var point_souce_lat = 0.0
        var point_souce_lng = 0.0
        var point_des_lat = 0.0
        var point_des_lng = 0.0
        var point_source_address = ""
        var point_des_address = ""
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_village
    }

    override fun getCurrentRunningActivity(): AppCompatActivity {
        return this@VillageActivity
    }

    override fun getViewModel(): VillageViewModel {
        val vm: VillageViewModel by viewModel()
        return vm
    }

    override fun getBindingVariable(): Int {
        return BR.village
    }

    private fun getCurrentActivity(): VillageActivity {
        return this@VillageActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.addOnBackStackChangedListener(getCurrentActivity())
        performBinding()
        navigateToHome()
        bottomNavigationListiner()
        toolbarListiners()
        loadAds()
    }

    private fun loadAds() {
        val adRequest = AdRequest.Builder().build()
        binding!!.adView.loadAd(adRequest)
    }

    private fun toolbarListiners() {
        binding!!.apply {

            layoutBack.setOnClickListener {
                onBackPressed()
            }

            ivleft.setOnClickListener {
                if (getCurrentFragment() is FragmentHome) {
                    replaceFragment(FragmentSearch(), false, false)
                    bottomNav.selectedItemId = R.id.menu_setting
                }
            }

            ivRightIcon.setOnClickListener {
                if (getCurrentFragment() is FragmentRestaurant || getCurrentFragment() is FragmentGolfCourseSubcategory
                    || getCurrentFragment() is FragmentFindProperty || getCurrentFragment() is FragmentFavourite
                ) {
                    onEventListener?.onEventClick()
                }
            }

            tvRight.setOnClickListener {
                if (getCurrentFragment() is FragmentNotification) {
                    onEventListener?.onEventClick()
                }
            }

        }
    }

    private fun navigateToHome() {
        replaceFragment(FragmentHome(), true, false)
    }

    private fun performBinding() {
        binding = DataBindingUtil.setContentView(this@VillageActivity, R.layout.activity_village)
        binding?.executePendingBindings()
    }

    private fun bottomNavigationListiner() {
        binding!!.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    navigateToHome()
                    true
                }

                R.id.menu_speedometer -> {
                    replaceFragment(FragmentSpeedoMeter(), false, false)
                    true
                }

                R.id.menu_weather -> {
                    replaceFragment(FragmentWeather(), false, false)
                    true
                }

                R.id.menu_fav -> {
                    replaceFragment(FragmentFavourite(), false, false)
                    true
                }

                R.id.menu_setting -> {
                    replaceFragment(FragmentSearch(), false, false)
                    true
                }

                else -> false
            }
        }
        binding!!.bottomNav.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    VillageLogs.printLog(">>>>check", "menu_home")
                    true
                }

                R.id.menu_speedometer -> {
                    VillageLogs.printLog(">>>>check", "menu_speedometer")
                    true
                }

                R.id.menu_weather -> {
                    VillageLogs.printLog(">>>>check", "menu_weather")
                    true
                }

                R.id.menu_fav -> {
                    VillageLogs.printLog(">>>>check", "amenu_favsda")
                    true
                }

                R.id.menu_setting -> {
                    VillageLogs.printLog(">>>>check", "menu_setting")
                    true
                }

                else -> false
            }
        }
    }

    override fun onBackStackChanged() {

    }

    var fragmentTransaction: FragmentTransaction? = null
    override fun replaceFragment(fragment: Fragment, addToStack: Boolean, showAnimation: Boolean) {
        lifecycleScope.launch {
            mCurrentFrg0 = fragment
            fragmentTransaction = supportFragmentManager.beginTransaction()
            if (addToStack) {
                fragmentTransaction!!.addToBackStack(fragment::class.java.simpleName)
            }
            if (showAnimation) {
                fragmentTransaction!!.setCustomAnimations(
                    R.anim.enter,
                    R.anim.exit,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
            }
            fragmentTransaction!!.replace(
                binding!!.flContainer.id,
                mCurrentFrg0!!,
                fragment::class.java.simpleName
            ).commitAllowingStateLoss()
        }
    }

    override fun setArguments(fragment: Fragment, bundle: Bundle): Fragment {
        fragment.arguments = bundle
        return fragment
    }

    override fun showErrorDialog(message: String, listener: OnMessageButtonClickListener) {
        VillageDialog<DialogErrorBinding>(
            this@VillageActivity, R.layout.dialog_error,
            object : DialogBinding<DialogErrorBinding> {

                override fun onBind(binder: DialogErrorBinding, dialog: Dialog) {
                    binder.tvMessage.text = message
                    binder.btnCancel.show()
                    binder.btnOk.text = getString(R.string.yes)
                    binder.btnOk.setOnClickListener {
                        listener.onYesClick(getString(R.string.yes))
                        dialog.dismiss()
                    }
                    binder.btnCancel.setOnClickListener {
                        listener.onNoClick(getString(R.string.no))
                        dialog.dismiss()
                    }
                }
            }, false, true
        ).show(supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    override fun showErrorDialog(message: String) {
        VillageDialog<DialogErrorBinding>(
            this@VillageActivity, R.layout.dialog_error,
            object : DialogBinding<DialogErrorBinding> {

                override fun onBind(binder: DialogErrorBinding, dialog: Dialog) {
                    binder.tvMessage.text = message
                    binder.btnOk.text = getString(R.string.okay)
                    binder.btnOk.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            }, false, true
        ).show(supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    override fun showToasts(message: String) {
        getCurrentRunningActivity().runOnUiThread {
            showToast(message)
        }
    }

    fun showBottomNavigation(showBottomBar: Boolean) {
        if (showBottomBar) {
            binding?.bottomNav?.show()
        } else {
            binding?.bottomNav?.hide()
        }
    }

    fun isToolbarVisible(showToolbar: Boolean) {
        if (showToolbar) {
            binding?.toolbar?.show()
        } else {
            binding?.toolbar?.hide()
        }
    }

    fun configureToolbar(configureToolbar: ToolbarConfiguration?) {
        runOnUiThread {
            if (configureToolbar != null) {
                binding!!.apply {
                    needShow(binding!!.layoutBack, configureToolbar.isBackVisibile)

                    needShow(binding!!.ivleft, configureToolbar.isLeftIconVisble)
                    loadGlide(configureToolbar.leftIcon, ivleft, this@VillageActivity)

                    needShow(binding!!.tvTitle, configureToolbar.isTitleVisible)
                    binding!!.tvTitle.text = configureToolbar.title

                    needShow(binding!!.tvRight, configureToolbar.isRightTextVisible)
                    binding!!.tvRight.text = configureToolbar.rightText

                    needShow(binding!!.ivRightIcon, configureToolbar.isRightIconVisible)
                    loadGlide(configureToolbar.rightIcon, ivRightIcon, this@VillageActivity)
                }
            }
        }
    }


    private val INTERNAL_SERVER_ERROR = 500
    private val UN_AUTHROIZED = 401
    private val BAD_REQUEST = 400
    private val FORBIDDEN = 403
    private val NOT_FOUND = 404
    private val NOT_ACCEPTABLE = 406
    private val APP_UPDATE = 426
    private val TO_MANY_REQUEST = 429
    private val INTERNET_ERROR = 1001

    fun handleError(data: ApiResponse.ApiError) {
        var message: String? = ""
        try {
            message = JSONObject(data.errorBody).optString(getString(R.string.error))

            if (message.equals("")) {
                message = JSONObject(data.errorBody).optString(getString(R.string.detail)) + ""
            }
            if (message.equals("")) {
                message = JSONObject(data.errorBody).optString(getString(R.string.message)) + ""
            }

        } catch (e: Exception) {
            e.printStackTrace()
            message = data.message
        }
        try {
            when (data.code) {
                INTERNAL_SERVER_ERROR -> {
                    showToast(getString(R.string.internal_server_error))
                }

                UN_AUTHROIZED -> {
                    showToast(getString(R.string.unauthorized_access))
                }

                BAD_REQUEST -> {
                    VillageLogs.printLog(getString(R.string.handleerror), message!!)
                    showErrorDialog(message)
                }

                FORBIDDEN -> {
                    VillageLogs.printLog(getString(R.string.handleerror), message!!)
                    showErrorDialog(message)
                }

                NOT_FOUND -> {
                    VillageLogs.printLog(getString(R.string.handleerror), message!!)
                    showErrorDialog(message)
                }

                NOT_ACCEPTABLE -> {
                    VillageLogs.printLog(getString(R.string.handleerror), message!!)
                    showErrorDialog(message)
                }

                APP_UPDATE -> {
                    VillageLogs.printLog(getString(R.string.handleerror), message!!)
                    showErrorDialog(message)
                }

                TO_MANY_REQUEST -> {
                    VillageLogs.printLog(getString(R.string.handleerror), message!!)
                    showErrorDialog(message)
                }

                INTERNET_ERROR -> {
                    VillageLogs.printLog(getString(R.string.handleerror), message!!)
                    showErrorDialog(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun locationPermissions(listner: PermissionListener?) {

        Dexter.withActivity(this@VillageActivity)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        fetchLocation(listner)
                    }

                    if (ActivityCompat.checkSelfPermission(
                            this@VillageActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(
                            this@VillageActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        var oneTimeDialog = true
                        if (oneTimeDialog) {
                            oneTimeDialog = false
                            showSettingsDialog()
                        }
                    } else {
                        fetchLocation(listner)
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }


            }).check()
    }

    fun fetchLocation(listner: PermissionListener?) {
        if (isLocationEnabled()) {
            getCurrentLatLng(listner)
        } else {
            showLocationErrorDialog()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun getCurrentLatLng(listner: PermissionListener?) {
        try {
            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setSpeedRequired(true);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(false);
            val bestProvider = locationManager!!.getBestProvider(criteria, true).toString()
            val location: Location? = locationManager!!.getLastKnownLocation(bestProvider)

            if (location != null) {
                val latitude = location.getLatitude()
                val longitude = location.getLongitude()
                listner!!.onAccepted(latitude, longitude)

            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                locationManager!!.requestLocationUpdates(
                    bestProvider,
                    1000,
                    0f,
                    object : LocationListener {
                        override fun onLocationChanged(loc: Location) {
                            loc.let {
                                listner!!.onAccepted(it.latitude, it.longitude)
                            } ?: showToast(getString(R.string.failed_to_access_location))
                        }
                    })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showLocationErrorDialog() {
        VillageDialog<DialogErrorBinding>(
            this@VillageActivity,
            R.layout.dialog_error,
            object : DialogBinding<DialogErrorBinding> {
                override fun onBind(binder: DialogErrorBinding, dialog: Dialog) {
                    binder.apply {
                        tvMessage.text =
                            getString(R.string.please_enable_you_location_from_location_setting)
                        btnOk.text = getString(R.string.go_to_settings)
                        btnOk.setOnClickListener {
                            dialog.dismiss()
                            val viewIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(viewIntent)
                        }
                    }
                }
            },
            true,
            true
        ).show(
            supportFragmentManager,
            VillageDialog::class.java.simpleName
        )
    }

    fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@VillageActivity)
        builder.setTitle(this@VillageActivity.getString(R.string.dialog_permission_title))
        builder.setCancelable(false)
        builder.setMessage(this@VillageActivity.getString(R.string.dialog_permission_message))
        builder.setPositiveButton(this@VillageActivity.getString(R.string.go_to_settings)) { dialog, _ ->
            dialog.cancel()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts(
                getString(R.string.package_),
                this@VillageActivity.packageName,
                null
            )
            intent.data = uri
            this@VillageActivity.startActivityForResult(intent, 101)
        }
        builder.show()
    }

    fun openDialPad(phoneNumber: String?) {
        Dexter.withActivity(this@VillageActivity)
            .withPermissions(Manifest.permission.CALL_PHONE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        startDialPad(phoneNumber)
                    }

                    if (ActivityCompat.checkSelfPermission(
                            this@VillageActivity,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        var oneTimeDialog = true
                        if (oneTimeDialog) {
                            oneTimeDialog = false
                            showSettingsDialog()
                        }
                    } else {
                        startDialPad(phoneNumber)
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }


            }).check()
    }

    fun startDialPad(phoneNumber: String?) {
        val phone_intent = Intent(Intent.ACTION_DIAL)
        phone_intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(phone_intent)
    }

    override fun onBackPressed() {

        if (getCurrentFragment() is FragmentSearch && IS_FROM_POINT) {
            onEventListener?.onEventClick()
        } else if (getCurrentFragment() is FragmentPointToPoint) {
            onEventListener?.onEventClick()
        } else if (getCurrentFragment() is FragmentMore) {
            replaceFragment(FragmentHome(), true, false)
        } else if (getCurrentFragment() is FragmentHome || getCurrentFragment() is FragmentSpeedoMeter || getCurrentFragment() is FragmentWeather
            || getCurrentFragment() is FragmentFavourite || getCurrentFragment() is FragmentSearch
        ) {
            exitDialog()
        } else {
            super.onBackPressed()
        }
    }

    fun exitDialog() {
        VillageDialog<DialogExitBinding>(
            this@VillageActivity, R.layout.dialog_exit,
            object : DialogBinding<DialogExitBinding> {

                override fun onBind(binder: DialogExitBinding, dialog: Dialog) {
                    binder.apply {
                        tvYes.setOnClickListener {
                            finishAffinity()
                            dialog.dismiss()
                        }
                        tvNo.setOnClickListener {
                            dialog.dismiss()
                        }
                    }
                }
            }, false, true
        ).show(supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    fun adsVisibility(value : Boolean){
        if (value) {
            binding!!.adView.show()
        }else{
            binding!!.adView.hide()
        }
    }

}