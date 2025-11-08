package org.stratum0.hamsterlist.viewmodel.shoppinglist

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.stratum0.hamsterlist.business.SettingsRepository
import org.stratum0.hamsterlist.business.ShoppingListRepository
import org.stratum0.hamsterlist.business.ShoppingListRepositoryImpl
import org.stratum0.hamsterlist.models.CompletionItem
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.Order
import org.stratum0.hamsterlist.models.ShoppingList
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.network.ShoppingListApi
import org.stratum0.hamsterlist.viewmodel.BaseViewModel

@Suppress("TooManyFunctions")
class ShoppingListViewModel(
    userInputHamsterList: HamsterList,
    private val settingsRepository: SettingsRepository,
    shoppingListApi: ShoppingListApi
) : BaseViewModel() {
    /**
     * The id and title of a hamsterlist might be changed server-side.
     */
    private var hamsterList: HamsterList = userInputHamsterList
    private val shoppingListRepository: ShoppingListRepository = ShoppingListRepositoryImpl(
        settingsRepository = settingsRepository,
        shoppingListApi = shoppingListApi
    )

    private val _uiState = MutableStateFlow(
        ShoppingListState(
            shoppingList = ShoppingList(
                id = hamsterList.listId,
                title = hamsterList.listId,
                items = emptyList()
            )
        )
    )

    @NativeCoroutinesState
    val uiState = _uiState.asStateFlow()

    init {
        shoppingListRepository.clear()
        shoppingListRepository.lastSync.onEach { latestSync ->
            latestSync?.let {
                updateSyncState(latestSync)
            }
        }.launchIn(scope)
        shoppingListRepository.syncState.onEach { syncState ->
            _uiState.update { currentState ->
                currentState.copy(loadingState = syncState)
            }
        }.launchIn(scope)
        // load from cache
        settingsRepository.getCachedLists()
            .find { it.hamsterList == hamsterList }
            ?.let { cachedList ->
                updateSyncState(cachedList.lastSyncState, isFromCache = true)
            }
    }

    fun handleAction(action: ShoppingListAction) {
        when (action) {
            is ShoppingListAction.AddItem -> addItem(action.input)
            is ShoppingListAction.AddItemByCompletion -> addItemByCompletion(action.completionItem)
            is ShoppingListAction.ChangeCategoryForItem -> changeCategoryForItem(
                item = action.item,
                newCategoryId = action.newCategoryId
            )

            is ShoppingListAction.ChangeItem -> changeItem(
                oldItem = action.oldItem,
                newItem = action.newItem
            )

            is ShoppingListAction.DeleteItem -> deleteItem(action.item)
            is ShoppingListAction.FetchList -> fetchList()
            is ShoppingListAction.SelectOrder -> selectOrder(action.order)
            is ShoppingListAction.UpdateAddItemInput -> updateAddItemInput(action.input)
        }
    }

    private fun updateAddItemInput(newInput: String) {
        _uiState.update { oldState ->
            oldState.copy(addItemInput = newInput)
        }
    }

    private fun fetchList() {
        scope.launch {
            shoppingListRepository.loadHamsterList(hamsterList)
        }
    }

    private fun deleteItem(item: Item) {
        _uiState.update { currentState ->
            currentState.copy(
                shoppingList = shoppingListRepository.deleteItem(
                    hamsterList = hamsterList,
                    currentList = currentState.shoppingList,
                    item = item
                )
            )
        }
    }

    private fun addItem(userInput: String) {
        _uiState.update { currentState ->
            currentState.copy(
                shoppingList = shoppingListRepository.addItemInput(
                    hamsterList = hamsterList,
                    currentList = currentState.shoppingList,
                    itemInput = userInput
                )
            )
        }
    }

    private fun addItemByCompletion(completion: CompletionItem) {
        _uiState.update { currentState ->
            currentState.copy(
                shoppingList = shoppingListRepository.addItem(
                    hamsterList = hamsterList,
                    currentList = currentState.shoppingList,
                    item = completion.toItem()
                )
            )
        }
    }

    private fun changeItem(oldItem: Item, newItem: String) {
        // filter out newlines like webclient
        val newItemFiltered = newItem.replace("\n", " ")
        val parsedItem = Item.parse(
            stringRepresentation = newItemFiltered,
            id = oldItem.id,
            category = oldItem.category,
            categories = uiState.value.categories
        )
        _uiState.update { currentState ->
            currentState.copy(
                shoppingList = shoppingListRepository.changeItem(
                    hamsterList = hamsterList,
                    currentList = currentState.shoppingList,
                    item = parsedItem
                )
            )
        }

    }

    private fun changeCategoryForItem(item: Item, newCategoryId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                shoppingList = shoppingListRepository.addItem(
                    hamsterList = hamsterList,
                    currentList = currentState.shoppingList,
                    item = item.copy(category = newCategoryId)
                )
            )
        }
    }

    private fun selectOrder(order: Order) {
        _uiState.update { oldState ->
            oldState.copy(
                shoppingList = oldState.shoppingList.copy(
                    items = oldState.shoppingList.items.sortedByOrder(order)
                ),
                selectedOrder = order
            )
        }
    }

    private fun updateSyncState(syncResponse: SyncResponse, isFromCache: Boolean = false) {
        _uiState.update { oldState ->
            val selectedOrder = oldState.selectedOrder ?: syncResponse.orders.firstOrNull()
            oldState.copy(
                shoppingList = syncResponse.list.copy(
                    items = syncResponse.list.items.sortedByOrder(selectedOrder)
                ).toShoppingList(),
                categories = syncResponse.categories,
                completions = syncResponse.completions.distinctBy { it.name },
                orders = syncResponse.orders,
                selectedOrder = selectedOrder,
            )
        }
        if (!isFromCache) {
            updateHamsterList(syncResponse)
            settingsRepository.updateCachedSync(
                hamsterList = hamsterList,
                newSyncResponse = syncResponse,
            )
        }
    }

    private fun updateHamsterList(syncResponse: SyncResponse) {
        val syncedList = syncResponse.list
        if (hamsterList.listId != syncedList.id || hamsterList.title != syncedList.title) {
            settingsRepository.deleteKnownList(hamsterList)
            hamsterList = hamsterList.copy(
                listId = syncedList.id,
                title = syncedList.title
            )
            settingsRepository.addKnownList(hamsterList)
        }
    }

    private fun List<Item>.sortedByOrder(selectedOrder: Order?) =
        this.sortedWith(
            compareBy(nullsLast(orderComparator(selectedOrder))) { it.category }
        )

    private fun orderComparator(selectedOrder: Order?) =
        Comparator { category1: String, category2: String ->
            selectedOrder?.categoryOrder?.let {
                it.indexOfOrMax(category1) - it.indexOfOrMax(category2)
            } ?: category1.compareTo(category2)
        }

    private fun <T> List<T>.indexOfOrMax(element: T): Int {
        return when (val index = indexOf(element)) {
            -1 -> Int.MAX_VALUE
            else -> index
        }
    }
}
