package org.stratum0.hamsterlist.business

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.stratum0.hamsterlist.models.CategoryDefinition
import org.stratum0.hamsterlist.models.CompletionItem
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.HamsterListDataException
import org.stratum0.hamsterlist.models.Item
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
    private val settingsRepository: SettingsRepository
) : ShoppingListRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    // Flows
    private val _syncStateFlow = MutableStateFlow<LoadingState>(LoadingState.Loading)
    override val syncState = _syncStateFlow.asStateFlow()

    private var _lastSyncFlow = MutableStateFlow<SyncResponse?>(null)
    override var lastSync = _lastSyncFlow.asStateFlow()

    private val _sharedItemsFlow = MutableStateFlow<List<String>?>(null)
    override val sharedItems: StateFlow<List<String>?> = _sharedItemsFlow.asStateFlow()

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
    override suspend fun loadHamsterList(hamsterList: HamsterList): Result<ShoppingList> {
        val cachedList = settingsRepository.getCachedLists().find { it.hamsterList == hamsterList }
        // if cached list exists, request a sync instead of just loading the list.
        return if (cachedList != null) {
            _lastSyncFlow.update { cachedList.lastSyncState }
            scope.launch {
                executeSync {
                    shoppingListApi.requestSync(
                        hamsterList,
                        SyncRequest(cachedState = cachedList)
                    )
                }
            }
            Result.Success(cachedList.currentList)
        } else {
            val syncResult = executeSync {
                shoppingListApi.getSyncedShoppingList(hamsterList)
            }
            syncResult.mapSuccess { it.list.toShoppingList() }
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
        hamsterList: HamsterList,
        currentList: ShoppingList,
        items: List<Item>
    ): ShoppingList {
        _sharedItemsFlow.update { null }
        return addItems(hamsterList, currentList, items, skipQueue = true)
    }

    override fun enqueueSharedContent(content: String) {
        _sharedItemsFlow.update {
            content.split("\n")
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
        _syncStateFlow.update { LoadingState.SyncEnqueued }
        val lastSync = this.lastSync.value
        if (lastSync == null) {
            // TODO error handling
            _syncStateFlow.update { LoadingState.Error(HamsterListDataException(null)) }
            return
        }
        scope.launch {
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

    private suspend fun executeSync(
        syncAction: suspend () -> SyncResponse
    ): Result<SyncResponse> {
        _syncStateFlow.update { LoadingState.Loading }
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

    private fun parseItemAndCheckCompletions(
        input: String,
        categories: List<CategoryDefinition>,
        completions: List<CompletionItem>
    ): Item {
        val parsedItem = Item.parse(
            stringRepresentation = input,
            categories = categories
        )
        val completion = completions.find { it.name == parsedItem.name }
        return if (completion != null) {
            parsedItem.copy(
                id = parsedItem.id,
                name = completion.name,
                amount = parsedItem.amount,
                category = parsedItem.category ?: completion.category
            )
        } else {
            parsedItem
        }
    }
}
