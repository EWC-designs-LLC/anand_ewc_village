package com.golfcart.ui.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.golfcart.utils.dialog_utils.DialogUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseActivity <T : ViewDataBinding, V : ViewModel> : AppCompatActivity() {

    /**
     * Abstract methods for fetching data
     * */
    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun getCurrentRunningActivity(): AppCompatActivity
    abstract fun getViewModel(): V
    abstract fun getBindingVariable(): Int

    private var mViewModel: V? = null
    var mCurrentFrg0: Fragment? = null
    private var mViewDataBinding: T? = null
    private var mDialogLoader: Dialog? = null

    fun getViewDataBinding(): ViewDataBinding {
        return mViewDataBinding!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
    }

    private fun performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this,getLayoutId())
        this@BaseActivity.mViewModel=mViewModel ?: getViewModel()
        mViewDataBinding!!.setVariable(getBindingVariable(),mViewModel)
        mViewDataBinding!!.executePendingBindings()
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        try {
            val view = currentFocus
            if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE)
                && view is EditText && !view.javaClass.name.startsWith("android.webkit.")
            ) {
                val scrcoords = IntArray(2)
                view.getLocationOnScreen(scrcoords)
                val x = ev.rawX + view.getLeft() - scrcoords[0]
                val y = ev.rawY + view.getTop() - scrcoords[1]
                if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (Objects.requireNonNull(
                    this.getSystemService(INPUT_METHOD_SERVICE)) as InputMethodManager).hideSoftInputFromWindow(this.window.decorView.applicationWindowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return super.dispatchTouchEvent(ev)
    }

    fun setCurrentFrag(frag: Fragment) {
        this@BaseActivity.mCurrentFrg0 = frag
    }

    fun removeObserver(mutableLiveData: MutableLiveData<*>) {
        mutableLiveData.removeObservers(this)
    }

    fun needShow(view:View,isVisible:Boolean){
         if (isVisible){
             view.visibility= View.VISIBLE
         }else{
             view.visibility= View.GONE
         }
    }

    fun getCurrentFragment(): Fragment? {
        return mCurrentFrg0
    }

    open fun timeFormatChangeIn24(inputString: String?): String? {
        var newFormat: String? = null
        val formatString = "yyyy-MM-dd kk:mm:ss"
        val sdf = SimpleDateFormat(formatString)
        try {
            val date = sdf.parse(inputString)
            val formatter = SimpleDateFormat("hh a")
            newFormat = formatter.format(date)
            println(".....Date...$newFormat")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return newFormat
    }

    open fun timeFormatChangeInDays(inputString: String?): String? {
        var newFormat: String? = null
        //2015-10-21T22:00:00.000Z
        // String formatString = "hh:mm:ss";
        val formatString = "yyyy-MM-dd HH:mm:s"
        val sdf = SimpleDateFormat(formatString)
        try {
            val date = sdf.parse(inputString)
            val formatter = SimpleDateFormat("EEE")
            newFormat = formatter.format(date)
            println(".....Date...$newFormat")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return newFormat
    }

    @SuppressLint("HardwareIds")
    fun getDeviceUniqueId(): String? {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    fun showLoader() {
        runOnUiThread {
            hideLoader()
            mDialogLoader = DialogUtils.setLoadingDialog(this@BaseActivity);
        }
    }

    fun hideLoader() {
        runOnUiThread {
            if (mDialogLoader != null && mDialogLoader!!.isShowing) {
                mDialogLoader?.dismiss()
            }
        }
    }

    fun showToast(message: String) {
        getCurrentRunningActivity().runOnUiThread {
            Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

}