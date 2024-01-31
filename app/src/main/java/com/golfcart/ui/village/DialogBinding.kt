package com.golfcart.ui.village

import android.app.Dialog
import androidx.databinding.ViewDataBinding

interface DialogBinding<T : ViewDataBinding> {
    fun onBind(binder: T, dialog: Dialog)
}