package com.golfcart.ui.point_to_point

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.golfcart.BR
import com.golfcart.BuildConfig
import com.golfcart.R
import com.golfcart.databinding.FragmentPointVersionBinding
import com.golfcart.model.app_interface.OnBackEventCallBack
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.search.FragmentSearch
import com.golfcart.ui.version.AppVersionViewModel
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.village_constant.VillageConstants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPointContainer : VillageFragment<FragmentPointVersionBinding, PointViewModel>() {

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentPointContainer
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, getString(R.string.point_to_point),
            false, -1, false, "", false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.point
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_point_version
    }

    override fun getViewModel(): PointViewModel {
        val vm: PointViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            lifecycleScope.launch {
                startPointFragment()
            }
        }
    }

    private fun startPointFragment() {
        val pointToPoint = FragmentPointToPoint()
        val transaction = getContainerActivity().supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_Container, pointToPoint)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun removePointFragment() {
        // Create a FragmentTransaction
        val transaction = getContainerActivity().supportFragmentManager.beginTransaction()
        // Remove FragmentB using the "remove" method
        transaction.remove(this)
        // Commit the transaction
        transaction.commit()
    }
}