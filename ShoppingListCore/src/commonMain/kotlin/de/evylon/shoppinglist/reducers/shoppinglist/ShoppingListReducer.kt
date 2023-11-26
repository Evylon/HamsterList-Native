package de.evylon.shoppinglist.reducers.shoppinglist

import de.evylon.shoppinglist.business.ShoppingListRepository
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.reducers.LoadingState
import de.evylon.shoppinglist.reducers.Reducer
import de.evylon.shoppinglist.reducers.shoppinglist.ShoppingListAction.DeleteItem
import de.evylon.shoppinglist.reducers.shoppinglist.ShoppingListAction.FetchList
import de.evylon.shoppinglist.reducers.shoppinglist.ShoppingListAction.UpdateList
import de.evylon.shoppinglist.utils.NetworkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ShoppingListReducer(
    coroutineScope: CoroutineScope,
    private val shoppingListRepository: ShoppingListRepository = ShoppingListRepository.instance
) : Reducer<ShoppingListAction, ShoppingListState>(coroutineScope) {

    private val _uiStateFlow = MutableStateFlow(ShoppingListState.empty)
    override val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        shoppingListRepository.shoppingListFlow.onEach { networkResult ->
            when (networkResult) {
                is NetworkResult.Success -> reduce(UpdateList(networkResult.value))
                is NetworkResult.Failure -> networkResult.throwable.printStackTrace()
                null -> {} // do nothing?
            }
        }.launchIn(coroutineScope)
    }

    override fun reduce(action: ShoppingListAction) {
        when (action) {
            is FetchList -> fetchList(action.listId)
            is DeleteItem -> deleteItem(action.item)
            is UpdateList -> updateList(action.shoppingList)
        }
    }

    private fun fetchList(listId: String) {
        coroutineScope.launch(dispatcher) {
            shoppingListRepository.loadListById(listId)
        }
    }

    private fun deleteItem(item: Item) {
        val oldState = _uiStateFlow.value
        coroutineScope.launch(dispatcher) {
            shoppingListRepository.deleteItem(oldState.shoppingList.id, item)
        }
    }

    private fun updateList(shoppingList: ShoppingList) {
        coroutineScope.launch(dispatcher) {
            _uiStateFlow.emit(
                _uiStateFlow.value.copy(
                    shoppingList = shoppingList,
                    loadingState = LoadingState.Done
                )
            )
        }
    }
}
