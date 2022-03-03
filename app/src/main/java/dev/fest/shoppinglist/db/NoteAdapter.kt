package dev.fest.shoppinglist.db

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.databinding.NoteListItemBinding
import dev.fest.shoppinglist.entities.NoteItem
import dev.fest.shoppinglist.utils.HtmlManager
import dev.fest.shoppinglist.utils.TimeManager

class NoteAdapter(private val listener: Listener, private val defaultPreferences: SharedPreferences) :
    ListAdapter<NoteItem, NoteAdapter.ItemHolder>(ItemHolder.ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position), listener, defaultPreferences)
    }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = NoteListItemBinding.bind(view)

        fun setData(note: NoteItem, listener: Listener, defaultPreferences: SharedPreferences) = with(binding) {
            textViewTitle.text = note.title
            textViewDescription.text = HtmlManager.getFromHtml(note.description).trim()
            textViewTime.text = TimeManager.getTimeFormat(note.time, defaultPreferences)
            itemView.setOnClickListener { listener.onClickItem(note) }
            imageButtonDelete.setOnClickListener {
                listener.deleteItem(note.id!!)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.note_list_item, parent, false)
                )
            }
        }

        class ItemComparator : DiffUtil.ItemCallback<NoteItem>() {
            override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem) =
                oldItem == newItem
        }
    }

    interface Listener {
        fun deleteItem(id: Int)
        fun onClickItem(note: NoteItem)
    }
}
