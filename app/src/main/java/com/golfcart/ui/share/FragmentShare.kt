package com.golfcart.ui.share

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.FragmentShareBinding
import com.golfcart.model.toolbar_configuration.ToolbarConfiguration
import com.golfcart.ui.village.VillageFragment
import com.golfcart.utils.village_constant.VillageConstants
import com.golfcart.utils.village_constant.VillageConstants.APP_URL
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.Locale

class FragmentShare : VillageFragment<FragmentShareBinding, ShareViewModel>() {

    data class MoreData(val name:String, val image:Int)

    override fun showBottomBar(): Boolean {
        return false
    }

    override fun getCurrentFragment(): Fragment {
        return this@FragmentShare
    }

    override fun showToolbar(): Boolean {
        return true
    }

    override fun configureToolbar(): ToolbarConfiguration {
        return ToolbarConfiguration(
            true, true, requireArguments().getString(VillageConstants.TOOLBAR_TITLE, "") ,
            false, -1, false, "", false, -1
        )
    }

    override fun getBindingVariable(): Int {
        return BR.share
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_share
    }

    override fun getViewModel(): ShareViewModel {
        val vm: ShareViewModel by viewModel()
        return vm
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding()!!.apply {
            setListiner()
        }
    }

    private fun setListiner() {
        getViewDataBinding()!!.apply {
            ivInsta.setOnClickListener {
                shareInsta()
            }
            ivFacebook.setOnClickListener {
                shareFacebook(APP_URL)
            }
            ivEmail.setOnClickListener {
                shareEmail(APP_URL)
            }
            ivTwitter.setOnClickListener {
                shareTwitter(APP_URL)
            }
            ivMsg.setOnClickListener {
                shareMsg(APP_URL)
            }
        }
    }

    private fun shareInsta() {
        val uri = Uri.parse("android.resource://com.golfcart/" + R.drawable.splash_logo)
        val instagramIntent = Intent(Intent.ACTION_SEND)
        instagramIntent.type = "image/*"
        instagramIntent.putExtra(Intent.EXTRA_STREAM, uri)
        instagramIntent.setPackage("com.instagram.android")
        val packManager = getContainerActivity().packageManager
        val resolvedInfoList =
            packManager.queryIntentActivities(instagramIntent, PackageManager.MATCH_DEFAULT_ONLY)
        var resolved = false
        for (resolveInfo in resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.instagram.android")) {
                instagramIntent.setClassName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name
                )
                resolved = true
                break
            }
        }
        if (resolved) {
            startActivity(instagramIntent)
        } else {
            Toast.makeText(activity, "Application is not installed", Toast.LENGTH_LONG).show()
            val appPackageName =
                "com.instagram.android" // getPackageName() from Context or Activity object
            try {
                getContainerActivity().startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                getContainerActivity().startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }
    }

    private fun shareFacebook(appurl: String) {
        var intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "VillagesGPS") // NB: has no effect!
        intent.putExtra(Intent.EXTRA_TEXT, appurl)

// See if official Facebook app is found
        var facebookAppFound = false
        val matches = getContainerActivity().packageManager.queryIntentActivities(intent, 0)
        for (info in matches) {
            if (info.activityInfo.packageName.lowercase(Locale.getDefault())
                    .startsWith("com.facebook.katana")
            ) {
                intent.setPackage(info.activityInfo.packageName)
                facebookAppFound = true
                break
            }
        }
        if (!facebookAppFound) {
            val sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=$appurl"
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl))
        }
        startActivity(intent)
    }

    private fun shareEmail(appurl: String) {
        val gmail = Intent(Intent.ACTION_VIEW)
        gmail.setClassName(
            "com.google.android.gm",
            "com.google.android.gm.ComposeActivityGmail"
        )
        try {
            gmail.putExtra(
                Intent.EXTRA_TEXT, java.lang.String.format(
                    "please download this app $appurl",
                    Build.VERSION.RELEASE,
                    getContainerActivity().packageManager.getPackageInfo(
                        getContainerActivity().packageName, 0
                    ).versionCode
                )
            )
            startActivity(gmail)
        } catch (e: Exception) {
            e.printStackTrace()
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "message/rfc822"
            try {
                emailIntent.putExtra(
                    Intent.EXTRA_TEXT, java.lang.String.format(
                        "please download this app $appurl",
                        Build.VERSION.RELEASE,
                        getContainerActivity().packageManager.getPackageInfo(
                            getContainerActivity().packageName, 0
                        ).versionCode
                    )
                )
            } catch (e1: PackageManager.NameNotFoundException) {

                // TODO Auto-generated catch block
                e1.printStackTrace()
            }
            startActivity(
                Intent.createChooser(
                    emailIntent, "Email Chooser"
                )
            )
            // String recepientEmail = ""; // either set to destination email or
        }
    }

    private fun shareTwitter(link: String) {
        val tweetIntent = Intent(Intent.ACTION_SEND)
        tweetIntent.putExtra(Intent.EXTRA_TEXT, "https://www.google.com")
        tweetIntent.type = "text/plain"
        // tweetIntent.setData(Uri.parse("https://www.google.com"));
        val packManager = getContainerActivity().packageManager
        val resolvedInfoList =
            packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY)
        var resolved = false
        for (resolveInfo in resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name
                )
                resolved = true
                break
            }
        }
        if (resolved) {
            startActivity(tweetIntent)
        } else {
            val i = Intent()
            i.putExtra(Intent.EXTRA_TEXT, link)
            i.action = Intent.ACTION_VIEW
            i.data = Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(link))
            startActivity(i)
            Toast.makeText(activity, "Twitter app isn't found", Toast.LENGTH_LONG).show()
        }
    }

    private fun urlEncode(s: String): String? {
        return try {
            URLEncoder.encode(s, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            ""
        }
    }

    private fun shareMsg(appurl: String) {
        val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse("sms:"))
        intent1.putExtra("sms_body", "please download this app $appurl")
        startActivity(intent1)
    }


}
