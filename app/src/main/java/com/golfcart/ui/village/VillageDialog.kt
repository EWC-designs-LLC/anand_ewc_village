package com.golfcart.ui.village

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.golfcart.R

class VillageDialog<T : ViewDataBinding>(
    val activity: AppCompatActivity,
    val resId: Int,
    val binder: DialogBinding<T>,
    val setOutSideTouch: Boolean,
    val setFullScreenTheme: Boolean
) : DialogFragment() {

    private var mViewDataBinding: T? = null
    var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mViewDataBinding == null) {
            mViewDataBinding = DataBindingUtil.inflate<T>(
                LayoutInflater.from(activity),
                resId,
                null,
                false
            )
        }
        mView = mViewDataBinding?.root
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.onBind(mViewDataBinding!!, dialog!!)

        if (!setOutSideTouch) {
            dialog!!.setOnKeyListener { _, keyCode, event ->
                keyCode == KeyEvent.KEYCODE_BACK
            }
        } else {
            dialog?.setCancelable(false)
            dialog?.setCanceledOnTouchOutside(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (setFullScreenTheme) {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme)
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
    }

}