package com.golfcart.ui.golf_cart_help

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.FragmentGolfCartHelpBinding
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.village.VillageFragment
import com.golfcart.ui.webview.FragmentWebView
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.GOLF_CART_NUMBER
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentGolfCartHelp : VillageFragment<FragmentGolfCartHelpBinding, GolfCartHelpViewModel>() {

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentGolfCartHelp
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, requireArguments().getString(VillageConstants.TOOLBAR_TITLE, ""),
            false, -1,false,"",
            false,-1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.golf_help
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_golf_cart_help
    }

    override fun getViewModel(): GolfCartHelpViewModel {
        val vm: GolfCartHelpViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListiner()
    }

    private fun setListiner() {
        getViewDataBinding()!!.apply {
            btnCall.setOnClickListener {
                openPhoneDialPad(GOLF_CART_NUMBER)
            }
            btnWebsite.setOnClickListener {
                navigateToWebsite()
            }
        }
    }

    private fun navigateToWebsite() {
        val bundle = Bundle()
        bundle.putString(VillageConstants.TOOLBAR_TITLE, requireArguments().getString(VillageConstants.TOOLBAR_TITLE, ""))
        bundle.putString(VillageConstants.WEB_URL,VillageConstants.GOLF_CART_HELP_WEBSITE)
        replaceFragment(setArguments(FragmentWebView(), bundle), true, false)
    }


}