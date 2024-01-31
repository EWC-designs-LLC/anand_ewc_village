package com.golfcart.ui.more

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.DialogCancelSubscriptionStepsBinding
import com.golfcart.databinding.DialogEditFavNameBinding
import com.golfcart.databinding.DialogRateUsBinding
import com.golfcart.databinding.DialogSubscriptionBinding
import com.golfcart.databinding.FragmentMoreBinding
import com.golfcart.databinding.ItemMoreBinding
import com.golfcart.model.profile.ProfileResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.contact_us.FragmentContactUs
import com.golfcart.ui.notification.FragmentNotification
import com.golfcart.ui.point_to_point.FragmentPointToPoint
import com.golfcart.ui.rate_us.FragmentRateUs
import com.golfcart.ui.share.FragmentShare
import com.golfcart.ui.version.FragmentAppVersion
import com.golfcart.ui.village.DialogBinding
import com.golfcart.ui.village.VillageActivity
import com.golfcart.ui.village.VillageDialog
import com.golfcart.ui.village.VillageFragment
import com.golfcart.ui.webview.FragmentWebView
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.loadGlide
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.ABOUT_US_URL
import com.golfcart.utils.village_constant.VillageConstants.DISCLAIMER_URL
import com.golfcart.utils.village_constant.VillageConstants.FAQ_URL
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentMore : VillageFragment<FragmentMoreBinding, MoreViewModel>() {

    var moreAdapter: RecyclerViewGenericAdapter<MoreData, ItemMoreBinding>? = null
    var moreList = ArrayList<MoreData>()

    data class MoreData(val name: String, val image: Int)

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentMore
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, getString(R.string.more),
            false, -1, false, "", false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.more
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_more
    }

    override fun getViewModel(): MoreViewModel {
        val vm: MoreViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            setData()
            setAdapter()
            newAndOldUserValidations()
            clearPointData()
        }
    }

    private fun clearPointData() {
        VillageActivity.point_souce_lat = 0.0
        VillageActivity.point_souce_lng = 0.0
        VillageActivity.point_source_address = ""
        VillageActivity.point_des_lat = 0.0
        VillageActivity.point_des_lng = 0.0
        VillageActivity.point_des_address = ""
    }

    private fun setData() {
        moreList.clear()
        moreList.add(MoreData(getString(R.string.share), R.drawable.ic_share_setting))
        moreList.add(MoreData(getString(R.string.faq), R.drawable.ic_faq))
        moreList.add(MoreData(getString(R.string.point_to_point), R.drawable.ic_point))
        moreList.add(MoreData(getString(R.string.about_us), R.drawable.ic_about_us))
        moreList.add(MoreData(getString(R.string.contact), R.drawable.ic_contact_us))
        moreList.add(MoreData(getString(R.string.disclaimer), R.drawable.ic_disclaimer))
        moreList.add(MoreData(getString(R.string.app_version), R.drawable.ic_version))
        moreList.add(MoreData(getString(R.string.notifications), R.drawable.ic_bell))
        moreList.add(MoreData(getString(R.string.notification_list), R.drawable.ic_bell))
        moreList.add(MoreData(getString(R.string.rate_us), R.drawable.ic_bell))
       // moreList.add(MoreData(getString(R.string.cancel_subscription), R.drawable.ic_bell))
       // moreList.add(MoreData(getString(R.string.no_ads), R.drawable.ic_bell))
    }

    fun addAds(){
        moreList.add(MoreData(getString(R.string.no_ads), R.drawable.ic_bell))
        moreAdapter!!.updateAdapterList(moreList)
    }

    fun removeAds(){
        moreList.remove(MoreData(getString(R.string.no_ads), R.drawable.ic_bell))
        moreAdapter!!.updateAdapterList(moreList)
    }

    fun addSubscription(){
        moreList.add(MoreData(getString(R.string.cancel_subscription), R.drawable.ic_bell))
        moreAdapter!!.updateAdapterList(moreList)
    }

    fun cancelSubscription(){
        moreList.remove(MoreData(getString(R.string.cancel_subscription), R.drawable.ic_bell))
        moreAdapter!!.updateAdapterList(moreList)
    }



    private fun setAdapter() {
        getViewDataBinding()!!.apply {
            getContainerActivity().runOnUiThread {
                if (moreAdapter == null) {

                    moreAdapter = RecyclerViewGenericAdapter(moreList, R.layout.item_more,
                        object : RecyclerCallback<ItemMoreBinding, MoreData> {
                            override fun bindData(
                                binder: ItemMoreBinding,
                                model: MoreData,
                                position: Int,
                                itemView: View?
                            ) {
                                binder.apply {

                                    loadGlide(model.image, ivShare, getContainerActivity())
                                    tvTitle.text = model.name

                                    if (model.name.equals("Notifications")) {
                                        btnToogle.show()
                                        ivArrow.hide()
                                        btnToogle.isChecked =
                                            getViewModel().getAppRepository().isNotificationToogle()
                                    } else {
                                        btnToogle.hide()
                                        ivArrow.show()
                                    }

                                    itemView?.setOnClickListener {
                                        navigateTo(model)
                                    }

                                    btnToogle.setOnClickListener {
                                        updateNotification(if (btnToogle.isChecked) 1 else 0)
                                    }

                                }
                            }
                        })
                    rvMore.adapter = moreAdapter
                }
            }
        }
    }

    private fun navigateTo(model: MoreData) {
        when (model.name) {
            getString(R.string.share) -> {
                replaceFragment(FragmentShare(), true, true)
            }

            getString(R.string.faq) -> {
                val bundle = Bundle()
                bundle.putString(VillageConstants.TOOLBAR_TITLE, model.name)
                bundle.putString(VillageConstants.WEB_URL, FAQ_URL)
                replaceFragment(setArguments(FragmentWebView(), bundle), true, true)
            }

            getString(R.string.point_to_point) -> {
                showPointToPointDialog()
            }

            getString(R.string.about_us) -> {
                val bundle = Bundle()
                bundle.putString(VillageConstants.TOOLBAR_TITLE, model.name)
                bundle.putString(VillageConstants.WEB_URL, ABOUT_US_URL)
                replaceFragment(setArguments(FragmentWebView(), bundle), true, true)
            }

            getString(R.string.contact) -> {
                val bundle = Bundle()
                bundle.putString(VillageConstants.TOOLBAR_TITLE, model.name)
                replaceFragment(setArguments(FragmentContactUs(), bundle), true, true)
            }

            getString(R.string.disclaimer) -> {
                val bundle = Bundle()
                bundle.putString(VillageConstants.TOOLBAR_TITLE, model.name)
                bundle.putString(VillageConstants.WEB_URL, DISCLAIMER_URL)
                replaceFragment(setArguments(FragmentWebView(), bundle), true, true)
            }

            getString(R.string.app_version) -> {
                val bundle = Bundle()
                bundle.putString(VillageConstants.TOOLBAR_TITLE, model.name)
                replaceFragment(setArguments(FragmentAppVersion(), bundle), true, true)
            }

            getString(R.string.notification_list) -> {
                val bundle = Bundle()
                bundle.putString(VillageConstants.TOOLBAR_TITLE, model.name)
                replaceFragment(setArguments(FragmentNotification(), bundle), true, true)
            }

            getString(R.string.rate_us) -> {
                val bundle = Bundle()
                bundle.putString(VillageConstants.TOOLBAR_TITLE, model.name)
                replaceFragment(setArguments(FragmentRateUs(), bundle), true, true)
            }
            getString(R.string.cancel_subscription) -> {
                subscriptionDialog()
            }
        }
    }

    private fun subscriptionDialog() {
        VillageDialog<DialogCancelSubscriptionStepsBinding>(
            getContainerActivity(), R.layout.dialog_cancel_subscription_steps,
            object : DialogBinding<DialogCancelSubscriptionStepsBinding> {

                override fun onBind(binder: DialogCancelSubscriptionStepsBinding, dialog: Dialog) {
                    binder.apply {
                        btnCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        btnContinue.setOnClickListener {
                            dialog.dismiss()
                            openPlaystoreReview()
                        }
                    }
                }
            }, false, true
        ).show(getContainerActivity().supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    private fun showPointToPointDialog() {
        VillageDialog<DialogRateUsBinding>(
            getContainerActivity(), R.layout.dialog_rate_us,
            object : DialogBinding<DialogRateUsBinding> {

                override fun onBind(binder: DialogRateUsBinding, dialog: Dialog) {
                    binder.apply {
                        ivClose.setOnClickListener {
                            dialog.dismiss()
                        }
                        okBtn.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString(
                                VillageConstants.TOOLBAR_TITLE,
                                getString(R.string.point_to_point)
                            )
                            replaceFragment(
                                setArguments(FragmentPointToPoint(), bundle),
                                true,
                                true
                            )
                            dialog.dismiss()
                        }
                    }
                }
            }, false, true
        ).show(getContainerActivity().supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    fun updateNotification(status: Int) {
        getViewModel().updateNotification(
            getContainerActivity(),
            getViewModel().getAppRepository().getUserId(),
            status
        )
        getViewModel().updateNotificationLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    getViewModel().getAppRepository()
                        .setNotificationToogle(if (status == 1) true else false)
                    removeObserver(getViewModel().updateNotificationMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun newAndOldUserValidations() {
        try {
            val profileResponse: ProfileResponse = getViewModel().getReflectionUtils().getStringToPojo(getViewModel().getAppRepository().getProfileModel()!!,ProfileResponse::class.java)
            if (profileResponse != null) {
                if (profileResponse.data!!.accountType === 0) {         // old user
                    if (profileResponse.data!!.subscriptionType === 0) {
                        addAds()
                    } else {
                        removeAds()
                        adsVisibility(false)
                    }
                }
                if (profileResponse.data?.accountType === 1) {        // new user
                    removeAds()
                }
                if (profileResponse.data?.accountType === 2) {        // coupoun applied user
                    removeAds()
                }
                if (profileResponse.data?.subscriptionType === 0) {
                     cancelSubscription()
                } else {
                     addSubscription()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}