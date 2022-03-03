package dev.fest.shoppinglist.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.utils.BillingManager

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var removeAdsPreference: Preference
    private lateinit var billingManager: BillingManager
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
        init()
    }


    private fun init() {
        billingManager = BillingManager(activity as AppCompatActivity)
        removeAdsPreference = findPreference(REMOVE_ADS_KEY)!!
        removeAdsPreference.setOnPreferenceClickListener {
            billingManager.startConnection()
            true
        }
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.settings_title)
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.closeConnection()
    }

    companion object {
        const val REMOVE_ADS_KEY = "remove_ads_key"
    }
}