package com.golfcart.ui.contact_us

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.FragmentContactUsBinding
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.village_constant.VillageConstants
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentContactUs : VillageFragment<FragmentContactUsBinding, ContactUsViewModel>() {

    data class MoreData(val name:String, val image:Int)

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentContactUs
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, requireArguments().getString(VillageConstants.TOOLBAR_TITLE, "") ,
            false, -1, false, "", false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.contact_us
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_contact_us
    }

    override fun getViewModel(): ContactUsViewModel {
        val vm: ContactUsViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            tvSubtitle.setOnClickListener {
                gotoMail()
            }
        }
    }


}
