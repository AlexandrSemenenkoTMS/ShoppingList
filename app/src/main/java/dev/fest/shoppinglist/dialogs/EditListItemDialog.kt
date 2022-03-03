package dev.fest.shoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.databinding.EditListItemDialogBinding
import dev.fest.shoppinglist.databinding.NewListDialogBinding
import dev.fest.shoppinglist.entities.ShopListItem

object EditListItemDialog {

    fun showDialog(context: Context, item: ShopListItem, listener: Listener) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context))

        builder.setView(binding.root)
        binding.apply {
            editTextName.setText(item.name)
            editTextInfo.setText(item.itemInfo)
            if(item.itemType == 1){
                editTextInfo.visibility = View.GONE
            }
            buttonUpdate.setOnClickListener {
                if(editTextName.text.toString().isNotEmpty()){
                    listener.onClick(item.copy(name = editTextName.text.toString(), itemInfo = editTextInfo.text.toString()))
                }
                dialog?.dismiss()
            }
        }

        dialog = builder.create()
        with(dialog) {
            window?.setBackgroundDrawable(null)
            show()
        }
    }

    interface Listener {
        fun onClick(item: ShopListItem)
    }
}