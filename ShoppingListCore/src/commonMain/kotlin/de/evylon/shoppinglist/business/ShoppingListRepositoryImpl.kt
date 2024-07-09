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
        trySync(listId) { previousList ->
            previousList.copy(
                items = previousList.items.filterNot { it.id == item.id }
            )
        }
    }

    override suspend fun addItem(listId: String, item: Item) {
        trySync(listId) { previousList ->
            previousList.copy(
                items = previousList.items.plus(item)
            )
        }
    }

    override suspend fun changeItem(listId: String, item: Item) {
        trySync(listId) { previousList ->
            previousList.copy(
                items = previousList.items.map {
                    if (it.id == it.id) item else it
                }
            )
        }
    }

    private suspend fun trySync(listId: String, transformList: (SyncedShoppingList) -> SyncedShoppingList) {
        val previousList = getLatestList(listId) ?: return // TODO add error handling
        val updatedList = transformList(previousList)
        if (previousList.id != updatedList.id) return // TODO add error handling
        // TODO separate last saved sync state from current ShoppingList
        _shoppingListFlow.emit(FetchState.Success(updatedList))
        val syncRequest = SyncRequest(
            previousSync = previousList,
            currentState = updatedList.toShoppingList()
        )
        _shoppingListFlow.loadCatchingAndEmit {
            shoppingListApi.requestSync(previousList.id, syncRequest)
        }
    }

    // TODO add caching and resolving for multiple lists
    private fun getLatestList(listId: String): SyncedShoppingList? {
        val list = (_shoppingListFlow.value as? FetchState.Success)?.value
        return if (list == null || list.id != listId) {
            null
        } else {
            list
        }
    }
}
