package dev.fest.shoppinglist.utils

import android.content.SharedPreferences
import dev.fest.shoppinglist.R

object ThemeManager {
    const val SHOPPING_LIST_THEME = "shopping_list"
    const val NEW_NOTE_THEME = "new_note"
    fun getSelectedTheme(
        shoppingListOrNewNote: String,
        defaultPreferences: SharedPreferences
    ): Int {
        return when (shoppingListOrNewNote) {
            SHOPPING_LIST_THEME -> {
                if (defaultPreferences.getString("theme_key", "blue") == "blue") {
                    R.style.Theme_ShoppingListBlue
                } else {
                    R.style.Theme_ShoppingListRed
                }
            }
            NEW_NOTE_THEME -> {
                if (defaultPreferences.getString("theme_key", "blue") == "blue") {
                    R.style.Theme_NewNoteBlue
                } else {
                    R.style.Theme_NewNoteRed
                }
            }
            else -> return 0
        }

    }
}