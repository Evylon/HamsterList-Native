package de.evylon.shoppinglist.business

import de.evylon.shoppinglist.models.AdditionalData
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.SyncRequest
import de.evylon.shoppinglist.models.SyncResponse
import de.evylon.shoppinglist.models.SyncedShoppingList
import de.evylon.shoppinglist.network.ShoppingListApi
import de.evylon.shoppinglist.utils.FetchState
import de.evylon.shoppinglist.utils.loadCatchingAndEmit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShoppingListRepositoryImpl : ShoppingListRepository {
    private val shoppingListApi = ShoppingListApi()

    // Flows
    private val _syncStateFlow = MutableStateFlow<FetchState<SyncResponse>>(FetchState.Loading)
    override val syncState = _syncStateFlow.asStateFlow()

    // Service Calls
    override suspend fun loadListById(id: String) {
        _syncStateFlow.loadCatchingAndEmit {
            shoppingListApi.getSyncedShoppingList(id)
        }
    }

    override suspend fun deleteItem(listId: String, item: Item) {
        trySync(listId) { previousList ->
            previousList.copy(
                items = previousList.items.filterNot {
                    it.itemId() == item.itemId()
                }
            )
        }
    }

    override suspend fun addItem(listId: String, item: Item.Text) {
        trySync(listId) { previousList ->
            previousList.copy(
                items = previousList.items.plus(item)
            )
        }
    }

    override suspend fun changeItem(listId: String, item: Item.Text) {
        trySync(listId) { previousList ->
            previousList.copy(
                items = previousList.items.map {
                    if (it.itemId() == item.itemId()) item else it
                }
            )
        }
    }

    // TODO separate last saved sync state from current ShoppingList
    private suspend fun trySync(listId: String, transformList: (SyncedShoppingList) -> SyncedShoppingList) {
        val previousList = getLatestList(listId) ?: return // TODO add error handling
        val updatedList = transformList(previousList)
        if (previousList.id != updatedList.id) return // TODO add error handling
        val syncRequest = SyncRequest(
            previousSync = previousList,
            currentState = updatedList.toShoppingList(),
            includeInResponse = listOf(AdditionalData.orders, AdditionalData.categories)
        )
        _syncStateFlow.loadCatchingAndEmit {
            shoppingListApi.requestSync(previousList.id, syncRequest)
        }
    }

    // TODO add caching and resolving for multiple lists
    private fun getLatestList(listId: String): SyncedShoppingList? {
        val list = (_syncStateFlow.value as? FetchState.Success)?.value?.list
        return if (list == null || list.id != listId) {
            null
        } else {
            list
        }
    }
}
