package dev.fest.shoppinglist.fragments

import androidx.appcompat.app.AppCompatActivity
import dev.fest.shoppinglist.R

object FragmentManager {
    var currentFragment: BaseFragment? = null
    fun setFragment(newFragment: BaseFragment, activity: AppCompatActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, newFragment)
        transaction.commit()
        currentFragment = newFragment
    }
}