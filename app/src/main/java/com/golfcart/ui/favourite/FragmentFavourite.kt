package com.golfcart.ui.favourite

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.DialogEditFavNameBinding
import com.golfcart.databinding.FragmentFavouritesBinding
import com.golfcart.databinding.ItemFavoriteBinding
import com.golfcart.model.app_interface.EventsInterface
import com.golfcart.model.app_interface.OnBackEventCallBack
import com.golfcart.model.detail_data.DetailDataModel
import com.golfcart.model.favorite.FavoriteResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.detail.FragmentDetail
import com.golfcart.ui.favourite.adapter.DragDropAdapter
import com.golfcart.ui.favourite.adapter.ItemMoveCallback
import com.golfcart.ui.favourite.adapter.ItemMoveCallback.Companion.mDraggable
import com.golfcart.ui.village.DialogBinding
import com.golfcart.ui.village.VillageDialog
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.loadGlide
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentFavourite : VillageFragment<FragmentFavouritesBinding, FavouriteViewModel>(),
    DragDropAdapter.DragDropInterface, EventsInterface, OnBackEventCallBack {

    var favoriteAdapter: RecyclerViewGenericAdapter<FavoriteResponse.Data, ItemFavoriteBinding>? = null
    var favoriteList = ArrayList<FavoriteResponse.Data>()
    var adapter: DragDropAdapter? = null

    override fun showBottomBar(): Boolean {
        return true
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentFavourite
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            false, true, getString(R.string.favorites),
            false, -1, true, "", true, R.drawable.ic_edit_fav
        )
    }

    override fun getBindingVariable(): Int {
        return BR.favorite
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_favourites
    }

    override fun getViewModel(): FavouriteViewModel {
        val vm: FavouriteViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            if (adapter == null) {
                initialise()
                getFavourites()
            }
        }
    }

    private fun initialise() {
        getContainerActivity().onEventListener = this@FragmentFavourite
        getContainerActivity().onBackEventCallback = this@FragmentFavourite

    }

    private fun setAdapter() {
        getViewDataBinding()!!.apply {
            if (favoriteAdapter == null) {

                favoriteAdapter = RecyclerViewGenericAdapter(favoriteList, R.layout.item_favorite,
                    object : RecyclerCallback<ItemFavoriteBinding, FavoriteResponse.Data> {
                        override fun bindData(
                            binder: ItemFavoriteBinding,
                            model: FavoriteResponse.Data,
                            position: Int,
                            itemView: View?
                        ) {
                            binder.apply {

                                tvHeading.text = model.address
                                tvAddress.text = model.location
                                loadGlide(model.image,ivLogo,getContainerActivity())
                            }
                        }
                    })
                rvFav.adapter = favoriteAdapter
            }
        }
    }

    private fun getFavourites() {
        getViewModel().getFavourites(getContainerActivity(), getViewModel().getAppRepository().getUserId())
        getViewModel().favouriteLiveData.observe(getContainerActivity()) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateUi(it.data)
                    removeObserver(getViewModel().favoritesMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    showToast(getString(R.string.something_went_wrong))
                }
            }
        }
    }

    fun updateUi(response : FavoriteResponse?) {
        getViewDataBinding()!!.apply {
            if (!response?.data.isNullOrEmpty()) {
                favoriteList.clear()
                favoriteList.addAll(response?.data!!)

                adapter = DragDropAdapter(favoriteList, getContainerActivity(), this@FragmentFavourite)
                val callback: ItemTouchHelper.Callback = ItemMoveCallback(adapter!!)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(getViewDataBinding()!!.rvFav)
                rvFav.setAdapter(adapter)

                rvFav.show()
                layoutNoDataFound.tvNoDataFound.hide()
                tvHeading.show()
            } else {
                rvFav.hide()
                layoutNoDataFound.tvNoDataFound.show()
                tvHeading.hide()
            }
        }
    }

    override fun onEventClick() {
        mDraggable = !mDraggable
        if (mDraggable) {
            loadGlide(R.drawable.ic_save_fav,getContainerActivity().binding!!.ivRightIcon,getContainerActivity())
            vibrateDevice()
            adapter?.isEditEnable(true)
        } else {
            loadGlide(R.drawable.ic_edit_fav,getContainerActivity().binding!!.ivRightIcon,getContainerActivity())
            vibrateDevice()
            adapter?.isEditEnable(false)
            if (!favoriteList.isNullOrEmpty()) {
                updateFavOrder()
            }
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDraggable= false
    }

    private fun addFavourite(model: FavoriteResponse.Data?, position: Int) {

        val map = HashMap<String,String?>()

        map.put("user_id", getViewModel().getAppRepository().getUserId().toString())
        map.put("event_id", model?.id.toString())
        map.put("status",  "0")
        map.put("address", model?.address ?: VillageConstants.EMPTY)
        map.put("location", model?.location ?: VillageConstants.EMPTY)
        map.put("latitude", model?.latitude.toString())
        map.put("longitude",model?.longitude.toString())
        map.put("image", model?.image ?: VillageConstants.EMPTY)
        map.put("type", "N")
        map.put("contact_number", model?.contactNumber ?: VillageConstants.EMPTY)
        map.put("golf_number", model?.golfNumber ?: VillageConstants.EMPTY)
        map.put("submit", "submit")

        getViewModel().addFavourite(getContainerActivity(),map)
        getViewModel().removeLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    removeUi(position,model)
                    removeObserver(getViewModel().removeFavMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun removeUi(position: Int, model: FavoriteResponse.Data?) {
        if (!favoriteList.isNullOrEmpty()){
            favoriteList.remove(model)
            adapter?.updateArraList(favoriteList)
        }
    }

    private fun updateFavOrder() {

        val orderId = ArrayList<String>()
        val eventId = ArrayList<String>()

        favoriteList.forEachIndexed { index, model ->
            eventId.add(model.id.toString())
            orderId.add(index.toString())
        }

        val ids = eventId.toString().replace("[","").replace("]","")
        val order = orderId.toString().replace("[","").replace("]","")

        getViewModel().updateOrder(getContainerActivity(),getViewModel().getAppRepository().getUserId(),ids,order)
        getViewModel().updateOrderLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }
                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    removeObserver(getViewModel().updateOrderMutableLiveData)
                }
                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun updateFavName(model: FavoriteResponse.Data?, position: Int, title: String) {

        getViewModel().nameUpdate(getContainerActivity(),getViewModel().getAppRepository().getUserId(),
            model?.id!!,title)
        getViewModel().nameUpdateLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }
                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateNameUi(title,position)
                    removeObserver(getViewModel().nameUpdateMutableLiveData)
                }
                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun updateNameUi(title: String, position: Int) {
        favoriteList[position].location = title
        adapter!!.notifyDataSetChanged()
    }

    override fun onClick(model: FavoriteResponse.Data?, position: Int) {
            val bundle = Bundle()
            bundle.putString(VillageConstants.TOOLBAR_TITLE, model?.location)
            bundle.putString(VillageConstants.SNIPPET, model?.address)
            bundle.putInt(VillageConstants.POSITION, position)
            bundle.putString(VillageConstants.TYPE, model?.type)
            bundle.putString(VillageConstants.EVENT_ID, model?.id.toString())
            bundle.putBoolean(VillageConstants.IS_FROM_FAVOURITE, true)
            bundle.putString(VillageConstants.DESITNATION_LATITUDE, model?.latitude.toString())
            bundle.putString(VillageConstants.DESITNATION_LONGITUDE, model?.longitude.toString())
            bundle.putString(VillageConstants.DETAIL_MODEL, getViewModel().getReflectionUtils().convertPojoToJson(getDetailModel(model)))
            replaceFragment(setArguments(FragmentDetail(), bundle), true, true)
    }

    private fun getDetailModel(model: FavoriteResponse.Data?): DetailDataModel {
        val detailModel = DetailDataModel(model?.location,model?.address ?: "__________",
            model?.image,"" ,"")
        return detailModel
    }

    override fun onTitleClick(model: FavoriteResponse.Data?, position: Int) {
        updateNamDialog(model,position)
    }

    private fun updateNamDialog(model: FavoriteResponse.Data?, position: Int) {
        VillageDialog<DialogEditFavNameBinding>(
            getContainerActivity(), R.layout.dialog_edit_fav_name,
            object : DialogBinding<DialogEditFavNameBinding> {

                override fun onBind(binder: DialogEditFavNameBinding, dialog: Dialog) {
                    binder.apply {

                        etName.setText(model?.location)

                        btnCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        btnOk.setOnClickListener {
                            if (etName.toString().trim().isNullOrBlank() || etName.text.toString().trim().equals("")){
                                showToast("field can't be cancel")
                            }else{
                                updateFavName(model,position,etName.text.toString())
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }, false, true
        ).show(getContainerActivity().supportFragmentManager, VillageDialog::class.java.simpleName)
    }

    override fun onDelete(model: FavoriteResponse.Data?, position: Int) {
        addFavourite(model,position)
    }

    override fun onRowClear() {

    }

    override fun onBackTrigger(isFav: Boolean, favPosition: Int?) {
        if (!isFav){
            favoriteList.removeAt(favPosition!!)
            adapter!!.updateArraList(favoriteList)
        }
    }

}