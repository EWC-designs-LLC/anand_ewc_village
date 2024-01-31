package com.golfcart.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.ActivitySplashBinding
import com.golfcart.model.remote.ApiResponse
import com.golfcart.model.signup.SignUpResponse
import com.golfcart.ui.base.BaseActivity
import com.golfcart.ui.terms.TermsAndConditionActivity
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.Logs.VillageLogs
import com.golfcart.utils.view_utils.show
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

    var binding: ActivitySplashBinding? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun getCurrentRunningActivity(): AppCompatActivity {
        return this@SplashActivity
    }

    override fun getViewModel(): SplashViewModel {
        val vm: SplashViewModel by viewModel()
        return vm
    }

    override fun getBindingVariable(): Int {
        return BR.splash
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performBinding()
        fadeanimation()
        startSignUp()
        setListiner()
        VillageLogs.printLog("uniqe_id",getDeviceUniqueId())
    }

    private fun setListiner() {
        binding!!.apply {
            btnGetStarted.setOnClickListener {
                signup()
            }
        }
    }

    private fun startSignUp() {
        lifecycleScope.launch {
            binding!!.apply {
                delay(3000)
                try {
                if (getViewModel().getAppRepository().isCustomerLogin()) {
                        signup()
                    } else {
                        btnGetStarted.show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun signup() {
        getViewModel().signup(this@SplashActivity, getDeviceUniqueId(), "asdijahduiashdudasd")
        getViewModel().getLiveData().observe(this@SplashActivity) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    updateUi(it.data)
                    removeObserver(getViewModel().mutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    showToast(getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun updateUi(data: SignUpResponse?) {

        getViewModel().getAppRepository().setUserId(data?.user_id!!)
        getViewModel().getAppRepository().setCustomerLogin(true)

        if (getViewModel().getAppRepository().isTermsConditions()) {
            startActivity(Intent(this@SplashActivity, VillageActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this@SplashActivity, TermsAndConditionActivity::class.java))
            finish()
        }
    }

    private fun performBinding() {
        binding =
            DataBindingUtil.setContentView(this@SplashActivity, R.layout.activity_splash)
        binding?.executePendingBindings()
    }

    private fun fadeanimation() {
        val animation = AnimationUtils.loadAnimation(this@SplashActivity, R.anim.fade)
        binding!!.ivAppLogo.startAnimation(animation)
    }

}