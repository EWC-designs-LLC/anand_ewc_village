package com.golfcart.ui.terms

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.ActivityTermsAndConditionBinding
import com.golfcart.ui.base.BaseActivity
import com.golfcart.ui.tutorial.TutorialActivity
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants.TERMS_AND_CONDITION
import org.koin.androidx.viewmodel.ext.android.viewModel

class TermsAndConditionActivity : BaseActivity<ActivityTermsAndConditionBinding, TermsAndConditionViewModel>() {

    var binding: ActivityTermsAndConditionBinding? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_terms_and_condition
    }

    override fun getCurrentRunningActivity(): AppCompatActivity {
        return this@TermsAndConditionActivity
    }

    override fun getViewModel(): TermsAndConditionViewModel {
        val vm: TermsAndConditionViewModel by viewModel()
        return vm
    }

    override fun getBindingVariable(): Int {
        return BR.terms_condition
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performBinding()
        setWebView()
        setListiner()
    }

    private fun setListiner() {
        binding!!.apply {
            btnAccpet.setOnClickListener {
                getViewModel().getAppRepository().setIsTermsConditions(true)
                startActivity(Intent(this@TermsAndConditionActivity, TutorialActivity::class.java))
                finish()
            }
            btnDecline.setOnClickListener {
                finish()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        binding!!.apply {
            webview.settings.javaScriptEnabled= true
            webview.loadUrl(TERMS_AND_CONDITION)
            webview.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    layoutButtons.show()
                    view.loadUrl(url)
                    return true
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    if (webview.getProgress() == 100) {
                        layoutButtons.show()
                    }
                }
            })
        }
    }

    private fun performBinding() {
        binding =
            DataBindingUtil.setContentView(this@TermsAndConditionActivity, R.layout.activity_terms_and_condition)
        binding?.executePendingBindings()
    }
}