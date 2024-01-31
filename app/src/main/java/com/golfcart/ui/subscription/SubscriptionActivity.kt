package com.golfcart.ui.subscription

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryPurchasesAsync
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.ActivitySubsBinding
import com.golfcart.model.subscription_param.SubscriptionParam
import com.golfcart.model.promo_param.PromoParam
import com.golfcart.model.remote.ApiResponse
import com.golfcart.ui.base.BaseActivity
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.Logs.VillageLogs
import com.golfcart.utils.app_configuration.AppConfiguration.DEVICE_TYPE
import com.golfcart.utils.village_constant.VillageConstants.PRODUCT_ID
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscriptionActivity : BaseActivity<ActivitySubsBinding, SubscriptionViewModel>(),
    PurchasesUpdatedListener {

    var binding: ActivitySubsBinding? = null
    private lateinit var billingClient: BillingClient
    private var productDetail: ProductDetails? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_subs
    }

    override fun getCurrentRunningActivity(): AppCompatActivity {
        return this@SubscriptionActivity
    }

    override fun getViewModel(): SubscriptionViewModel {
        val vm: SubscriptionViewModel by viewModel()
        return vm
    }

    override fun getBindingVariable(): Int {
        return BR.subscription
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performBinding()
        setUpBillingClient() // initialise billing client
        setListiner()
    }

    private fun setListiner() {
        binding!!.apply {
            sharebtn.setOnClickListener {
                buySubscription()
            }

            clearall.setOnClickListener {
                setUpRestoreClient()
            }

            btnApply.setOnClickListener {
                if (etCoupoun.text!!.trim().isEmpty()){
                    showToast("Enter your coupoun code")
                }else {
                    checkCoupounCode()
                }
            }
            backRL.setOnClickListener {
                finish()
            }

        }
    }

    private fun performBinding() {
        binding = DataBindingUtil.setContentView(this@SubscriptionActivity, R.layout.activity_subs)
        binding?.executePendingBindings()
    }

    private fun checkCoupounCode() {
        getViewModel().checkCoupoun(this@SubscriptionActivity, PromoParam(getDeviceUniqueId()!!, DEVICE_TYPE, binding!!.etCoupoun.text.toString()))
        getViewModel().checkCoupounliveData.observe(this) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data!!.status == 200){
                        finish()
                        startActivity(Intent(this@SubscriptionActivity, VillageActivity::class.java))
                    }else{
                        showToast("Invalid Coupoun")
                    }
                    removeObserver(getViewModel().checkCoupounMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    showToast(getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun setUpBillingClient() {
        billingClient = BillingClient.newBuilder(this@SubscriptionActivity)
            .setListener(this@SubscriptionActivity)
            .enablePendingPurchases()
            .build()

        // start connection setup
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(p0: BillingResult) {
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                    getSubscriptionList()
                    checkCurrentSubscription()
                } else {
                    hideLoader()
                }
            }

            override fun onBillingServiceDisconnected() {
                hideLoader()
                //  hide progress here
            }

        })
    }

    private fun checkCurrentSubscription() {
        lifecycleScope.launch {
            val purchasesResult: PurchasesResult = billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS)
            val purchases: List<Purchase> = purchasesResult.purchasesList
            VillageLogs.printLog(">>>>purchase_list", Gson().toJson(purchases))

            for (purchase in purchases) {
                val purchaseToken = purchase.purchaseToken
                if (purchaseToken.isNotEmpty()) {
                    showLoader()
                    val obj = SubscriptionParam(
                        purchase.orderId,
                        purchase.packageName,
                        purchase.products.toString(),
                        purchase.purchaseTime.toString(),
                        purchase.purchaseState.toString(),
                        purchase.purchaseToken,
                        purchase.quantity.toString(),
                        purchase.isAutoRenewing.toString(),
                        purchase.isAcknowledged.toString(),
                        Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
                            .toString(),
                        "1",
                        DEVICE_TYPE
                    )

                    verifyReceipt(obj)
                }
            }
        }
    }

    private fun verifyReceipt(obj: SubscriptionParam) {
        getViewModel().verifyReceipt(this@SubscriptionActivity, obj)
        getViewModel().verifyliveData.observe(this) {
            when (it.status) {
                ApiResponse.Status.LOADING -> {
                    showLoader()
                }

                ApiResponse.Status.SUCCESS -> {
                    hideLoader()
                    finish()
                    startActivity(Intent(this@SubscriptionActivity, VillageActivity::class.java))
                    removeObserver(getViewModel().verifyReciptMutableLiveData)
                }

                ApiResponse.Status.ERROR -> {
                    hideLoader()
                    showToast(getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun setUpRestoreClient() {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        // start connection setup
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(p0: BillingResult) {
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS).build(),
                        PurchasesResponseListener { _, list ->
                            if (list == null) {
                                return@PurchasesResponseListener
                            }

                            if (list.isNotEmpty()) {
                                val purchaseToken = list[0]
                                val purchaseTime = list[0].purchaseTime
                                val productId = list[0].products
                                //api hit to send purchases to server


                            }

                        })
                } else {
                    showToast(getString(R.string.something_went_wrong))

                }
            }

            override fun onBillingServiceDisconnected() {
                showToast(getString(R.string.something_went_wrong))
            }
        })
    }

    private fun buySubscription() { // buy subscription passing the product details
        val offerToken = productDetail?.subscriptionOfferDetails?.get(0)?.offerToken ?: return

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetail!!)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList).build()

        billingClient.launchBillingFlow(this@SubscriptionActivity, billingFlowParams)
    }

    private fun getSubscriptionList() {
        val productList =
            listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID)  // replace this product id with your id
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList)

        billingClient.queryProductDetailsAsync(params.build()) { billingResult, productDetailsList ->
            // Process the result
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (productDetailsList.size > 0) {
                    for (skuDetails in productDetailsList) {
                        hideLoader()
                        val sku = skuDetails.productId
                        if (sku == "com.villagegps.12month") { // replace with your id
                            productDetail = skuDetails
                        }
                    }
                }
            } else {
                VillageLogs.printLog(
                    "TAG",
                    "Connected.responseCode: Fail ${billingResult.responseCode}"
                )
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when {
            billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null -> {
//                showToast(billingResult.debugMessage)

                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            }

            billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED -> {
//                showToast(billingResult.debugMessage)
            }

            else -> {
//                showToast("Handle any other error codes.")
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        showLoader()
        val obj = SubscriptionParam(
            purchase.orderId,
            purchase.packageName,
            purchase.products.toString(),
            purchase.purchaseTime.toString(),
            purchase.purchaseState.toString(),
            purchase.purchaseToken,
            purchase.quantity.toString(),
            purchase.isAutoRenewing.toString(),
            purchase.isAcknowledged.toString(),
            Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toString(),
            "1",
            DEVICE_TYPE
        )

        verifyReceipt(obj)
    }

}