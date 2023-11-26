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
import kotlinx.coroutines.launch

class ShoppingListReducer(
    coroutineScope: CoroutineScope,
    private val shoppingListRepository: ShoppingListRepository = ShoppingListRepository.instance
) : Reducer<ShoppingListAction, ShoppingListState>(coroutineScope) {

    private val _flow = MutableStateFlow(ShoppingListState.inital)
    override val stateFlow = _flow.asStateFlow()

    init {
        coroutineScope.launch(dispatcher) {
            shoppingListRepository.shoppingListFlow.collect { result ->
                when (result) {
                    is NetworkResult.Success -> reduce(UpdateList(result.value))
                    is NetworkResult.Failure -> result.throwable.printStackTrace()
                    null -> {} // do nothing?
                }
            }
        }
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
        val oldState = _flow.value
        coroutineScope.launch(dispatcher) {
            shoppingListRepository.deleteItem(oldState.shoppingList.id, item)
        }
    }

    private fun updateList(shoppingList: ShoppingList) {
        coroutineScope.launch(dispatcher) {
            _flow.emit(
                _flow.value.copy(
                    shoppingList = shoppingList,
                    loadingState = LoadingState.Done
                )
            )
        }
    }
}
