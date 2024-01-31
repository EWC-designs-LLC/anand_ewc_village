package com.golfcart.ui.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.DialogNotificationAlertBinding
import com.golfcart.databinding.DialogSubscriptionBinding
import com.golfcart.databinding.DialogUpdateBinding
import com.golfcart.databinding.FragmentHomeBinding
import com.golfcart.databinding.ItemHomeBinding
import com.golfcart.model.app_interface.PermissionListener
import com.golfcart.model.home.CategoryResponse
import com.golfcart.model.profile.ProfileResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.find_property.FragmentFindProperty
import com.golfcart.ui.golf_cart_help.FragmentGolfCartHelp
import com.golfcart.ui.golf_course.FragmentGolfCourse
import com.golfcart.ui.golf_course_sub.FragmentGolfCourseSubcategory
import com.golfcart.ui.more.FragmentMore
import com.golfcart.ui.notification.FragmentNotification
import com.golfcart.ui.restaurant.FragmentRestaurant
import com.golfcart.ui.subscription.SubscriptionActivity
import com.golfcart.ui.tutorial.TutorialActivity
import com.golfcart.ui.village.DialogBinding
import com.golfcart.ui.village.VillageDialog
import com.golfcart.ui.village.VillageFragment
import com.golfcart.ui.webview.FragmentWebView
import com.golfcart.utils.storage.VillageStorage
import com.golfcart.utils.storage.VillageStorage.storeHomeCategoryResponse
import com.golfcart.utils.view_utils.loadGlide
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.CATEGORY_ID
import com.golfcart.utils.village_constant.VillageConstants.IS_FROM_HOME
import com.golfcart.utils.village_constant.VillageConstants.SUB_CATEGORY_ID
import com.golfcart.utils.village_constant.VillageConstants.TOOLBAR_TITLE
import com.golfcart.utils.village_constant.VillageConstants.WEATHER_APP
import com.golfcart.utils.village_constant.VillageConstants.WEB_URL
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentHome : VillageFragment<FragmentHomeBinding, HomeViewModel>() {

    var homeAdapter: RecyclerViewGenericAdapter<CategoryResponse.Data, ItemHomeBinding>? = null
    var homeList = ArrayList<CategoryResponse.Data>()

    override fun showBottomBar(): Boolean {
        return true
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentHome
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            false, true, getString(R.string.home),
            true, R.drawable.ic_search_home, true, "", false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.home
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun getViewModel(): HomeViewModel {
        val vm: HomeViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            setAdapter()
            launchHomeCategories()
            getProfileData()
            fetchLocation()
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
                    getWeatherStatus()
                }
            }
        })
    }

    private fun getProfileData() {
        getViewModel().getProfileData(getContainerActivity(), getDeviceUniqueId()!!)
        getViewModel().profileLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateProfile(it.data)
                    removeObserver(getViewModel().profileMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun updateProfile(response: ProfileResponse?) {
        if (response != null) {
            getViewModel().getAppRepository()
                .setProfileModel(getViewModel().getReflectionUtils().convertPojoToJson(response))
            validateNewAndOldUser(response)
        }
    }

    /*
    *
    *  subscription_type ---> 0 // no subscription
    *  subscription_type ---> 1 // subscribed
    *
    * */
    private fun validateNewAndOldUser(response: ProfileResponse) {
        try {
            if (response.data!!.accountType === 1) {           // new user
                if (response.data!!.subscriptionType === 0) {   // not subscribe
                    adsVisibility(true)
                    subscriptionDialog()
                } else {                                    // subscribe
                    adsVisibility(false)
                }
            }
            if (response.data!!.accountType === 0) {      // old user
                if (response.data!!.subscriptionType === 0) {   // not subscribe
                    adsVisibility(true)
                } else {
                    adsVisibility(false)
                }
            }
            if (response.data!!.accountType === 2) {   // used the coupoun
                adsVisibility(false)
            }
            if (!response.data!!.current_version.equals(getAppVersion(getContainerActivity()))) {
                showAppUpdateDialog()
            }
            if (response.data!!.unread_notification !== 0) {
                notificationPopup()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun subscriptionDialog() {
        VillageDialog<DialogSubscriptionBinding>(
            getContainerActivity(), R.layout.dialog_subscription,
            object : DialogBinding<DialogSubscriptionBinding> {

                override fun onBind(binder: DialogSubscriptionBinding, dialog: Dialog) {
                    binder.apply {
                       okBtn.setOnClickListener {
                           startActivity(Intent(getContainerActivity(), SubscriptionActivity::class.java))
                       }
                    }
                }
            }, true, true
        ).show(getContainerActivity().supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    private fun showAppUpdateDialog() {
        VillageDialog<DialogUpdateBinding>(
            getContainerActivity(), R.layout.dialog_update,
            object : DialogBinding<DialogUpdateBinding> {

                override fun onBind(binder: DialogUpdateBinding, dialog: Dialog) {
                    binder.apply {
                        updateBtn.setOnClickListener {
                            openPlaystoreReview()
                            dialog.dismiss()
                        }
                    }
                }
            }, true, true
        ).show(getContainerActivity().supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    private fun notificationPopup() {
        VillageDialog<DialogNotificationAlertBinding>(
            getContainerActivity(), R.layout.dialog_notification_alert,
            object : DialogBinding<DialogNotificationAlertBinding> {

                override fun onBind(binder: DialogNotificationAlertBinding, dialog: Dialog) {
                    binder.apply {
                        btnOk.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString(VillageConstants.TOOLBAR_TITLE, getString(R.string.notifications))
                            replaceFragment(setArguments(FragmentNotification(), bundle), true, true)
                            dialog.dismiss()
                        }
                    }
                }
            }, false, true
        ).show(getContainerActivity().supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    private fun launchHomeCategories() {
        if (VillageStorage.getStoreHomeResponse() != null) {
            homeAdapter?.updateAdapterList(VillageStorage.getStoreHomeResponse()?.data as ArrayList<CategoryResponse.Data>)
        } else {
            getCategories()
        }
    }

    private fun getCategories() {
        getViewModel().getHomeCategoriesData(getContainerActivity())
        getViewModel().categoriesliveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateCategoriesUi(it.data)
                    removeObserver(getViewModel().categoriesMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun getWeatherStatus() {
        lifecycleScope.launch {
            val url = VillageConstants.WEATHER_TEMP_URL +
                    "lat=${latLng?.latitude}&" +
                    "lon=${latLng?.longitude}&" +
                    "units=metric&" +
                    "appid=${WEATHER_APP}&"+
                    "type=accurat"

            getViewModel().getWeatherInfo(getContainerActivity(), url)
            getViewModel().weatherLiveData.observe(viewLifecycleOwner) {
                when (it.status) {
                    ApiResponse.Status.LOADING -> {
                        showLoader()
                    }

                    ApiResponse.Status.SUCCESS -> {
                        try {
                            getContainerActivity().binding!!.tvRight.text =
                                "${Math.round(9.0 / 5.0 * it.data?.main?.temp!! + 32)}" + "\u2109 " + "\n" + wordFirstCap("${it.data.weather!!.get(0).main}")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        hideLoader()
                        removeObserver(getViewModel().weatherMutableLiveData)
                    }

                    ApiResponse.Status.ERROR -> {
                        hideLoader()
                        handleError(it.error)
                        removeObserver(getViewModel().weatherMutableLiveData)
                    }
                }
            }
        }
    }

    fun wordFirstCap(str: String): String? {
        val words = str.trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val ret = StringBuilder()
        for (i in words.indices) {
            if (words[i].trim { it <= ' ' }.length > 0) {
                ret.append(words[i].trim { it <= ' ' }[0].uppercaseChar())
                ret.append(words[i].trim { it <= ' ' }.substring(1))
                if (i < words.size - 1) {
                    ret.append(' ')
                }
            }
        }
        return ret.toString()
    }

    private fun updateCategoriesUi(response: CategoryResponse?) {
        if (!response?.data.isNullOrEmpty()) {
            storeHomeCategoryResponse(response!!)
            homeList.clear()
            homeList.addAll(response.data!!)
            homeAdapter!!.updateAdapterList(homeList)
        }
    }

    private fun setAdapter() {
        getViewDataBinding()!!.apply {
            getContainerActivity().runOnUiThread {
                if (homeAdapter == null) {

                    homeAdapter = RecyclerViewGenericAdapter(homeList, R.layout.item_home,
                        object : RecyclerCallback<ItemHomeBinding, CategoryResponse.Data> {
                            override fun bindData(
                                binder: ItemHomeBinding,
                                model: CategoryResponse.Data,
                                position: Int,
                                itemView: View?
                            ) {
                                binder.apply {

                                    if (model.value.equals("tab")) {
                                        loadGlide(
                                            R.drawable.ic_tutorial,
                                            ivCategories,
                                            getContainerActivity()
                                        )
                                        tvTitle.text = getString(R.string.tutorials)
                                    } else {
                                        tvTitle.text = model.name
                                        loadGlide(model.image, ivCategories, getContainerActivity())
                                    }

                                    itemView?.setOnClickListener {
                                        navigateTo(model)
                                    }

                                }
                            }
                        })
                    rvHome.adapter = homeAdapter
                }
            }
        }
    }

    private fun navigateTo(model: CategoryResponse.Data) {
        if (model != null) {
            when {
                model.value == null -> {
                    val bundle = Bundle()
                    bundle.putString(TOOLBAR_TITLE, model.name)
                    bundle.putInt(CATEGORY_ID, model.id!!)
                    replaceFragment(setArguments(FragmentGolfCourse(), bundle), true, true)
                }
                model.value.equals("by_admin", true) -> {
                    val bundle = Bundle()
                    bundle.putString(TOOLBAR_TITLE, model.name)
                    bundle.putInt(CATEGORY_ID, model.id!!)
                    bundle.putInt(SUB_CATEGORY_ID, model.sub_cat_status!!)
                    replaceFragment(setArguments(FragmentGolfCourseSubcategory(), bundle), true, true)
                }
                model.value!!.startsWith("http") -> {
                    val bundle = Bundle()
                    bundle.putString(TOOLBAR_TITLE, model.name)
                    bundle.putString(WEB_URL, model.value)
                    replaceFragment(setArguments(FragmentWebView(), bundle), true, false)
                }
                model.value.equals("notification") -> {
                    val bundle = Bundle()
                    bundle.putString(TOOLBAR_TITLE, model.name)
                    replaceFragment(setArguments(FragmentFindProperty(), bundle), true, true)
                }
                model.value.equals("Restaurants") || model.value.equals("Shopping") -> {
                    val bundle = Bundle()
                    bundle.putString(TOOLBAR_TITLE, model.name)
                    bundle.putInt(CATEGORY_ID, model.id!!)
                    replaceFragment(setArguments(FragmentRestaurant(), bundle), true, true)
                }
                model.value.equals("golf_cart_help") -> {
                    val bundle = Bundle()
                    bundle.putString(TOOLBAR_TITLE, model.name)
                    replaceFragment(setArguments(FragmentGolfCartHelp(), bundle), true, true)
                }
                model.value.equals("tab") -> {
                    val intent = Intent(getContainerActivity(), TutorialActivity::class.java)
                    intent.putExtra(IS_FROM_HOME, true)
                    startActivity(intent)
                }
                model.value.equals("Setting") -> {
                    replaceFragment(FragmentMore(), true, true)
                }
            }
        }
    }

}