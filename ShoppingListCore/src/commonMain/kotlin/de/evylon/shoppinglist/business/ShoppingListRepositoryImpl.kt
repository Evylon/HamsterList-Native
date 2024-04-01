package de.evylon.shoppinglist.business

import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.models.SyncedShoppingList
import de.evylon.shoppinglist.network.ShoppingListApi
import de.evylon.shoppinglist.utils.FetchState
import de.evylon.shoppinglist.utils.loadCatchingAndEmit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShoppingListRepositoryImpl : ShoppingListRepository {
    private val shoppingListApi = ShoppingListApi()

    // Flows
    private val _shoppingListFlow = MutableStateFlow<FetchState<SyncedShoppingList>>(FetchState.Loading)
    override val shoppingList = _shoppingListFlow.asStateFlow()

    // Service Calls
    override suspend fun loadListById(id: String) {
        _shoppingListFlow.loadCatchingAndEmit {
            shoppingListApi.getSyncedShoppingList(id)
        }
    }

    override suspend fun deleteItem(listId: String, item: Item) {
        val list = (_shoppingListFlow.value as? FetchState.Success)?.value
        if (list == null || list.id != listId) return // TODO
        _shoppingListFlow.loadCatchingAndEmit {
            shoppingListApi.deleteItem(list, item)
        }
    }
}
