package com.golfcart.ui.village

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.golfcart.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class VillageBottomSheet <T: ViewDataBinding> {

    var binding:T?=null
    var dialog: BottomSheetDialog?=null

    constructor(activity: AppCompatActivity, resId: Int, binder: BottomSheetBinding<T>){
        binding = DataBindingUtil.inflate<T>(LayoutInflater.from(activity), resId , null, false)
        dialog = BottomSheetDialog(activity, R.style.bottom)
        dialog!!.setContentView(binding!!.root)
        binder.onBind(binding!!, dialog!!)
        binding!!.executePendingBindings()
    }

    fun show(){
        dialog?.show()
    }

}