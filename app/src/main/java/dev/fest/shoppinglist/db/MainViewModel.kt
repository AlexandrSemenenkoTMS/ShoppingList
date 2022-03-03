package dev.fest.shoppinglist.db

import androidx.lifecycle.*
import dev.fest.shoppinglist.entities.LibraryItem
import dev.fest.shoppinglist.entities.NoteItem
import dev.fest.shoppinglist.entities.ShopListItem
import dev.fest.shoppinglist.entities.ShopListNameItem
import kotlinx.coroutines.launch

class MainViewModel(dataBase: MainDataBase) : ViewModel() {

    private val dao = dataBase.getDao()

    val libraryItems = MutableLiveData<List<LibraryItem>>()

    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData()

    val allShopListNamesItem: LiveData<List<ShopListNameItem>> =
        dao.getAllShopListNames().asLiveData()

    fun getAllItemsFromList(listId: Int): LiveData<List<ShopListItem>> {
        return dao.getAllShopListItems(listId).asLiveData()
    }

    fun getAllLibraryItems(name: String) = viewModelScope.launch {
        libraryItems.postValue(dao.getAllLibraryItems(name))
    }

    fun insertNote(note: NoteItem) = viewModelScope.launch {
        dao.insertNote(note)
    }

    fun insertShopListName(shopListNameItem: ShopListNameItem) = viewModelScope.launch {
        dao.insertShopListName(shopListNameItem)
    }

    fun insertShopItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.insertShopItem(shopListItem)
        if (!isLibraryItemsExists(shopListItem.name)) {
            dao.insertLibraryItem(LibraryItem(null, shopListItem.name))
        }
    }

    fun editNote(note: NoteItem) = viewModelScope.launch {
        dao.editNote(note)
    }

    fun editLibraryItem(libraryItem: LibraryItem) = viewModelScope.launch {
        dao.editLibraryItem(libraryItem)
    }

    fun editShopListName(shopListNameItem: ShopListNameItem) = viewModelScope.launch {
        dao.editShopListName(shopListNameItem)
    }

    fun editShopListItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.editShopListItem(shopListItem)
    }

    fun deleteNote(id: Int) = viewModelScope.launch {
        dao.deleteNote(id)
    }

    fun deleteLibraryItem(id: Int) = viewModelScope.launch {
        dao.deleteLibraryItem(id)
    }

    fun deleteShopList(id: Int, deleteList: Boolean) = viewModelScope.launch {
        if (deleteList) {
            dao.deleteShopListName(id)
        }
        dao.deleteShopItemsByListId(id)
    }

    private suspend fun isLibraryItemsExists(name: String): Boolean {
        return dao.getAllLibraryItems(name).isNotEmpty()
    }

    class MainViewModelFactory(private val dataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(dataBase) as T
            }
            throw  IllegalAccessException("Unknown ViewModelClass")
        }

    }
}