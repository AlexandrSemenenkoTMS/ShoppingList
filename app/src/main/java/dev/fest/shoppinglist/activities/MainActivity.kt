package dev.fest.shoppinglist.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.databinding.ActivityMainBinding
import dev.fest.shoppinglist.dialogs.NewListDialog
import dev.fest.shoppinglist.fragments.FragmentManager
import dev.fest.shoppinglist.fragments.NoteFragment
import dev.fest.shoppinglist.fragments.ShopListNameItemFragment
import dev.fest.shoppinglist.utils.BillingManager
import dev.fest.shoppinglist.utils.ThemeManager
import io.ak1.OnBubbleClickListener

class MainActivity : AppCompatActivity(), NewListDialog.Listener {

    private lateinit var binding: ActivityMainBinding

    private var currentMenuItemId = R.id.notes

    private var currentTheme = ""

    private var interstitialAd: InterstitialAd? = null

    private var adShowCounter = 0

    private var adShowCounterMax = 3

    private lateinit var preferences: SharedPreferences

    private lateinit var defaultPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(
            ThemeManager.getSelectedTheme(
                ThemeManager.SHOPPING_LIST_THEME,
                defaultPreferences
            )
        )
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        currentTheme = defaultPreferences.getString(THEME_KEY, BLUE).toString()
        setContentView(binding.root)
        FragmentManager.setFragment(NoteFragment.newInstance(), this)
        setBottomNavListener()
        onClickNew()
        if (!preferences.getBoolean(BillingManager.REMOVE_ADS_KEY, false)) loadInterstitialAd()

    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationMenu.selectedItemId = currentMenuItemId
        if (defaultPreferences.getString(THEME_KEY, BLUE) != currentTheme) recreate()
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            getString(R.string.inter_ad_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    super.onAdFailedToLoad(adError)
                    interstitialAd = null
                }
            })
    }


    private fun showInterstitialAd(adListener: AdListener) {
        if (interstitialAd != null && adShowCounter > adShowCounterMax && !preferences.getBoolean(
                BillingManager.REMOVE_ADS_KEY,
                false
            )
        ) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd()
                    adListener.onFinish()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    interstitialAd = null
                    loadInterstitialAd()
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd()
                }
            }
            adShowCounter = 0
            interstitialAd?.show(this)
        } else {
            adShowCounter++
            adListener.onFinish()
        }
    }


    private fun setBottomNavListener() {
        binding.bottomNavigationMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.notes -> {
                    showInterstitialAd(object : AdListener {
                        override fun onFinish() {
                            currentMenuItemId = R.id.notes
                            FragmentManager.setFragment(
                                NoteFragment.newInstance(),
                                this@MainActivity
                            )
                        }
                    })
                }
                R.id.shopList -> {
                    currentMenuItemId = R.id.shopList
                    FragmentManager.setFragment(ShopListNameItemFragment.newInstance(), this)
                }
                R.id.settings -> {
                    showInterstitialAd(object : AdListener {
                        override fun onFinish() {
                            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        }
                    })
                }
            }
            true
        }
    }

    private fun onClickNew() {
        binding.buttonAddNew.setOnClickListener {
            FragmentManager.currentFragment?.onClickNew()
        }
    }

    override fun onClick(name: String) {
        Log.d("MyLog", "name: $name")
    }

    interface AdListener {
        fun onFinish()
    }

    companion object {
        const val THEME_KEY = "theme_key"
        const val BLUE = "blue"
    }
}