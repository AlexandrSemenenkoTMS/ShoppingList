package dev.fest.shoppinglist.utils

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import dev.fest.shoppinglist.R

class BillingManager(private val activity: AppCompatActivity) {

    private var billingClient: BillingClient? = null

    init {
        setUpBillingClient()
    }

    private fun setUpBillingClient() {
        billingClient =
            BillingClient.newBuilder(activity).setListener(getPurchaseListener())
                .enablePendingPurchases().build()
    }

    private fun savePref(isPurchase: Boolean) {
        val preference = activity.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean(REMOVE_ADS_KEY, isPurchase)
        editor.apply()
    }

    private fun getPurchaseListener(): PurchasesUpdatedListener {
        return PurchasesUpdatedListener { billingResult, list ->
            run {
                if (billingResult.responseCode > BillingClient.BillingResponseCode.OK) {
                    list?.get(0)?.let { nonConsumableItem(it) }
                }
            }
        }
    }

    fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                getItem()
            }

        })
    }

    fun closeConnection() {
        billingClient?.endConnection()
    }

    private fun getItem() {
        val skuList = ArrayList<String>()
        skuList.add(REMOVE_AD_ITEM)
        val skuDetails = SkuDetailsParams.newBuilder()
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(skuDetails.build()) { billingResult, list ->
            run {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (list != null)
                        if (list.isNotEmpty()) {
                            val billingFlowParams =
                                BillingFlowParams.newBuilder().setSkuDetails(list[0]).build()
                            billingClient?.launchBillingFlow(activity, billingFlowParams)
                        }
                }
            }
        }
    }

    private fun nonConsumableItem(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                        .build()
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) {
                    if (it.responseCode == BillingClient.BillingResponseCode.OK) {
                        savePref(true)
                        Toast.makeText(activity, R.string.toast_ads_deleted, Toast.LENGTH_LONG)
                            .show()
                    } else {
                        savePref(false)
                        Toast.makeText(
                            activity,
                            R.string.toast_ads_deleted_problem,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    companion object {
        const val REMOVE_AD_ITEM = "remove_ad_item_id"
        const val MAIN_PREF = "main_pref"
        const val REMOVE_ADS_KEY = "remove_ads_key"
    }
}

