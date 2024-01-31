package com.golfcart.utils.view_utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import java.io.File

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun loadGlide(resId: Int?, img: AppCompatImageView, context: Context) {
    Glide.with(context).load(resId).into(img)
}

fun loadGlide(resId: String?, img: AppCompatImageView, context: Context) {
    Glide.with(context).load(resId).into(img)
}

fun loadGlide(resId: String?, img: AppCompatImageView, context: Context, placeholder: Int) {
    Glide.with(context).load(resId).placeholder(placeholder).error(placeholder).into(img)
}

fun loadGlide(resId: Int?, img: AppCompatImageView, context: Context, placeholder: Int) {
    Glide.with(context).load(resId).placeholder(placeholder).error(placeholder).into(img)
}

fun loadGlide(resId: File?, img: AppCompatImageView, context: Context) {
    Glide.with(context).load(resId).into(img)
}

fun loadGlide(resId: File?, img: AppCompatImageView, context: Context, isCenterCrop: Boolean) {
    if (isCenterCrop) {
        Glide.with(context).load(resId).centerCrop().into(img)
    } else {
        Glide.with(context).load(resId).into(img)
    }

}

fun loadGlide(resId: String?, img: AppCompatImageView, placeholder: Int) {
    if (resId != null && resId != "") {
        Glide.with(img.context).load(resId).placeholder(placeholder).error(placeholder).into(img)
    } else {
        Glide.with(img.context).load("").placeholder(placeholder).error(placeholder).into(img)
    }
}

fun loadGif(resId: Int?, img: AppCompatImageView) {
    Glide.with(img.context).asGif().load(resId).into(img)
}