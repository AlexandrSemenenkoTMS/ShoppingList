package dev.fest.shoppinglist.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.activities.MainActivity.Companion.BLUE
import dev.fest.shoppinglist.activities.MainActivity.Companion.THEME_KEY
import dev.fest.shoppinglist.databinding.ActivityShopListBinding
import dev.fest.shoppinglist.db.MainViewModel
import dev.fest.shoppinglist.db.ShopListItemAdapter
import dev.fest.shoppinglist.dialogs.EditListItemDialog
import dev.fest.shoppinglist.entities.LibraryItem
import dev.fest.shoppinglist.entities.ShopListItem
import dev.fest.shoppinglist.entities.ShopListNameItem
import dev.fest.shoppinglist.utils.ShareHelper
import dev.fest.shoppinglist.utils.ThemeManager
import dev.fest.shoppinglist.utils.TimeManager

class ShopListActivity : AppCompatActivity(), ShopListItemAdapter.Listener {

    private lateinit var binding: ActivityShopListBinding

    private var shopListNameItem: ShopListNameItem? = null

    private lateinit var saveItem: MenuItem

    private var editTextItem: EditText? = null

    private var adapter: ShopListItemAdapter? = null

    private lateinit var defaultPreferences: SharedPreferences

    private var currentTheme = ""

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).database)
    }

    private lateinit var textWatcher: TextWatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(
            ThemeManager.getSelectedTheme(
                ThemeManager.SHOPPING_LIST_THEME,
                defaultPreferences
            )
        )
        super.onCreate(savedInstanceState)
        currentTheme = defaultPreferences.getString(THEME_KEY, BLUE).toString()
        binding = ActivityShopListBinding.inflate(layoutInflater)
        adapter = ShopListItemAdapter(this)
        setContentView(binding.root)
        init()
        initRecyclerView()
        listItemObserver()
        actionBarSetting()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu, menu)
        saveItem = menu?.findItem(R.id.save_item)!!
        val newItem = menu.findItem(R.id.new_item)
        editTextItem = newItem.actionView.findViewById(R.id.editTextNewShopItem) as EditText
        newItem.setOnActionExpandListener(expandActionView())
        saveItem.isVisible = false
        textWatcher = textWatcher()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_item -> {
                addNewShopItem(editTextItem?.text.toString())
            }
            R.id.delete_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, true)
                finish()
            }
            R.id.clear_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, false)
            }
            R.id.share_list -> {
                startActivity(
                    Intent.createChooser(
                        ShareHelper.shareShopList(
                            adapter?.currentList!!,
                            shopListNameItem?.name!!
                        ), SHARE_BY
                    )
                )
            }
            android.R.id.home -> {
                saveItemCount()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClickItem(shopListItem: ShopListItem, state: Int) {
        when (state) {
            ShopListItemAdapter.CHECK_BOX -> mainViewModel.editShopListItem(shopListItem)
            ShopListItemAdapter.EDIT -> editListItem(shopListItem)
            ShopListItemAdapter.EDIT_LIBRARY_ITEM -> editLibraryItem(shopListItem)
            ShopListItemAdapter.DELETE_LIBRARY_ITEM -> {
                mainViewModel.deleteLibraryItem(shopListItem.id!!)
                mainViewModel.getAllLibraryItems("%${editTextItem?.text.toString()}%")
            }
            ShopListItemAdapter.ADD -> addNewShopItem(shopListItem.name)

        }
    }

    override fun onBackPressed() {
        saveItemCount()
        super.onBackPressed()
    }

    private fun actionBarSetting() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = shopListNameItem?.name
    }

    private fun textWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("ShopListActivity", "on text changed: $s")
                mainViewModel.getAllLibraryItems("%$s%")
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }


    private fun addNewShopItem(name: String) {
        if (name.isEmpty()) return
        val item = ShopListItem(
            null,
            name,
            "",
            false,
            shopListNameItem?.id!!,
            0
        )
        editTextItem?.setText("")
        mainViewModel.insertShopItem(item)
    }

    private fun expandActionView(): MenuItem.OnActionExpandListener {
        return object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                saveItem.isVisible = true

                editTextItem?.addTextChangedListener(textWatcher)
                libraryItemObserver()
                mainViewModel.getAllItemsFromList(shopListNameItem?.id!!)
                    .removeObservers(this@ShopListActivity)
                mainViewModel.getAllLibraryItems("%%")
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                saveItem.isVisible = false
                editTextItem?.removeTextChangedListener(textWatcher)
                invalidateOptionsMenu()
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity)
                editTextItem?.setText("")
                listItemObserver()
                return true
            }
        }
    }

    private fun listItemObserver() {
        mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).observe(this, {
            adapter?.submitList(it)
            binding.textViewEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
    }

    private fun libraryItemObserver() {
        mainViewModel.libraryItems.observe(this, {
            val tempShopList = ArrayList<ShopListItem>()
            it.forEach { item ->
                val shopListItem = ShopListItem(
                    item.id,
                    item.name,
                    "",
                    false,
                    0,
                    1
                )
                tempShopList.add(shopListItem)
            }
            adapter?.submitList(tempShopList)
        }
        )
    }

    private fun initRecyclerView() = with(binding) {
        recyclerView.layoutManager = LinearLayoutManager(this@ShopListActivity)
        recyclerView.adapter = adapter

    }

    private fun init() {
        shopListNameItem = intent.getSerializableExtra(SHOP_LIST_NAME) as ShopListNameItem
    }

    private fun editListItem(shopListItem: ShopListItem) {
        EditListItemDialog.showDialog(this, shopListItem, object : EditListItemDialog.Listener {
            override fun onClick(item: ShopListItem) {
                mainViewModel.editShopListItem(item)
            }
        })
    }

    private fun editLibraryItem(shopListItem: ShopListItem) {
        EditListItemDialog.showDialog(this, shopListItem, object : EditListItemDialog.Listener {
            override fun onClick(item: ShopListItem) {
                mainViewModel.editLibraryItem(LibraryItem(item.id, item.name))
                mainViewModel.getAllLibraryItems("%${editTextItem?.text.toString()}%")
            }
        })
    }

    private fun saveItemCount() {
        var checkedItemCounter = 0
        adapter?.currentList?.forEach {
            if (it.itemChecked) checkedItemCounter++
        }
        val tempShopListNameItem = shopListNameItem?.copy(
            time = TimeManager.getCurrentTime(),
            allItemCounter = adapter?.itemCount!!,
            checkedItemsCounter = checkedItemCounter
        )
        mainViewModel.editShopListName(tempShopListNameItem!!)
    }

    companion object {
        const val SHOP_LIST_NAME = "shop_list_name"
        const val SHARE_BY = "Share by"
    }
}