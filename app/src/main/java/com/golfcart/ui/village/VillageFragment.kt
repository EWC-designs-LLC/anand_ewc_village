package com.golfcart.ui.village

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.golfcart.R
import com.golfcart.model.app_interface.FragmentView
import com.golfcart.model.app_interface.OnMessageButtonClickListener
import com.golfcart.model.app_interface.PermissionListener
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.remote.ReflectionUtil
import com.golfcart.ui.base.BaseFragment
import com.golfcart.utils.Logs.VillageLogs
import com.mapbox.mapboxsdk.geometry.LatLng
import org.koin.android.ext.android.inject
import java.text.DecimalFormat

 abstract class VillageFragment<T : ViewDataBinding, V : ViewModel> : BaseFragment<T, V>(),
     FragmentView {

    companion object{
        var TOKEN_ENABLE=true
    }

    val df = DecimalFormat("0.00")
     private val EARTH_RADIUS_KM = 6371.0

    private var containerActivity: VillageActivity? = null
    val reflectionUtils: ReflectionUtil by inject()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.containerActivity = context as VillageActivity
        VillageLogs.printLog(">>>name",this.javaClass.name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getContainerActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onStart() {
        super.onStart()
        try {
            containerActivity?.showBottomNavigation(showBottomBar()!!)
            containerActivity?.isToolbarVisible(showToolbar()!!)
            containerActivity?.setCurrentFrag(getCurrentFragment()!!)
            containerActivity?.configureToolbar(configureToolbar())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun replaceFragment(frg0: Fragment, isBack: Boolean, showAnim: Boolean) {
        containerActivity!!.replaceFragment(frg0, isBack, showAnim)
    }

    fun setArguments(mFragment: Fragment, mBundle: Bundle): Fragment {
        return containerActivity?.setArguments(mFragment, mBundle)!!
    }

    fun getContainerActivity(): VillageActivity {
        return containerActivity!!
    }

    fun showToast(message: String) {
        this.containerActivity?.showToast(message)
    }

    fun showLoader() {
        return containerActivity?.showLoader()!!
    }

    fun hideLoader() {
        return containerActivity?.hideLoader()!!
    }

    fun showErrorDialog(message: String){
        return containerActivity?.showErrorDialog(message)!!
    }

    fun showErrorDialog(message: String, listener: OnMessageButtonClickListener){
        return containerActivity?.showErrorDialog(message,listener)!!
    }

    fun handleError(data: ApiResponse.ApiError?) {
        return getContainerActivity().handleError(data!!)
    }

    fun locationPermission(listener: PermissionListener) {
        getContainerActivity().locationPermissions(listener)
    }

    fun removeObserver(mutableLiveData: MutableLiveData<*>) {
        mutableLiveData.removeObservers(viewLifecycleOwner)
    }

    fun getDeviceUniqueId(): String? {
        return containerActivity!!.getDeviceUniqueId()
    }

    fun openPhoneDialPad(phone_number:String?){
        return containerActivity!!.openDialPad(phone_number?.replace(("[^\\d.]").toRegex(), ""))
    }

    fun openGmail(email:String){
        val intent = Intent(Intent.ACTION_SEND)
        val recipients = arrayOf(email)
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)
        intent.type = "text/html"
        intent.setPackage("com.google.android.gm")
        startActivity(Intent.createChooser(intent, "Send mail"))
    }

    fun getMiles(meters:Double?): String {
        val number=meters!! * 0.000621371192
        return df.format(number)
    }

    fun enableAuthToken() {
        TOKEN_ENABLE=true
    }

    fun disableAuthToken() {
        TOKEN_ENABLE=false
    }

     fun gotoMail() {
         val uri = Uri.parse("mailto:villagegps@gmail.com")
             .buildUpon()
             .build()
         val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
         startActivity(Intent.createChooser(emailIntent, "Choose Title"))
     }

    fun vibrateDevice(){
        val v = getContainerActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v!!.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v!!.vibrate(200)
        }
    }

    fun getMiles(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return String.format("%.2f", dist);
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

     fun findDistance(
         sourceLat: Double,
         sourceLng: Double,
         desLat: Double,
         desLng: Double
     ): String {
         val latLngA = LatLng(sourceLat, sourceLng)
         val latLngB = LatLng(desLat, desLng)
         val locationA = Location("point A")
         locationA.latitude = latLngA.latitude
         locationA.longitude = latLngA.longitude
         val locationB = Location("point B")
         locationB.latitude = latLngB.latitude
         locationB.longitude = latLngB.longitude
         return df.format(locationA.distanceTo(locationB).toDouble() * 0.00062137)
     }

     fun calculateDistance(
         lat1: Double,
         lon1: Double,
         lat2: Double,
         lon2: Double
     ): Double {
         // Convert latitude and longitude from degrees to radians
         val radLat1 = Math.toRadians(lat1)
         val radLon1 = Math.toRadians(lon1)
         val radLat2 = Math.toRadians(lat2)
         val radLon2 = Math.toRadians(lon2)

         // Calculate the differences between the latitudes and longitudes
         val dLat = radLat2 - radLat1
         val dLon = radLon2 - radLon1

         // Calculate the distance using the Haversine formula
         val a = Math.pow(
             Math.sin(dLat / 2),
             2.0
         ) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(
             Math.sin(
                 dLon / 2
             ), 2.0
         )
         val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
         return EARTH_RADIUS_KM * c
     }

     fun adsVisibility(value : Boolean){
         getContainerActivity().adsVisibility(value)
     }

     open fun getAppVersion(context: Context): String? {
         return try {
             val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
             packageInfo.versionName
         } catch (e: java.lang.Exception) {
             e.printStackTrace()
             ""
         }
     }

     fun openPlaystoreReview() {
         val appPackageName: String =
             getContainerActivity().getPackageName() // Replace with the actual package name of the app
         try {
             // Create an Intent to open the Play Store with the review section
             val intent =
                 Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
             intent.putExtra(
                 "referrer",
                 "utm_source=your_app&utm_medium=your_medium"
             ) // Optional referrer information

             // Check if there's a package that can handle the intent (Play Store app)
             if (intent.resolveActivity(getContainerActivity().getPackageManager()) != null) {
                 startActivity(intent)
             } else {
                 // If there is no Play Store app, you can handle it gracefully here
                 showToast(getString(R.string.play_store_app_not_found))
             }
         } catch (e: java.lang.Exception) {
             // Handle the case where the Play Store app is not available on the device
             showToast(getString(R.string.play_store_app_not_installed))
         }
     }

 }