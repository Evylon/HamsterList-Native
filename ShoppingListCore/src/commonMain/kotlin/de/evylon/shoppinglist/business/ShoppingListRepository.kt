package de.evylon.shoppinglist.business

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.utils.FetchState
import kotlinx.coroutines.flow.StateFlow

interface ShoppingListRepository {
    @NativeCoroutinesState
    val shoppingList: StateFlow<FetchState<ShoppingList>>

    @NativeCoroutines
    suspend fun loadListById(id: String)
    @NativeCoroutines
    suspend fun deleteItem(listId: String, item: Item)
    companion object {
        // TODO temporary solution for fast setup/testing, use dependency injection
        val instance: ShoppingListRepository = ShoppingListRepositoryImpl()
    }
}
