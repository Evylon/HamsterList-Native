package org.stratum0.hamsterlist.business

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.stratum0.hamsterlist.models.AdditionalData
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.SyncRequest
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.models.SyncedShoppingList
import org.stratum0.hamsterlist.network.ShoppingListApi
import org.stratum0.hamsterlist.utils.FetchState
import org.stratum0.hamsterlist.utils.loadCatchingAndEmit

internal class ShoppingListRepositoryImpl(
    private val shoppingListApi: ShoppingListApi
) : ShoppingListRepository {

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
                    it.id == item.id
                }
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
                    if (it.id == item.id) item else it
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
