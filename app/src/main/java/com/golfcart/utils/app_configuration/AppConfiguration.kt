package com.golfcart.utils.app_configuration

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.golfcart.R
import com.google.android.material.snackbar.Snackbar

object AppConfiguration {

    const val APP_VERSION = "1.0"
    const val DEVICE_TYPE = "A"
    const val APPLICATION_JSON = "application/json"

    fun showExitWarning(layout: ViewGroup, activity: AppCompatActivity) {
        val snack = Snackbar.make(layout, "To exit, Tap back button again.", Snackbar.LENGTH_SHORT)

        snack.setTextColor(ContextCompat.getColor(activity, R.color.white))
        snack.setActionTextColor(ContextCompat.getColor(activity, R.color.white))
        snack.setAction("EXIT NOW") {
            activity.finish()
        }
        val snackBarView = snack.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.app_blue))
        snack.show()
    }


}