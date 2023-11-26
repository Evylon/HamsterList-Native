package de.evylon.shoppinglist.android.gui.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.reducers.shoppinglist.ShoppingListAction
import de.evylon.shoppinglist.reducers.shoppinglist.ShoppingListReducer

class ShoppingListViewModel : ViewModel() {

    private var reducer = ShoppingListReducer(coroutineScope = viewModelScope)
    val uiStateFlow = reducer.stateFlow

    fun loadList(id: String) {
        reducer.reduce(ShoppingListAction.FetchList(id))
    }

    fun deleteItem(item: Item) {
        reducer.reduce(ShoppingListAction.DeleteItem(item))
    }
}
