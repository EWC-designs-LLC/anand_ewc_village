package com.golfcart.ui.search

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.FragmentSearchBinding
import com.golfcart.databinding.ItemAddressPlaceBinding
import com.golfcart.databinding.ItemSearchCategoriesBinding
import com.golfcart.model.app_interface.EventsInterface
import com.golfcart.model.detail_data.DetailDataModel
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.search.AddressPlaceSearchModel
import com.golfcart.model.search.SearchCategoriesModel
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.address_place_search.FragmentAddressPlaceSearch
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.detail.FragmentDetail
import com.golfcart.ui.golf_course_sub.FragmentGolfCourseSubcategory
import com.golfcart.ui.point_to_point.FragmentPointToPoint
import com.golfcart.ui.village.VillageActivity
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.loadGlide
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.EMPTY
import com.golfcart.utils.village_constant.VillageConstants.IS_FROM_POINT
import com.golfcart.utils.village_constant.VillageConstants.IS_FROM_POINT_TO_POINT
import com.golfcart.utils.village_constant.VillageConstants.POINT_TYPE
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentSearch : VillageFragment<FragmentSearchBinding, SearchViewModel>(), EventsInterface {

    var searchAdapter: RecyclerViewGenericAdapter<AddressPlaceSearchModel.Data, ItemAddressPlaceBinding>? =
        null
    var searchList = ArrayList<AddressPlaceSearchModel.Data>()

    var categoryAdapter: RecyclerViewGenericAdapter<SearchCategoriesModel.Data, ItemSearchCategoriesBinding>? =
        null
    var categoriesList = ArrayList<SearchCategoriesModel.Data>()

    val ADDRESS = "1"
    val PLACE = "2"

    val ADDRESS_TEXT = "address"
    val PLACE_TEXT = "place"
    val CATEGORIES_TEXT = "categories"
    var isAnyTabSeleted = false
    var isFromPoint = false
    var pointType = "1"

    override fun showBottomBar(): Boolean {
        if (arguments != null) {
            return !arguments?.getBoolean(IS_FROM_POINT_TO_POINT, false)!!
        } else {
            return true
        }
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentSearch
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        try {
            return ToolbarConfiguration(
                requireArguments().getBoolean(IS_FROM_POINT_TO_POINT, false), true, "Search",
                false, R.drawable.ic_search_home, false, "",
                false, -1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return ToolbarConfiguration(
                false, true, "Search",
                false, R.drawable.ic_search_home, false, "",
                false, -1
            )
        }
    }

    override fun getBindingVariable(): Int {
        return BR.search
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_search
    }

    override fun getViewModel(): SearchViewModel {
        val vm: SearchViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            getBundleData()
            setListiner()
            setViews()
            setBackgroundColor(ADDRESS_TEXT)
            setAddressAdapter()
            getAddressPlace(ADDRESS)
        }
    }

    private fun setViews() {
        if (isFromPoint) {
            getContainerActivity().binding!!.layoutBack.show()
        } else {
            getContainerActivity().binding!!.layoutBack.hide()
        }
    }

    private fun getBundleData() {
        try {
            if (arguments != null) {
                isFromPoint = requireArguments().getBoolean(IS_FROM_POINT_TO_POINT, false)
                pointType = requireArguments().getString(POINT_TYPE, "1")
                IS_FROM_POINT = isFromPoint
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListiner() {
        getViewDataBinding()!!.apply {
            getContainerActivity().onEventListener = this@FragmentSearch

            btnAddress.tag = 1
            btnAddress.setOnClickListener {
                setBackgroundColor(ADDRESS_TEXT)
                setAddressAdapter()
                getAddressPlace(ADDRESS)
            }

            btnPlace.setOnClickListener {
                setBackgroundColor(PLACE_TEXT)
                setAddressAdapter()
                getPlaceSearch(PLACE)
            }

            btnCategory.setOnClickListener {
                setBackgroundColor(CATEGORIES_TEXT)
                setCategoriesAdapter()
                getCategories()
            }

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().isNullOrBlank()) {
                        if (categoryAdapter != null) {
                            categoryAdapter!!.updateAdapterList(categoriesList)
                        }
                    } else {
                        filterList(s.toString())
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            tvSearch.setOnClickListener {
                if (btnAddress.tag == 1) {
                    val bundle = Bundle()
                    bundle.putString(VillageConstants.SCREE_NAME, ADDRESS_TEXT)
                    bundle.putBoolean(IS_FROM_POINT_TO_POINT, isFromPoint)
                    bundle.putString(POINT_TYPE, pointType)
                    replaceFragment(setArguments(FragmentAddressPlaceSearch(), bundle), true, true)
                } else {
                    val bundle = Bundle()
                    bundle.putString(VillageConstants.SCREE_NAME, PLACE_TEXT)
                    bundle.putBoolean(IS_FROM_POINT_TO_POINT, isFromPoint)
                    bundle.putString(POINT_TYPE, pointType)
                    replaceFragment(setArguments(FragmentAddressPlaceSearch(), bundle), true, true)
                }
            }
        }
    }

    private fun filterList(text: String) {
        val list = categoriesList.filter { it.name!!.startsWith(text, true) }
        categoryAdapter!!.updateAdapterList(list as ArrayList<SearchCategoriesModel.Data>)
    }

    private fun getAddressPlace(searchType: String) {
        getViewModel().getAddressPlace(
            getContainerActivity(),
            getViewModel().getAppRepository().getUserId().toString(),
            searchType
        )
        getViewModel().searchLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateSearch(it.data)
                    removeObserver(getViewModel().searchMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun getPlaceSearch(searchType: String) {
        getViewModel().getPlaceSearch(
            getContainerActivity(),
            getViewModel().getAppRepository().getUserId().toString(),
            searchType
        )
        getViewModel().placeLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateSearch(it.data)
                    removeObserver(getViewModel().placeMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun updateSearch(response: AddressPlaceSearchModel?) {
        if (response != null && !response.data.isNullOrEmpty()) {
            searchList.clear()
            searchList.addAll(response.data!!)
            searchAdapter!!.updateAdapterList(searchList)
        } else {
            searchList.clear()
            searchAdapter!!.updateAdapterList(searchList)
        }
    }


    private fun getCategories() {
        getViewModel().getSearchCategories(getContainerActivity(), "C")
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

    private fun updateCategoriesUi(response: SearchCategoriesModel?) {
        if (!response?.data.isNullOrEmpty()) {
            categoriesList.clear()
            response?.data?.forEachIndexed { index, model ->
                allowedCategories().forEachIndexed { index1, nm ->
                    if (model.name!!.equals(nm)) {
                        categoriesList.add(model)
                    }
                }
            }
            categoryAdapter!!.updateAdapterList(categoriesList)
        }
    }

    private fun setAddressAdapter() {
        getViewDataBinding()!!.apply {
            getContainerActivity().runOnUiThread {

                searchAdapter = RecyclerViewGenericAdapter(searchList, R.layout.item_address_place,
                    object :
                        RecyclerCallback<ItemAddressPlaceBinding, AddressPlaceSearchModel.Data> {
                        override fun bindData(
                            binder: ItemAddressPlaceBinding,
                            model: AddressPlaceSearchModel.Data,
                            position: Int,
                            itemView: View?
                        ) {
                            binder.apply {

                                tvTitle.text = model.place

                                itemView!!.setOnClickListener {
                                    if (isFromPoint) {
                                        navigateToPoint(model)
                                    } else {
                                        navigateToCityHall(model)
                                    }
                                }

                            }
                        }
                    })
                rvSearch.adapter = searchAdapter
            }
        }
    }

    private fun navigateToCityHall(model: AddressPlaceSearchModel.Data) {
        val bundle = Bundle()
        bundle.putString(VillageConstants.TOOLBAR_TITLE, model.address ?: EMPTY)
        bundle.putString(VillageConstants.SNIPPET, model.place)
        bundle.putString(VillageConstants.TYPE, VillageConstants.TYPE_SEARCH)
        bundle.putString(VillageConstants.EVENT_ID, model.googleId)
        bundle.putString(VillageConstants.DESITNATION_LATITUDE, model.latitude)
        bundle.putString(VillageConstants.DESITNATION_LONGITUDE, model.longitude)
        bundle.putString(
            VillageConstants.DETAIL_MODEL,
            getViewModel().getReflectionUtils().convertPojoToJson(getDetailModel(model))
        )
        replaceFragment(setArguments(FragmentDetail(), bundle), true, true)
    }

    private fun getDetailModel(model: AddressPlaceSearchModel.Data): DetailDataModel {
        val detailModel = DetailDataModel(
            model.place ?: EMPTY, model.address,
            EMPTY, EMPTY, ""
        )
        return detailModel
    }

    private fun navigateToPoint(model: AddressPlaceSearchModel.Data) {
        if (pointType.equals("1")) {
            VillageActivity.point_souce_lat =
                model.latitude?.toDouble() ?: VillageConstants.VILLAGE_LAT_START
            VillageActivity.point_souce_lng =
                model.longitude?.toDouble() ?: VillageConstants.VILLAGE_LNG_START
            VillageActivity.point_source_address = model.place.toString()
        } else {
            VillageActivity.point_des_lat =
                model.latitude?.toDouble() ?: VillageConstants.VILLAGE_LAT_START
            VillageActivity.point_des_lng =
                model.longitude?.toDouble() ?: VillageConstants.VILLAGE_LNG_START
            VillageActivity.point_des_address = model.place.toString()
        }

        val bundle = Bundle()
        bundle.putBoolean(VillageConstants.IS_POINT_SEARCH, true)
        bundle.putString(POINT_TYPE, pointType)
        replaceFragment(setArguments(FragmentPointToPoint(), bundle), true, false)
    }

    private fun setCategoriesAdapter() {
        getViewDataBinding()!!.apply {
            getContainerActivity().runOnUiThread {

                categoryAdapter =
                    RecyclerViewGenericAdapter(categoriesList, R.layout.item_search_categories,
                        object :
                            RecyclerCallback<ItemSearchCategoriesBinding, SearchCategoriesModel.Data> {
                            override fun bindData(
                                binder: ItemSearchCategoriesBinding,
                                model: SearchCategoriesModel.Data,
                                position: Int,
                                itemView: View?
                            ) {
                                binder.apply {

                                    loadGlide(
                                        model.image,
                                        ivLogo,
                                        getContainerActivity(),
                                        R.drawable.placeholder
                                    )
                                    tvTitle.text = model.name

                                    itemView?.setOnClickListener {
                                        val bundle = Bundle()
                                        bundle.putString(VillageConstants.TOOLBAR_TITLE, model.name)
                                        bundle.putInt(VillageConstants.CATEGORY_ID, model.id!!)
                                        bundle.putInt(
                                            VillageConstants.SUB_CATEGORY_ID,
                                            model.subCatStatus!!
                                        )
                                        bundle.putBoolean(IS_FROM_POINT_TO_POINT, isFromPoint)
                                        bundle.putString(POINT_TYPE, pointType)
                                        replaceFragment(
                                            setArguments(
                                                FragmentGolfCourseSubcategory(),
                                                bundle
                                            ), true, true
                                        )
                                    }
                                }
                            }
                        })
                rvSearch.adapter = categoryAdapter

            }
        }
    }

    private fun setBackgroundColor(text: String) {
        getViewDataBinding()!!.apply {
            if (text == ADDRESS_TEXT) {
                btnAddress.background =
                    ContextCompat.getDrawable(getContainerActivity(), R.drawable.bg_search_blue)
                btnPlace.background =
                    ContextCompat.getDrawable(getContainerActivity(), R.color.transparent)
                btnCategory.background =
                    ContextCompat.getDrawable(getContainerActivity(), R.color.transparent)
                btnAddress.setTextColor(Color.parseColor("#ffffff"))
                btnCategory.setTextColor(Color.parseColor("#000000"))
                btnPlace.setTextColor(Color.parseColor("#000000"))
                btnAddress.tag = 1
                btnCategory.tag = 0
                btnPlace.tag = 0
                tvRecentHeading.show()
                etSearch.hide()
                tvSearch.show()
                isAnyTabSeleted = true
            } else if (text == PLACE_TEXT) {
                btnPlace.background =
                    ContextCompat.getDrawable(
                        getContainerActivity(),
                        R.drawable.bg_search_center_button
                    )
                btnAddress.background =
                    ContextCompat.getDrawable(getContainerActivity(), R.color.transparent)
                btnCategory.background =
                    ContextCompat.getDrawable(getContainerActivity(), R.color.transparent)

                btnAddress.setTextColor(Color.parseColor("#000000"))
                btnCategory.setTextColor(Color.parseColor("#000000"))
                btnPlace.setTextColor(Color.parseColor("#ffffff"))
                btnAddress.tag = 0
                btnCategory.tag = 0
                btnPlace.tag = 1
                tvRecentHeading.show()
                etSearch.hide()
                tvSearch.show()
                isAnyTabSeleted = true
            } else if (text == CATEGORIES_TEXT) {
                btnCategory.background =
                    ContextCompat.getDrawable(
                        getContainerActivity(),
                        R.drawable.bg_search_right_button
                    )
                btnAddress.background =
                    ContextCompat.getDrawable(getContainerActivity(), R.color.transparent)
                btnPlace.background =
                    ContextCompat.getDrawable(getContainerActivity(), R.color.transparent)

                btnAddress.setTextColor(Color.parseColor("#000000"))
                btnCategory.setTextColor(Color.parseColor("#ffffff"))
                btnPlace.setTextColor(Color.parseColor("#000000"))
                btnAddress.tag = 0
                btnCategory.tag = 1
                btnPlace.tag = 0
                tvRecentHeading.hide()
                etSearch.show()
                tvSearch.hide()
            }
        }
    }

    private fun allowedCategories(): ArrayList<String> {
        val filterList = ArrayList<String>()
        filterList.add("Air Gun Range")
        filterList.add("Country Clubs")
        filterList.add("Dog Parks")
        filterList.add("Golf Cart Gas Stations")
        filterList.add("Golf Course")
        filterList.add("Neighborhood Villages")
        filterList.add("Pools")
        filterList.add("Recreation Centers")
        filterList.add("Softball Fields")
        filterList.add("Town Squares")
        filterList.add("Villas")
        return filterList
    }

    override fun onEventClick() {
        val bundle = Bundle()
        bundle.putBoolean(VillageConstants.IS_POINT_SEARCH, true)
        bundle.putString(VillageConstants.POINT_TYPE, pointType)
        replaceFragment(setArguments(FragmentPointToPoint(), bundle), true, false)
    }

}