package de.evylon.shoppinglist.reducers.fetchlist

import de.evylon.shoppinglist.business.ShoppingListRepository
import de.evylon.shoppinglist.business.ShoppingListRepositoryImpl
import de.evylon.shoppinglist.reducers.LoadingState
import de.evylon.shoppinglist.reducers.Reducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FetchListReducer(
    coroutineScope: CoroutineScope,
    private val shoppingListRepository: ShoppingListRepository = ShoppingListRepositoryImpl(),
) : Reducer<FetchListAction, FetchListState>(coroutineScope) {

    private val _state = MutableStateFlow(FetchListState(LoadingState.Loading))
    override val uiStateFlow = _state.asStateFlow()

    override fun reduce(action: FetchListAction) {
        when (action) {
            is FetchListAction.FetchList -> {
                coroutineScope.launch {
                    shoppingListRepository.loadListById(action.listId)
                }
            }
        }
    }
}
