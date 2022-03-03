package dev.fest.shoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import dev.fest.shoppinglist.databinding.DeleteDialogBinding
import dev.fest.shoppinglist.databinding.NewListDialogBinding

object DeleteDialog {

    fun showDialog(context: Context, listener: Listener) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = DeleteDialogBinding.inflate(LayoutInflater.from(context))

        builder.setView(binding.root)
        binding.apply {
            buttonDelete.setOnClickListener {
                    listener.onClick()
                dialog?.dismiss()
            }
            buttonCancel.setOnClickListener {
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
        fun onClick()
    }
}