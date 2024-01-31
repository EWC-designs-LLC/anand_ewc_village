package com.golfcart.ui.notification

import android.graphics.Color
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.FragmentNotificationBinding
import com.golfcart.databinding.ItemNotificationBinding
import com.golfcart.model.app_interface.EventsInterface
import com.golfcart.model.notification.NotificationResponse
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.swipe_helper.SwipeHelper
import com.golfcart.utils.village_constant.VillageConstants
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentNotification : VillageFragment<FragmentNotificationBinding, NotificationViewModel>(),
    EventsInterface {

    var notificationAdapter: RecyclerViewGenericAdapter<NotificationResponse.Data, ItemNotificationBinding>? =
        null
    var notificationList = ArrayList<NotificationResponse.Data>()

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentNotification
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, requireArguments().getString(VillageConstants.TOOLBAR_TITLE, ""), false,
            -1, true, "Clear All", false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.notification
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_notification
    }

    override fun getViewModel(): NotificationViewModel {
        val vm: NotificationViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            initialise()
            setAdapter()
            getNotificationList()
            badgeClear()
        }
    }

    private fun initialise() {
        getContainerActivity().onEventListener = this@FragmentNotification
    }

    private fun getNotificationList() {
        getViewModel().getNotification(
            getContainerActivity(),
            getViewModel().getAppRepository().getUserId()
        )
        getViewModel().notificationliveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateUi(it.data)
                    removeObserver(getViewModel().notificationMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun badgeClear() {
        getViewModel().badgeClear(
            getContainerActivity(),
            getViewModel().getAppRepository().getUserId()
        )
        getViewModel().badgeLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    removeObserver(getViewModel().badgeMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun clearAllNotification() {
        getViewModel().clearAllNotification(
            getContainerActivity(),
            getViewModel().getAppRepository().getUserId()
        )
        getViewModel().clearLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    clearAll(it.data)
                    removeObserver(getViewModel().clearMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }

    private fun clearAll(data: NotificationResponse?) {
        notificationList.clear()
        notificationAdapter!!.updateAdapterList(notificationList)
    }

    private fun setAdapter() {
        getViewDataBinding()!!.apply {
            getContainerActivity().runOnUiThread {
                if (notificationAdapter == null) {

                    notificationAdapter =
                        RecyclerViewGenericAdapter(notificationList, R.layout.item_notification,
                            object :
                                RecyclerCallback<ItemNotificationBinding, NotificationResponse.Data> {
                                override fun bindData(
                                    binder: ItemNotificationBinding,
                                    model: NotificationResponse.Data,
                                    position: Int,
                                    itemView: View?
                                ) {
                                    binder.apply {
                                        tvTitle.text = model.message
                                        tvSubtitle.text = model.created_at
                                    }
                                }
                            })
                    rvNotification.adapter = notificationAdapter
                }
            }
        }
    }

    private fun updateUi(response: NotificationResponse?) {
        if (!response?.data.isNullOrEmpty()) {
            notificationList.clear()
            notificationList.addAll(response?.data!!)
            notificationAdapter!!.updateAdapterList(notificationList)
        }
    }

    override fun onEventClick() {
        if (notificationList.isNotEmpty()) {
            clearAllNotification()
        }
    }

    private fun SwipeDelete() {
        getViewDataBinding()!!.apply {
            rvNotification.setLayoutManager(LinearLayoutManager(getContainerActivity()))
            val swipeHelper: SwipeHelper =
                object : SwipeHelper(getContainerActivity(), rvNotification) {
                    override fun instantiateUnderlayButton(
                        viewHolder: RecyclerView.ViewHolder?,
                        underlayButtons: MutableList<UnderlayButton?>
                    ) {
                        underlayButtons.add(UnderlayButton(
                            "Delete",
                            0,
                            Color.parseColor("#FF3C30"),
                            object : UnderlayButtonClickListener {
                                override fun onClick(pos: Int) {
                                    notificationList.removeAt(pos)
                                    notificationAdapter!!.updateAdapterList(notificationList)
                                    deleteNotification(notificationList.get(pos).id!!)
                                }
                            }
                        ))
                    }
                }
            val itemTouchHelper = ItemTouchHelper(swipeHelper)
            itemTouchHelper.attachToRecyclerView(rvNotification)
        }
    }

    private fun deleteNotification(notificationId:Int) {
        getViewModel().deleteNotification(
            getContainerActivity(),
            getViewModel().getAppRepository().getUserId(),notificationId
        )
        getViewModel().deleteLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    removeObserver(getViewModel().deleteMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    handleError(it.error)
                }
            }
        }
    }
}