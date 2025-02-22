package org.stratum0.hamsterlist.viewmodel.shoppinglist

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.stratum0.hamsterlist.business.ShoppingListRepository
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.Order
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.models.SyncedShoppingList
import org.stratum0.hamsterlist.utils.FetchState
import org.stratum0.hamsterlist.viewmodel.BaseViewModel
import org.stratum0.hamsterlist.viewmodel.LoadingState

@Suppress("TooManyFunctions")
class ShoppingListViewModel(
    private val hamsterListId: String,
    private val shoppingListRepository: ShoppingListRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(
        ShoppingListState(shoppingList = SyncedShoppingList(id = hamsterListId))
    )

    @NativeCoroutinesState
    val uiState = _uiState.asStateFlow()

    init {
        shoppingListRepository.syncState.onEach { networkResult ->
            when (networkResult) {
                is FetchState.Success -> updateSyncState(networkResult.value)
                is FetchState.Failure -> _uiState.update { oldState ->
                    networkResult.throwable.printStackTrace()
                    oldState.copy(loadingState = LoadingState.Error(networkResult.throwable))
                }

                is FetchState.Loading -> _uiState.update { oldState ->
                    oldState.copy(loadingState = LoadingState.Loading)
                }
            }
        }.launchIn(scope)
    }

    fun updateAddItemInput(newInput: String) {
        _uiState.update { oldState ->
            oldState.copy(addItemInput = newInput)
        }
    }

    fun fetchList() {
        scope.launch {
            shoppingListRepository.loadListById(hamsterListId)
        }
    }

    fun deleteItem(item: Item) {
        scope.launch {
            shoppingListRepository.deleteItem(
                listId = _uiState.value.shoppingList.id,
                item = item
            )
        }
    }

    fun addItem(newItem: String, completion: String?, category: String?) {
        val parsedItem = Item.parse(
            stringRepresentation = newItem,
            category = category,
            categories = uiState.value.categories
        ).let {
            // change name to completion if it is present
            if (completion != null) it.copy(name = completion) else it
        }
        scope.launch {
            shoppingListRepository.addItem(
                listId = _uiState.value.shoppingList.id,
                item = parsedItem
            )
        }
    }

    fun changeItem(oldItem: Item, newItem: String) {
        scope.launch {
            shoppingListRepository.changeItem(
                listId = _uiState.value.shoppingList.id,
                item = Item.parse(
                    stringRepresentation = newItem,
                    id = oldItem.id,
                    category = oldItem.category,
                    categories = uiState.value.categories
                )
            )
        }
    }

    fun changeCategoryForItem(item: Item, newCategoryId: String) {
        scope.launch {
            shoppingListRepository.changeItem(
                listId = _uiState.value.shoppingList.id,
                item = item.copy(category = newCategoryId)
            )
        }
    }

    fun selectOrder(order: Order) {
        _uiState.update { oldState ->
            oldState.copy(
                shoppingList = oldState.shoppingList.copy(
                    items = oldState.shoppingList.items.sortedByOrder(order)
                ),
                selectedOrder = order
            )
        }
    }

    private fun updateSyncState(syncResponse: SyncResponse) {
        _uiState.update { oldState ->
            val selectedOrder = oldState.selectedOrder ?: syncResponse.orders.firstOrNull()
            oldState.copy(
                shoppingList = syncResponse.list.copy(
                    items = syncResponse.list.items.sortedByOrder(selectedOrder)
                ),
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
