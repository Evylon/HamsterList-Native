package de.evylon.shoppinglist.business

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.SyncedShoppingList
import de.evylon.shoppinglist.utils.FetchState
import kotlinx.coroutines.flow.StateFlow

interface ShoppingListRepository {
    @NativeCoroutinesState
    val shoppingList: StateFlow<FetchState<SyncedShoppingList>>

    @NativeCoroutines
    suspend fun loadListById(id: String)
    @NativeCoroutines
    suspend fun deleteItem(listId: String, item: Item)
    @NativeCoroutines
    suspend fun addItem(listId: String, item: Item.Text)
    @NativeCoroutines
    suspend fun changeItem(listId: String, item: Item.Text)
    companion object {
        // TODO temporary solution for fast setup/testing, use dependency injection
        val instance: ShoppingListRepository = ShoppingListRepositoryImpl()
    }
}
