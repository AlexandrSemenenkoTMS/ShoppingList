package dev.fest.shoppinglist.db

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.databinding.ListNameItemBinding
import dev.fest.shoppinglist.entities.ShopListNameItem
import dev.fest.shoppinglist.utils.TimeManager

class ShopListNameAdapter(private val listener: Listener, private val defaultSharedPreferences: SharedPreferences) :
    ListAdapter<ShopListNameItem, ShopListNameAdapter.ItemHolder>(ItemHolder.ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position), listener, defaultSharedPreferences)
    }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListNameItemBinding.bind(view)

        fun setData(shopListNameItem: ShopListNameItem, listener: Listener, defaultSharedPreferences: SharedPreferences) = with(binding) {
            val colorState = ColorStateList.valueOf(
                getProgressColorState(
                    shopListNameItem,
                    binding.root.context
                )
            )
            textViewListName.text = shopListNameItem.name
            textViewTime.text = TimeManager.getTimeFormat(shopListNameItem.time, defaultSharedPreferences)
            progressBar.apply {
                max = shopListNameItem.allItemCounter
                progress = shopListNameItem.checkedItemsCounter
                progressTintList = colorState
            }

            counterCard.backgroundTintList = colorState

             val counterText =
                "${shopListNameItem.checkedItemsCounter}/${shopListNameItem.allItemCounter}"
            Log.d("ShopListNameAdapter", "$counterText")
            textViewCounter.text = counterText
            itemView.setOnClickListener {
                listener.onClickItem(shopListNameItem)
            }
            imageButtonDeleteList.setOnClickListener {
                listener.deleteItem(shopListNameItem.id!!)
            }
            imageButtonEditList.setOnClickListener {
                listener.editItem(shopListNameItem)
            }
        }

        private fun getProgressColorState(item: ShopListNameItem, context: Context): Int {
            return if (item.checkedItemsCounter == item.allItemCounter) {
                ContextCompat.getColor(context, R.color.green_main)
            } else {
                ContextCompat.getColor(context, R.color.red)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_name_item, parent, false)
                )
            }
        }

        class ItemComparator : DiffUtil.ItemCallback<ShopListNameItem>() {
            override fun areItemsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem) =
                oldItem == newItem
        }
    }

    interface Listener {
        fun deleteItem(id: Int)
        fun editItem(shopListNameItemItem: ShopListNameItem)
        fun onClickItem(shopListNameItemItem: ShopListNameItem)
    }
}
