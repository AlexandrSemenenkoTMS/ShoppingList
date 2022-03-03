package dev.fest.shoppinglist.activities

import android.app.Application
import dev.fest.shoppinglist.db.MainDataBase

class MainApp : Application() {
    val database by lazy { MainDataBase.getDataBase(this) }
}