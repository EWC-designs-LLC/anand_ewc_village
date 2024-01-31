package com.golfcart.ui.golf_course

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.FragmentGolfCourseBinding
import com.golfcart.databinding.ItemGolfCourseBinding
import com.golfcart.model.golf_course.GolfCourseModel
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.golf_course_sub.FragmentGolfCourseSubcategory
import com.golfcart.ui.village.VillageFragment
import com.golfcart.ui.webview.FragmentWebView
import com.golfcart.utils.village_constant.VillageConstants
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentGolfCourse : VillageFragment<FragmentGolfCourseBinding, GolfcourseViewModel>() {

    var golfCouseAdapter: RecyclerViewGenericAdapter<GolfCourseModel.Data, ItemGolfCourseBinding>? =
        null
    var catgoriesList = ArrayList<GolfCourseModel.Data>()
    var category_id = -1

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentGolfCourse
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, requireArguments().getString(VillageConstants.TOOLBAR_TITLE, ""),
            false, -1, false, "",
            false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.golf_course
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_golf_course
    }

    override fun getViewModel(): GolfcourseViewModel {
        val vm: GolfcourseViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            getBundleData()
            setAdapter()
            getGolfCourseCategories()
        }
    }

    private fun getBundleData() {
        category_id = requireArguments().getInt(VillageConstants.CATEGORY_ID, -1)
    }

    private fun getGolfCourseCategories() {
        getViewModel().getGolfCourseCategories(getContainerActivity(), category_id)
        getViewModel().golfCourseliveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateUi(it.data)
                    removeObserver(getViewModel().golfCourseMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun updateUi(response: GolfCourseModel?) {
        if (!response?.data.isNullOrEmpty()) {
            catgoriesList.clear()
            catgoriesList.addAll(response?.data!!)
            golfCouseAdapter!!.updateAdapterList(catgoriesList)
        }
    }

    private fun setAdapter() {
        getViewDataBinding()!!.apply {
            getContainerActivity().runOnUiThread {
                if (golfCouseAdapter == null) {

                    golfCouseAdapter =
                        RecyclerViewGenericAdapter(catgoriesList, R.layout.item_golf_course,
                            object : RecyclerCallback<ItemGolfCourseBinding, GolfCourseModel.Data> {
                                override fun bindData(
                                    binder: ItemGolfCourseBinding,
                                    model: GolfCourseModel.Data,
                                    position: Int,
                                    itemView: View?
                                ) {
                                    binder.apply {

                                        tvTitle.text = model.name

                                        itemView?.setOnClickListener {
                                            if (model.value!!.startsWith("http")) {
                                                navigateToWebView(model)
                                            } else {
                                                navigateToSubcategory(model)
                                            }
                                        }

                                    }
                                }
                            })
                    rvGolfcourse.adapter = golfCouseAdapter
                }
            }
        }
    }

    private fun navigateToWebView(model: GolfCourseModel.Data) {
        val bundle = Bundle()
        bundle.putString(VillageConstants.TOOLBAR_TITLE, model.name)
        bundle.putString(VillageConstants.WEB_URL,model.value)
        replaceFragment(setArguments(FragmentWebView(), bundle), true, false)
    }

    private fun navigateToSubcategory(model: GolfCourseModel.Data) {
        val bundle = Bundle()
        bundle.putString(VillageConstants.TOOLBAR_TITLE, model.name)
        bundle.putInt(VillageConstants.CATEGORY_ID, model.categoryId!!)
        bundle.putInt(VillageConstants.SUB_CATEGORY_ID, model.id!!)
        replaceFragment(setArguments(FragmentGolfCourseSubcategory(), bundle), true, true)
    }

}