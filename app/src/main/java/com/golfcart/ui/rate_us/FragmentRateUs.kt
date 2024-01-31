package com.golfcart.ui.rate_us

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.DialogRateUsBinding
import com.golfcart.databinding.FragmentRateUsBinding
import com.golfcart.databinding.ItemRateUsBinding
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.village.DialogBinding
import com.golfcart.ui.village.VillageDialog
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.loadGlide
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentRateUs : VillageFragment<FragmentRateUsBinding, RateUsViewModel>() {

    var rateAdapter: RecyclerViewGenericAdapter<Boolean, ItemRateUsBinding>? = null
    var rateList = ArrayList<Boolean>()
    var count = 0
    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentRateUs
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, requireArguments().getString(VillageConstants.TOOLBAR_TITLE, ""),
            false, -1, false, "", false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.rateus
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_rate_us
    }

    override fun getViewModel(): RateUsViewModel {
        val vm: RateUsViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        setAdapter()
        setListiner()
    }

    private fun setListiner() {
        getViewDataBinding()!!.apply {
            submitBtn.setOnClickListener {
                rateList.forEachIndexed { index, b ->
                    if (b){
                        count = index + 1
                    }
                }

                if (count == 5){
                    showRateDialog(true)
                }else{
                    showRateDialog(false)
                }
            }
        }
    }

    private fun setData() {
        rateList.clear()
        rateList.add(false)
        rateList.add(false)
        rateList.add(false)
        rateList.add(false)
        rateList.add(false)
    }

    private fun setAdapter() {
        getViewDataBinding()!!.apply {
            getContainerActivity().runOnUiThread {
                if (rateAdapter == null) {

                    rateAdapter = RecyclerViewGenericAdapter(rateList, R.layout.item_rate_us,
                        object : RecyclerCallback<ItemRateUsBinding, Boolean> {
                            override fun bindData(
                                binder: ItemRateUsBinding,
                                model: Boolean,
                                position: Int,
                                itemView: View?
                            ) {
                                binder.apply {

                                    tvTitle.text = "${position + 1}"

                                    if (model) {
                                        loadGlide(
                                            R.drawable.ic_star_active,
                                            ivLogo,
                                            getContainerActivity()
                                        )
                                    } else {
                                        loadGlide(
                                            R.drawable.ic_star_grey,
                                            ivLogo,
                                            getContainerActivity()
                                        )
                                    }

                                    ivLogo.setOnClickListener {
                                        activeStar(position)
                                    }

                                }
                            }
                        })
                    rvRateUs.adapter = rateAdapter
                }
            }
        }
    }

    private fun activeStar(position: Int) {
        rateList.forEachIndexed { index, bol ->
            if (position >= index) {
                rateList[index] = true
            } else {
                rateList[index] = false
            }
        }
        rateAdapter!!.updateAdapterList(rateList)
    }

    fun showRateDialog(status: Boolean) {
        VillageDialog<DialogRateUsBinding>(
            getContainerActivity(), R.layout.dialog_rate_us,
            object : DialogBinding<DialogRateUsBinding> {

                override fun onBind(binder: DialogRateUsBinding, dialog: Dialog) {
                    binder.apply {
                        tvTitle.hide()
                        if (status){
                            tvSubtitle.text=getString(R.string.review)
                        }else{
                            tvSubtitle.text=getString(R.string.fixit)
                            tvEmail.show()
                            okBtn.hide()
                        }

                        ivClose.setOnClickListener {
                            dialog.dismiss()
                        }
                        okBtn.setOnClickListener {
                            gotoReview()
                            dialog.dismiss()
                        }
                        tvEmail.setOnClickListener {
                            gotoMail()
                            dialog.dismiss()
                        }
                    }
                }
            }, false, true
        ).show(getContainerActivity().supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    fun gotoReview(){
        val appPackageName =
            getContainerActivity().packageName // getPackageName() from Context or Activity object

        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

}