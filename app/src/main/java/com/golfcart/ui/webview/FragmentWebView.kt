package com.golfcart.ui.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.view.View
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.golfcart.R
import com.golfcart.BR
import com.golfcart.databinding.FragmentWebViewBinding
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.WEB_URL
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentWebView : VillageFragment<FragmentWebViewBinding, WebViewViewModel>() {

    var url:String?=null

    override fun showBottomBar(): Boolean? {
        return false
    }

    override fun getCurrentFragment(): Fragment? {
        return this@FragmentWebView
    }

    override fun showToolbar(): Boolean? {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration? {
        return ToolbarConfiguration(
            true, true, requireArguments().getString(VillageConstants.TOOLBAR_TITLE, ""), false,
            -1, false, "", false,-1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.web
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_web_view
    }

    override fun getViewModel(): WebViewViewModel {
        val vm: WebViewViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
        setWebClient()
        setWebView()
    }

    private fun setWebClient() {
        getViewDataBinding()!!.apply {

            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    progressBar.show()
                    super.onPageStarted(view, url, favicon);
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.hide()
                    super.onPageFinished(view, url);
                }
                override fun onReceivedError(view: WebView?, errorCode: Int, description: String, failingUrl: String?) {
                    showToast(description)
                    progressBar.hide()
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    view!!.loadUrl(request?.url.toString());
                    return true;
                }
            }

            webView.setWebChromeClient(object : WebChromeClient() {
                @SuppressLint("NewApi")
                override fun onProgressChanged(view: WebView, progress: Int) {
                    //Make the bar disappear after URL is loaded, and changes string to Loading...
                    progressBar.setProgress(progress , true) //Make the bar disappear after URL is loaded
                }

                override fun onJsAlert(
                    view: WebView,
                    url: String,
                    message: String,
                    result: JsResult
                ): Boolean {
                    //Required functionality here
                    return super.onJsAlert(view, url, message, result)
                }

                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
                    val newWebView = WebView(view!!.context)
                    val transport = resultMsg!!.obj as WebView.WebViewTransport
                    transport.webView = newWebView
                    resultMsg.sendToTarget()
                    return true
                }

            })

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        getViewDataBinding()!!.apply {

            webView.settings.javaScriptEnabled = true
            webView.settings.setSupportZoom(true)
            webView.settings.builtInZoomControls = true
            webView.settings.pluginState = WebSettings.PluginState.ON
            webView.settings.setSupportMultipleWindows(true)
            webView.getSettings().setAllowFileAccess(true)
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.loadUrl(url!!);

        }
    }

    private fun fetchData() {
        url = requireArguments().getString(WEB_URL)
    }

}