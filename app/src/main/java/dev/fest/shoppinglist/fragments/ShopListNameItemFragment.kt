package dev.fest.shoppinglist.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import dev.fest.shoppinglist.R
import dev.fest.shoppinglist.activities.MainApp
import dev.fest.shoppinglist.activities.ShopListActivity
import dev.fest.shoppinglist.databinding.FragmentShoppingListNamesBinding
import dev.fest.shoppinglist.db.MainViewModel
import dev.fest.shoppinglist.db.ShopListNameAdapter
import dev.fest.shoppinglist.dialogs.DeleteDialog
import dev.fest.shoppinglist.dialogs.NewListDialog
import dev.fest.shoppinglist.entities.ShopListNameItem
import dev.fest.shoppinglist.utils.TimeManager

class ShopListNameItemFragment : BaseFragment(), ShopListNameAdapter.Listener {

    private lateinit var binding: FragmentShoppingListNamesBinding

    private lateinit var adapter: ShopListNameAdapter

    private lateinit var defaultSharedPreferences: SharedPreferences

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener {
            override fun onClick(name: String) {
                val shopListName =
                    ShopListNameItem(null, name, TimeManager.getCurrentTime(), 0, 0, "")
                mainViewModel.insertShopListName(shopListName)
            }
        }, "")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShoppingListNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observer()
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.shoplist_action_bar_shoplist)
        }
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                mainViewModel.deleteShopList(id, true)
            }
        })
    }

    override fun editItem(shopListNameItem: ShopListNameItem) {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener {
            override fun onClick(name: String) {
                mainViewModel.editShopListName(
                    shopListNameItem.copy(
                        name = name,
                        time = TimeManager.getCurrentTime()
                    )
                )
            }
        }, shopListNameItem.name)
    }

    override fun onClickItem(shopListNameItem: ShopListNameItem) {
        val intent = Intent(activity, ShopListActivity::class.java).apply {
            putExtra(ShopListActivity.SHOP_LIST_NAME, shopListNameItem)
        }
        startActivity(intent)
    }

    private fun initRecyclerView() = with(binding) {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        adapter = ShopListNameAdapter(this@ShopListNameItemFragment, defaultSharedPreferences)
        recyclerView.adapter = adapter
    }

    private fun observer() {
        mainViewModel.allShopListNamesItem.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            binding.textViewEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShopListNameItemFragment()
    }

}