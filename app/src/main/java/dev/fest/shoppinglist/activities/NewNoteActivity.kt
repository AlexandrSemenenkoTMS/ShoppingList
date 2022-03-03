package dev.fest.shoppinglist.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.activities.MainActivity.Companion.BLUE
import dev.fest.shoppinglist.activities.MainActivity.Companion.THEME_KEY
import dev.fest.shoppinglist.databinding.ActivityNewNoteBinding
import dev.fest.shoppinglist.entities.NoteItem
import dev.fest.shoppinglist.fragments.NoteFragment
import dev.fest.shoppinglist.utils.HtmlManager
import dev.fest.shoppinglist.utils.MyTouchListener
import dev.fest.shoppinglist.utils.ThemeManager
import dev.fest.shoppinglist.utils.TimeManager
import java.text.SimpleDateFormat
import java.util.*

class NewNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewNoteBinding

    private var note: NoteItem? = null

    private var prefSharedPreferences: SharedPreferences? = null

    private lateinit var defaultPreferences: SharedPreferences

    private var currentTheme = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(ThemeManager.getSelectedTheme(ThemeManager.NEW_NOTE_THEME, defaultPreferences))
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getNote()
        actionBarSetting()
        init()
        setTextSize()
        onClickColorPicker()
        actionMenuCallback()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        currentTheme = defaultPreferences.getString(THEME_KEY, BLUE).toString()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_save) {
            setMainResult()
        } else if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.id_bold_style) {
            setBoldForSelectedText()
        } else if (item.itemId == R.id.id_color) {
            if (binding.colorPicker.isShown) {
                closeColorPicker()
            } else {
                openColorPicker()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onClickColorPicker() = with(binding) {
        imageButtonRed.setOnClickListener { setColorForSelectedText(R.color.picker_red) }
        imageButtonYellow.setOnClickListener { setColorForSelectedText(R.color.picker_yellow) }
        imageButtonBlack.setOnClickListener { setColorForSelectedText(R.color.picker_black) }
        imageButtonBlue.setOnClickListener { setColorForSelectedText(R.color.picker_blue) }
        imageButtonGreen.setOnClickListener { setColorForSelectedText(R.color.picker_green) }
        imageButtonOrange.setOnClickListener { setColorForSelectedText(R.color.picker_orange) }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.colorPicker.setOnTouchListener(MyTouchListener())
        prefSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun getNote() {
        val serializableNoteItem = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if (serializableNoteItem != null) {
            note = serializableNoteItem as NoteItem
            fillNote()
        }
    }

    private fun fillNote() = with(binding) {
        if (note != null) {
            editTextTextTitle.setText(note?.title)
            editTextTextDescription.setText(HtmlManager.getFromHtml(note?.description!!))
        }
    }

    private fun setBoldForSelectedText() = with(binding) {
        val startPosition = editTextTextDescription.selectionStart
        val endPosition = editTextTextDescription.selectionEnd
        val styles =
            editTextTextDescription.text.getSpans(startPosition, endPosition, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if (styles.isNotEmpty()) {
            editTextTextDescription.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }
        editTextTextDescription.text.setSpan(
            boldStyle,
            startPosition,
            endPosition,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        editTextTextDescription.text.trim()
        editTextTextDescription.setSelection(startPosition)
    }

    private fun setColorForSelectedText(colorId: Int) = with(binding) {
        val startPosition = editTextTextDescription.selectionStart
        val endPosition = editTextTextDescription.selectionEnd
        val styles =
            editTextTextDescription.text.getSpans(
                startPosition,
                endPosition,
                ForegroundColorSpan::class.java
            )
        if (styles.isNotEmpty()) editTextTextDescription.text.removeSpan(styles[0])
        editTextTextDescription.text.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this@NewNoteActivity, colorId)),
            startPosition,
            endPosition,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        editTextTextDescription.text.trim()
        editTextTextDescription.setSelection(startPosition)
    }

    private fun setMainResult() {
        var editState = getString(R.string.create)
        val tempNote: NoteItem? = if (note == null) {
            createNewNote()
        } else {
            editState = getString(R.string.update)
            updateNote()
        }
        val intent = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun updateNote(): NoteItem? = with(binding) {
        return note?.copy(
            title = editTextTextTitle.text.toString(),
            description = HtmlManager.stringToHtml(editTextTextDescription.text),
            time = TimeManager.getCurrentTime()
        )
    }

    private fun createNewNote(): NoteItem = with(binding) {
        return NoteItem(
            null,
            editTextTextTitle.text.toString(),
            HtmlManager.stringToHtml(editTextTextDescription.text),
            TimeManager.getCurrentTime(),
            ""
        )
    }

    private fun actionBarSetting() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        if (note?.id == null) {
            actionBar?.title = getString(R.string.notes_action_bar_create)
        } else {
            actionBar?.title = getString(R.string.notes_action_bar_edit)
        }
    }

    private fun openColorPicker() {
        val openAnimation = AnimationUtils.loadAnimation(this, R.anim.open_color_picker)
        binding.apply {
            colorPicker.visibility = View.VISIBLE
            colorPicker.startAnimation(openAnimation)
        }
    }

    private fun closeColorPicker() {
        val openAnimation = AnimationUtils.loadAnimation(this, R.anim.close_color_picker)
        openAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                binding.colorPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(p0: Animation?) {}
        })
        binding.colorPicker.startAnimation(openAnimation)
    }

    private fun actionMenuCallback() {
        val actionCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onActionItemClicked(mode: ActionMode?, menu: MenuItem?): Boolean {
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
            }

        }
        binding.editTextTextDescription.customSelectionActionModeCallback = actionCallback
    }

    private fun EditText.setTextSize(size: String?) {
        if (size != null) {
            this.textSize = size.toFloat()
        }
    }

    private fun setTextSize() = with(binding) {
        editTextTextTitle.setTextSize(
            prefSharedPreferences?.getString(
                TITLE_TEXT_SIZE_KEY,
                TITLE_TEXT_SIZE
            )
        )
        editTextTextDescription.setTextSize(
            prefSharedPreferences?.getString(
                CONTENT_TEXT_SIZE_KEY,
                CONTENT_TEXT_SIZE
            )
        )
    }

    companion object {
        const val TITLE_TEXT_SIZE_KEY = "title_text_size_key"
        const val TITLE_TEXT_SIZE = "16"
        const val CONTENT_TEXT_SIZE_KEY = "content_text_size_key"
        const val CONTENT_TEXT_SIZE = "14"
    }
}