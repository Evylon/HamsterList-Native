package de.evylon.shoppinglist.business

import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.utils.Cancellable
import de.evylon.shoppinglist.utils.NetworkResult
import de.evylon.shoppinglist.utils.collect
import kotlinx.coroutines.flow.StateFlow

interface ShoppingListRepository {
    val shoppingListFlow: StateFlow<NetworkResult<ShoppingList>?>

    fun shoppingListFlow(
        onEach: (NetworkResult<ShoppingList>?) -> Unit,
        onCompletion: (Throwable?) -> Unit
    ): Cancellable = shoppingListFlow.collect(onEach, onCompletion)

    suspend fun loadListById(id: String)
    suspend fun deleteItem(listId: String, item: Item)
    companion object {
        // TODO temporary solution for fast setup/testing, use dependency injection
        val instance: ShoppingListRepository = ShoppingListRepositoryImpl()
    }
}
