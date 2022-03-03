package dev.fest.shoppinglist.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.ListPreference
import androidx.preference.PreferenceManager
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.fragments.SettingsFragment
import dev.fest.shoppinglist.utils.ThemeManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var defaultPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(ThemeManager.getSelectedTheme(ThemeManager.SHOPPING_LIST_THEME, defaultPreferences))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.place_holder, SettingsFragment())
                .commit()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}