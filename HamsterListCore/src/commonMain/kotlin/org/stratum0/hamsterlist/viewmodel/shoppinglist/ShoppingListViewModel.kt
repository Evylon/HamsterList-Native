package org.stratum0.hamsterlist.viewmodel.shoppinglist

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import org.stratum0.hamsterlist.business.ShoppingListRepository
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.Order
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.utils.FetchState
import org.stratum0.hamsterlist.viewmodel.BaseViewModel
import org.stratum0.hamsterlist.viewmodel.LoadingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShoppingListViewModel : BaseViewModel() {
    private val shoppingListRepository: ShoppingListRepository = ShoppingListRepository.instance
    private val _uiState = MutableStateFlow(ShoppingListState.empty)

    @NativeCoroutinesState
    val uiState = _uiState.asStateFlow()

    init {
        shoppingListRepository.syncState.onEach { networkResult ->
            when (networkResult) {
                is FetchState.Success -> updateSyncState(networkResult.value)
                is FetchState.Failure -> _uiState.update { oldState ->
                    networkResult.throwable.printStackTrace()
                    oldState.copy(loadingState = LoadingState.Error)
                }
                is FetchState.Loading -> _uiState.update { oldState ->
                    oldState.copy(loadingState = LoadingState.Loading)
                }
            }
        }.launchIn(scope)
    }

    fun fetchList(listId: String) {
        scope.launch {
            shoppingListRepository.loadListById(listId)
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

    fun addItem(newItem: String) {
        scope.launch {
            shoppingListRepository.addItem(
                listId = _uiState.value.shoppingList.id,
                item = Item.parse(stringRepresentation = newItem)
            )
        }
    }

    fun changeItem(id: String, newItem: String) {
        scope.launch {
            shoppingListRepository.changeItem(
                listId = _uiState.value.shoppingList.id,
                item = Item.parse(stringRepresentation = newItem, id = id)
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
