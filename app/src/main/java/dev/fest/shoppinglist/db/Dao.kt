package dev.fest.shoppinglist.db

import androidx.room.*
import androidx.room.Dao
import dev.fest.shoppinglist.entities.LibraryItem
import dev.fest.shoppinglist.entities.NoteItem
import dev.fest.shoppinglist.entities.ShopListNameItem
import dev.fest.shoppinglist.entities.ShopListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("SELECT * FROM node_list")
    fun getAllNotes(): Flow<List<NoteItem>>

    @Query("SELECT * FROM shop_list_names")
    fun getAllShopListNames(): Flow<List<ShopListNameItem>>

    @Query("SELECT * FROM shop_list_item WHERE listId LIKE :listId")
    fun getAllShopListItems(listId: Int): Flow<List<ShopListItem>>

    @Query("SELECT * FROM library WHERE name LIKE :name")
    suspend fun getAllLibraryItems(name: String): List<LibraryItem>

    @Query("DELETE FROM node_list WHERE id IS :id")
    suspend fun deleteNote(id: Int)

    @Query("DELETE FROM library WHERE id IS :id")
    suspend fun deleteLibraryItem(id: Int)

    @Query("DELETE FROM shop_list_names WHERE id IS :id")
    suspend fun deleteShopListName(id: Int)

    @Query("DELETE FROM shop_list_item WHERE listId IS :listId")
    suspend fun deleteShopItemsByListId(listId: Int)

    @Insert
    suspend fun insertNote(noteItem: NoteItem)

    @Insert
    suspend fun insertShopItem(shopListItem: ShopListItem)

    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem)

    @Insert
    suspend fun insertShopListName(shopListNameItemItem: ShopListNameItem)

    @Update
    suspend fun editNote(noteItem: NoteItem)

    @Update
    suspend fun editLibraryItem(libraryItem: LibraryItem)

    @Update
    suspend fun editShopListName(shopListNameItem: ShopListNameItem)

    @Update
    suspend fun editShopListItem(shopListItem: ShopListItem)

}