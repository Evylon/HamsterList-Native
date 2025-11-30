package org.stratum0.hamsterlist.business

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.stratum0.hamsterlist.models.CachedHamsterList
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.Item.Companion.parseItemAndCheckCompletions
import org.stratum0.hamsterlist.models.Result
import org.stratum0.hamsterlist.models.ShoppingList
import org.stratum0.hamsterlist.models.SyncRequest
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.models.loadCatching
import org.stratum0.hamsterlist.network.ShoppingListApi
import org.stratum0.hamsterlist.viewmodel.LoadingState
import kotlin.time.Duration.Companion.seconds

internal class ShoppingListRepositoryImpl(
    private val shoppingListApi: ShoppingListApi,
    private val settingsRepository: SettingsRepository,
    private val sharedContentManager: SharedContentManager
) : ShoppingListRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    // Flows
    private val _syncStateFlow = MutableStateFlow<LoadingState>(LoadingState.Loading)
    override val syncState = _syncStateFlow.asStateFlow()

    private var _lastSyncFlow = MutableStateFlow<SyncResponse?>(null)
    override var lastSync = _lastSyncFlow.asStateFlow()

    private val syncRequestFlow = MutableStateFlow<SyncQueueItem?>(null)

    private data class SyncQueueItem(
        val hamsterList: HamsterList,
        val syncRequest: SyncRequest
    )

    init {
        @OptIn(FlowPreview::class)
        syncRequestFlow
            .filterNotNull()
            .debounce(1.seconds)
            .onEach { syncQueueItem ->
                executeSync {
                    shoppingListApi.requestSync(
                        syncQueueItem.hamsterList,
                        syncQueueItem.syncRequest
                    )
                }
            }
            .launchIn(scope)
    }

    // Service Calls
    override suspend fun loadHamsterList(hamsterList: HamsterList) {
        val cachedList = settingsRepository.getCachedLists().find { it.hamsterList == hamsterList }
        // if cached list exists, request a sync instead of just loading the list.
        if (cachedList != null) {
            _lastSyncFlow.update { cachedList.lastSyncState }
            val sharedItems = sharedContentManager.sharedItems.value
            if (sharedItems != null) {
                handleSharedItems(cachedList, sharedItems)
            } else {
                scope.launch {
                    executeSync(LoadingState.Loading) {
                        shoppingListApi.requestSync(
                            hamsterList,
                            SyncRequest(cachedState = cachedList)
                        )
                    }
                }
            }
        } else {
            executeSync(LoadingState.Loading) {
                val initialSync = shoppingListApi.getSyncedShoppingList(hamsterList)
                /**
                 * Check if user is not using name as id.
                 * In that case we get the uuid as title and need to overwrite it.
                 */
                if (hamsterList.titleOrId == initialSync.list.title) return@executeSync initialSync
                shoppingListApi.requestSync(
                    hamsterList,
                    SyncRequest(
                        previousSync = initialSync,
                        updatedList = initialSync.list.toShoppingList().copy(
                            title = hamsterList.titleOrId
                        )
                    )
                )
            }
        }
    }

    override fun deleteItem(
        hamsterList: HamsterList,
        currentList: ShoppingList,
        item: Item
    ): ShoppingList {
        return transformListAndSync(hamsterList, currentList) { currentList ->
            currentList.copy(
                items = currentList.items.filterNot { it.id == item.id }
            )
        }
    }

    override fun addItemInput(
        hamsterList: HamsterList,
        currentList: ShoppingList,
        itemInput: String
    ): ShoppingList {
        return addItems(
            hamsterList = hamsterList,
            currentList = currentList,
            items = itemInput
                .split("\n")
                .map { input ->
                    parseItemAndCheckCompletions(
                        input = input,
                        categories = lastSync.value?.categories.orEmpty(),
                        completions = lastSync.value?.completions.orEmpty()
                    )
                }
        )
    }

    override fun addItem(
        hamsterList: HamsterList,
        currentList: ShoppingList,
        item: Item
    ): ShoppingList {
        return transformListAndSync(hamsterList, currentList) { currentList ->
            currentList.copy(
                items = currentList.items.plus(item)
            )
        }
    }

    override fun addItems(
        hamsterList: HamsterList,
        currentList: ShoppingList,
        items: List<Item>,
        skipQueue: Boolean
    ): ShoppingList {
        return transformListAndSync(
            hamsterList = hamsterList,
            currentList = currentList,
            skipQueue = skipQueue
        ) { currentList ->
            currentList.copy(
                items = currentList.items.plus(items)
            )
        }
    }

    override fun changeItem(
        hamsterList: HamsterList,
        currentList: ShoppingList,
        item: Item
    ): ShoppingList {
        return transformListAndSync(hamsterList, currentList) { currentList ->
            currentList.copy(
                items = currentList.items.map {
                    if (it.id == item.id) item else it
                }
            )
        }
    }

    override fun handleSharedItems(
        cachedList: CachedHamsterList,
        items: List<String>
    ): ShoppingList {
        val parsedItems = items.map { sharedItem ->
            parseItemAndCheckCompletions(
                input = sharedItem,
                categories = cachedList.lastSyncState.categories,
                completions = cachedList.lastSyncState.completions
            )
        }
        sharedContentManager.clearSharedItems()
        return addItems(
            hamsterList = cachedList.hamsterList,
            currentList = cachedList.currentList,
            items = parsedItems,
            skipQueue = true
        )
    }

    override fun changeListTitle(
        hamsterList: HamsterList,
        currentList: ShoppingList,
        newTitle: String,
        skipQueue: Boolean
    ): ShoppingList {
        return transformListAndSync(hamsterList, currentList, skipQueue) { currentList ->
            currentList.copy(title = newTitle)
        }
    }

    override fun clear() {
        _syncStateFlow.update { LoadingState.Loading }
        _lastSyncFlow.update { null }
        syncRequestFlow.update { null }
    }

    private fun transformListAndSync(
        hamsterList: HamsterList,
        currentList: ShoppingList,
        skipQueue: Boolean = false,
        transform: (ShoppingList) -> ShoppingList
    ): ShoppingList {
        val updatedList = transform(currentList)
        val lastSync = lastSync.value
        if (skipQueue && lastSync != null) {
            scope.launch {
                executeSync {
                    shoppingListApi.requestSync(
                        hamsterList = hamsterList,
                        syncRequest = SyncRequest(lastSync, updatedList)
                    )
                }
            }
        } else {
            enqueueSync(
                hamsterList = hamsterList,
                updatedList = updatedList
            )
        }
        settingsRepository.updateCachedList(hamsterList, updatedList)
        return updatedList
    }

    private fun enqueueSync(
        hamsterList: HamsterList,
        updatedList: ShoppingList
    ) {
        _syncStateFlow.update { LoadingState.Syncing }
        scope.launch {
            val lastSync = getOrReloadSync(hamsterList)
            if (lastSync != null) {
                syncRequestFlow.emit(
                    SyncQueueItem(
                        hamsterList,
                        SyncRequest(
                            previousSync = lastSync,
                            updatedList = updatedList,
                        )
                    )
                )
            }
        }
    }

    private suspend fun getOrReloadSync(hamsterList: HamsterList): SyncResponse? {
        val lastSync = lastSync.value
        return if (lastSync != null) {
            lastSync
        } else {
            val syncResult = executeSync {
                shoppingListApi.getSyncedShoppingList(hamsterList)
            }
            if (syncResult is Result.Success) {
                syncResult.value
            } else {
                null
            }
        }
    }

    private suspend fun executeSync(
        loadingState: LoadingState = LoadingState.Syncing,
        syncAction: suspend () -> SyncResponse
    ): Result<SyncResponse> {
        _syncStateFlow.update { loadingState }
        val syncResult = loadCatching {
            syncAction()
        }
        when (syncResult) {
            is Result.Success -> {
                _lastSyncFlow.update { syncResult.value }
                _syncStateFlow.update { LoadingState.Done }
                syncRequestFlow.emit(null)
            }

            is Result.Failure -> _syncStateFlow.update {
                syncResult.exception.printStackTrace()
                LoadingState.Error(syncResult.exception)
            }
        }
        return syncResult
    }
}
