package de.evylon.shoppinglist.business

import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.utils.NetworkResult
import kotlinx.coroutines.flow.StateFlow

interface ShoppingListRepository {
    val shoppingListFlow: StateFlow<NetworkResult<ShoppingList>?>

    suspend fun loadListById(id: String)
    suspend fun deleteItem(listId: String, item: Item)
    companion object {
        // TODO temporary solution for fast setup/testing, use dependency injection
        val instance: ShoppingListRepository = ShoppingListRepositoryImpl()
    }
}
