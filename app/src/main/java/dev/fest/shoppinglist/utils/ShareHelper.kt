package dev.fest.shoppinglist.utils

import android.content.Intent
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.entities.ShopListItem

object ShareHelper {
    fun shareShopList(shopList: List<ShopListItem>, listName: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, makeShareText(shopList, listName))
        }
        return intent
    }

    private fun makeShareText(shopList: List<ShopListItem>, listName: String): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("${R.string.share_title} $listName")
        stringBuilder.append("\n")
        var counter = 0
        shopList.forEach {
            val itemInfo = if (it.itemInfo.isNotEmpty()) {
                "(${it.itemInfo})"
            } else {
                ""
            }
            stringBuilder.append("${++counter}. ${it.name} $itemInfo")
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }
}