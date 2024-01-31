package com.golfcart.model.app_interface

import androidx.fragment.app.Fragment
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration

interface FragmentView {
    fun showBottomBar() : Boolean?

    fun getCurrentFragment(): Fragment?

    fun showToolbar(): Boolean?

    fun configureToolbar(): ToolbarConfiguration?
}