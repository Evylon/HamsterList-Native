package de.evylon.shoppinglist.viewmodel.shoppinglist

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import de.evylon.shoppinglist.business.ShoppingListRepository
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.viewmodel.LoadingState
import de.evylon.shoppinglist.utils.FetchState
import de.evylon.shoppinglist.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ShoppingListViewModel : BaseViewModel() {
    private val shoppingListRepository: ShoppingListRepository = ShoppingListRepository.instance
    private val _uiState = MutableStateFlow(ShoppingListState.empty)

    @NativeCoroutinesState
    val uiState = _uiState.asStateFlow()

    init {
        shoppingListRepository.shoppingList.onEach { networkResult ->
            when (networkResult) {
                is FetchState.Success -> updateList(networkResult.value)
                is FetchState.Failure -> networkResult.throwable.printStackTrace()
                is FetchState.Loading -> {} // TODO
            }
        }.launchIn(scope)
    }

    fun fetchList(listId: String) {
        scope.launch {
            shoppingListRepository.loadListById(listId)
        }
    }

    fun deleteItem(item: Item) {
        val oldState = _uiState.value
        scope.launch {
            shoppingListRepository.deleteItem(oldState.shoppingList.id, item)
        }
    }

    fun updateList(shoppingList: ShoppingList) {
        scope.launch {
            _uiState.emit(
                _uiState.value.copy(
                    shoppingList = shoppingList,
                    loadingState = LoadingState.Done
                )
            )
        }
    }
}
