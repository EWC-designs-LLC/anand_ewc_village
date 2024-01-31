package com.golfcart.ui.version

import androidx.fragment.app.Fragment
import com.golfcart.BR
import com.golfcart.BuildConfig
import com.golfcart.R
import com.golfcart.databinding.FragmentAppVersionBinding
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.village.VillageFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentAppVersion : VillageFragment<FragmentAppVersionBinding, AppVersionViewModel>() {

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentAppVersion
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
        return BR.version
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_app_version
    }

    override fun getViewModel(): AppVersionViewModel {
        val vm: AppVersionViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            tvAppVersion.setText(BuildConfig.VERSION_NAME)
        }
    }

}