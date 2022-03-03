package dev.fest.shoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.databinding.NewListDialogBinding

object NewListDialog {

    fun showDialog(context: Context, listener: Listener, name: String?) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = NewListDialogBinding.inflate(LayoutInflater.from(context))

        builder.setView(binding.root)
        binding.apply {
            editTextListName.setText(name)
            if (editTextListName.text.isNotEmpty()) {
                buttonCreate.text = context.getString(R.string.shoplist_button_edit_list)
                binding.textViewTitle.text = context.getString(R.string.shoplist_dialog_title_edit_list_name)
            }
            buttonCreate.setOnClickListener {
                val listName = editTextListName.text.toString()
                if (listName.isNotEmpty()) {
                    listener.onClick(listName)
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
        fun onClick(name: String)
    }
}