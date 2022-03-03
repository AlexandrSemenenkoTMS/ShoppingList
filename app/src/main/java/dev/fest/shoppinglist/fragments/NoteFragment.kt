package dev.fest.shoppinglist.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.activities.MainApp
import dev.fest.shoppinglist.activities.NewNoteActivity
import dev.fest.shoppinglist.databinding.FragmentNoteBinding
import dev.fest.shoppinglist.db.MainViewModel
import dev.fest.shoppinglist.db.NoteAdapter
import dev.fest.shoppinglist.entities.NoteItem

class NoteFragment : BaseFragment(), NoteAdapter.Listener {

    private lateinit var binding: FragmentNoteBinding

    private lateinit var editLauncher: ActivityResultLauncher<Intent>

    private lateinit var adapter: NoteAdapter

    private lateinit var defaultPreferences: SharedPreferences

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() {
        editLauncher.launch(Intent(activity, NewNoteActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observer()
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.notes_action_bar_notes)
    }

    override fun deleteItem(id: Int) {
        mainViewModel.deleteNote(id)
    }

    override fun onClickItem(note: NoteItem) {
        val intent = Intent(activity, NewNoteActivity::class.java).apply {
            putExtra(NEW_NOTE_KEY, note)
        }
        editLauncher.launch(intent)
    }

    private fun onEditResult() {
        editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val editState = it.data?.getStringExtra(EDIT_STATE_KEY)
                if (editState == getString(R.string.update)) {
                    mainViewModel.editNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)
                } else {
                    mainViewModel.insertNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)
                }
            }
        }
    }

    private fun initRecyclerView() = with(binding) {
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        recyclerViewNote.layoutManager = getLayoutManager()
        adapter = NoteAdapter(this@NoteFragment, defaultPreferences)
        recyclerViewNote.adapter = adapter
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager {
        return if (defaultPreferences.getString(
                NOTE_STYLE_KEY,
                LINEAR
            ) == LINEAR
        ) {
            LinearLayoutManager(activity)
        } else {
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun observer() {
        mainViewModel.allNotes.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            binding.textViewEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

        })
    }

    companion object {
        const val NEW_NOTE_KEY = "new_note_key"
        const val NOTE_STYLE_KEY = "note_style_key"
        const val EDIT_STATE_KEY = "edit_state_key"
        const val LINEAR = "Linear"

        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}