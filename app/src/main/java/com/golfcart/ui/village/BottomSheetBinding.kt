package com.golfcart.ui.village

import android.app.Dialog
import androidx.databinding.ViewDataBinding

interface BottomSheetBinding<T : ViewDataBinding> {
    fun onBind(binder: T, dialog: Dialog)
}