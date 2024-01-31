package com.golfcart.utils.dialog_utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import com.golfcart.R

object DialogUtils {
    fun setLoadingDialog(activity: AppCompatActivity): Dialog {
        val dialog = Dialog(activity)
        dialog.show()
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.setContentView(R.layout.loader)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}