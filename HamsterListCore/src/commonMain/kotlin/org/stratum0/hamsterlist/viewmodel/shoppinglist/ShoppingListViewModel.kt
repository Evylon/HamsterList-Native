package org.stratum0.hamsterlist.viewmodel.shoppinglist

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.stratum0.hamsterlist.business.SettingsRepository
import org.stratum0.hamsterlist.business.ShoppingListRepository
import org.stratum0.hamsterlist.models.CachedHamsterList
import org.stratum0.hamsterlist.models.CategoryDefinition
import org.stratum0.hamsterlist.models.CompletionItem
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.Order
import org.stratum0.hamsterlist.models.ShoppingList
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.viewmodel.BaseViewModel
import org.stratum0.hamsterlist.viewmodel.LoadingState

@Suppress("TooManyFunctions")
class ShoppingListViewModel(
    private val hamsterList: HamsterList,
    private val shoppingListRepository: ShoppingListRepository,
    private val settingsRepository: SettingsRepository
) : BaseViewModel() {
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
        // load last sync if available
        val cachedList = settingsRepository.getCachedLists().find { it.hamsterList == hamsterList }
        if (cachedList != null) {
            updateSyncState(cachedList.syncResponse)
        }

        shoppingListRepository.lastSync.onEach { latestSync ->
            latestSync?.let {
                updateSyncState(latestSync)
                settingsRepository.updateCachedList(
                    CachedHamsterList(hamsterList, latestSync)
                )
            }
        }.launchIn(scope)
        shoppingListRepository.syncState.onEach { syncState ->
            _uiState.update { currentState ->
                currentState.copy(loadingState = syncState)
            }
        }.launchIn(scope)
        shoppingListRepository.sharedItems.onEach { sharedItems ->
            if (sharedItems != null) {
                handleSharedItems(sharedItems)
            }
        }.launchIn(scope)
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

    private suspend fun handleSharedItems(shareItems: List<String>) {
        // wait for list to load
        val uiState = uiState.first { it.loadingState !is LoadingState.Loading }
        if (uiState.loadingState is LoadingState.Done) {
            shoppingListRepository.handleSharedItems(
                hamsterList = hamsterList,
                currentList = uiState.shoppingList,
                items = shareItems.map { sharedItem ->
                    parseItemAndCheckCompletions(
                        input = sharedItem,
                        categories = uiState.categories,
                        completions = uiState.completions
                    )
                }
            )
        } else {
            // TODO show dialog
            println("Error loading the list")
        }
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

    private fun updateSyncState(syncResponse: SyncResponse) {
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
                loadingState = LoadingState.Done
            )
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
