package de.evylon.shoppinglist.business

import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.SyncRequest
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
         val updatedList = list.copy(
             items = list.items.filterNot { it.id == item.id }
         )
        // TODO separate last saved sync state from current ShoppingList
        _shoppingListFlow.emit(FetchState.Success(updatedList))
        val syncRequest = SyncRequest(
            previousSync = list,
            currentState = updatedList.toShoppingList()
        )
        _shoppingListFlow.loadCatchingAndEmit {
            shoppingListApi.requestSync(listId, syncRequest)
        }
    }
}
