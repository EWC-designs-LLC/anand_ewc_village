package com.golfcart.model.app_interface

interface OnBackEventCallBack {
    fun onBackTrigger(isFav: Boolean, favPosition: Int?){}
    fun onBackTrigger(){}
}