package com.golfcart.model.app_interface

import android.os.Bundle
import androidx.fragment.app.Fragment

interface AppNavigationInterface {
    fun replaceFragment(fragment: Fragment, addToStack: Boolean, showAnimation: Boolean)

    fun setArguments(fragment: Fragment, bundle: Bundle): Fragment

    /**
     * With Call back
     * */
    fun showErrorDialog(message: String, listener: OnMessageButtonClickListener)

    /**
     * With out call back
     * */
    fun showErrorDialog(message: String)

    fun showToasts(message: String)


    fun showLoader()
    fun hideLoader()
}