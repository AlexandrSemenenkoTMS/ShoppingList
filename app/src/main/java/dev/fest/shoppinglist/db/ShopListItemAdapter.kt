package dev.fest.shoppinglist.db

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.databinding.ShopLibraryListItemBinding
import dev.fest.shoppinglist.databinding.ShopListItemBinding
import dev.fest.shoppinglist.entities.ShopListItem
import kotlin.math.E

class ShopListItemAdapter(private val listener: Listener) :
    ListAdapter<ShopListItem, ShopListItemAdapter.ItemHolder>(ItemHolder.ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if (viewType == 0) {
            ItemHolder.createShopItem(parent)
        } else {
            ItemHolder.createLibraryItem(parent)
        }
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (getItem(position).itemType == 0) {
            holder.setItemData(getItem(position), listener)
        } else {
            holder.setLibraryData(getItem(position), listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }

    class ItemHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun setItemData(shopListItem: ShopListItem, listener: Listener) {
            val binding = ShopListItemBinding.bind(view)
            binding.apply {
                textViewName.text = shopListItem.name
                textViewInfo.apply {
                    text = shopListItem.itemInfo
                    visibility = textViewInfoVisibility(shopListItem)
                }
                checkBox.isChecked = shopListItem.itemChecked
                setPaintFlagAndColor(binding)
                checkBox.setOnClickListener {
                    listener.onClickItem(
                        shopListItem.copy(itemChecked = checkBox.isChecked),
                        CHECK_BOX
                    )
                }
                imageButtonEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT)
                }
            }
        }

        fun setLibraryData(shopListItem: ShopListItem, listener: Listener) {
            val binding = ShopLibraryListItemBinding.bind(view)
            binding.apply {
                textViewName.text = shopListItem.name
                imageButtonEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT_LIBRARY_ITEM)
                }
                imageButtonDelete.setOnClickListener {
                    listener.onClickItem(shopListItem, DELETE_LIBRARY_ITEM)
                }
                itemView.setOnClickListener {
                    listener.onClickItem(shopListItem, ADD)

                }
            }
        }

        private fun textViewInfoVisibility(shopListItem: ShopListItem): Int {
            return if (shopListItem.itemInfo.isEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        private fun setPaintFlagAndColor(binding: ShopListItemBinding) {
            binding.apply {
                if (checkBox.isChecked) {
                    textViewName.apply {
                        paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                        setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))
                    }
                    textViewInfo.apply {
                        paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                        setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))
                    }
                } else {
                    textViewName.apply {
                        paintFlags = Paint.ANTI_ALIAS_FLAG
                        setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    }
                    textViewInfo.apply {
                        paintFlags = Paint.ANTI_ALIAS_FLAG
                        setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    }
                }
            }
        }

        companion object {
            fun createShopItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shop_list_item, parent, false)
                )
            }

            fun createLibraryItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shop_library_list_item, parent, false)
                )
            }

        }

        class ItemComparator : DiffUtil.ItemCallback<ShopListItem>() {
            override fun areItemsTheSame(oldItem: ShopListItem, newItem: ShopListItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ShopListItem, newItem: ShopListItem) =
                oldItem == newItem
        }
    }

    interface Listener {
        fun onClickItem(shopListItem: ShopListItem, state: Int)
    }

    companion object {
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY_ITEM = 2
        const val DELETE_LIBRARY_ITEM = 3
        const val ADD = 4
    }
}