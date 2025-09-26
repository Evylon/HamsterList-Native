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
import org.stratum0.hamsterlist.utils.Result
import org.stratum0.hamsterlist.utils.loadCatching

internal class ShoppingListRepositoryImpl(
    private val shoppingListApi: ShoppingListApi
) : ShoppingListRepository {

    // Flows
    private val _shoppingListFlow = MutableStateFlow<SyncResponse?>(null)
    override val shoppingList = _shoppingListFlow.asStateFlow()

    private val _syncStateFlow = MutableStateFlow<FetchState<Unit>>(FetchState.Loading)
    override val syncState = _syncStateFlow.asStateFlow()

    private var lastSync: SyncResponse? = null

    // Service Calls
    override suspend fun loadListById(id: String) {
        // first try loading from cache
        val cachedList = loadSyncFromCache(id) ?: SyncResponse(
            SyncedShoppingList(id = id),
            orders = emptyList(),
            categories = emptyList(),
            completions = emptyList()
        )
        _shoppingListFlow.emit(cachedList)
        // sync with server
        trySync {
            shoppingListApi.getSyncedShoppingList(id)
        }
    }

    private fun loadSyncFromCache(id: String): SyncResponse? {
        return null // TODO add caching and persistence
    }

    private suspend fun trySync(syncCall: suspend () -> SyncResponse) {
        _syncStateFlow.emit(FetchState.Loading)
        val syncedResult = loadCatching {
            syncCall()
        }
        when (syncedResult) {
            is Result.Success -> {
                _shoppingListFlow.emit(syncedResult.value)
                lastSync = syncedResult.value
                _syncStateFlow.emit(FetchState.Success(Unit))
            }
            is Result.Failure -> {
                _syncStateFlow.emit(FetchState.Failure(syncedResult.throwable))
            }
        }
    }

    private suspend fun updateStateLocally(transformState: (SyncResponse?) -> SyncResponse): SyncResponse {
        val newState = transformState(_shoppingListFlow.value)
        _shoppingListFlow.emit(newState)
        return newState
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
        _shoppingListFlow.value.let { previousState ->
            _shoppingListFlow.emit(
                SyncResponse(
                    updatedList,
                    orders = previousState?.orders.orEmpty(),
                    completions = previousState?.completions.orEmpty(),
                    categories = previousState?.categories.orEmpty()
                )
            )
        }
        val syncRequest = SyncRequest(
            previousSync = previousList,
            currentState = updatedList.toShoppingList(),
            includeInResponse = listOf(AdditionalData.orders, AdditionalData.categories, AdditionalData.completions)
        )
        trySync {
            shoppingListApi.requestSync(previousList.id, syncRequest)
        }
    }

    // TODO add caching and resolving for multiple lists
    private fun getLatestList(listId: String): SyncedShoppingList? {
        val list = (_shoppingListFlow.value as? FetchState.Success)?.value?.list
        return if (list == null || list.id != listId) {
            null
        } else {
            list
        }
    }
}
