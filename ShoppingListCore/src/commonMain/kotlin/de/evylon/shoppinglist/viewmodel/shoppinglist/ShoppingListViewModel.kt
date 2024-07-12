package de.evylon.shoppinglist.viewmodel.shoppinglist

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import de.evylon.shoppinglist.business.ShoppingListRepository
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.SyncResponse
import de.evylon.shoppinglist.viewmodel.LoadingState
import de.evylon.shoppinglist.utils.FetchState
import de.evylon.shoppinglist.viewmodel.BaseViewModel
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

    private fun updateSyncState(syncResponse: SyncResponse) {
        scope.launch {
            _uiState.emit(
                _uiState.value.copy(
                    shoppingList = syncResponse.list,
                    categories = syncResponse.categories,
                    orders = syncResponse.orders,
                    loadingState = LoadingState.Done
                )
            )
        }
    }
}
