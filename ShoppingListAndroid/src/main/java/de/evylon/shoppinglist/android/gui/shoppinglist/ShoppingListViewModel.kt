package de.evylon.shoppinglist.android.gui.shoppinglist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.evylon.shoppinglist.android.gui.utils.LoadingState
import de.evylon.shoppinglist.android.gui.utils.LoadingState.Done
import de.evylon.shoppinglist.android.gui.utils.LoadingState.Error
import de.evylon.shoppinglist.android.gui.utils.LoadingState.Loading
import de.evylon.shoppinglist.business.ShoppingListRepository
import de.evylon.shoppinglist.business.ShoppingListRepositoryImpl
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.utils.NetworkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListViewModel : ViewModel() {

    private val shoppingListRepository: ShoppingListRepository = ShoppingListRepositoryImpl()

    var loadingState by mutableStateOf<LoadingState<ShoppingList>>(Loading())

    init {
        viewModelScope.launch {
            shoppingListRepository.shoppingListFlow.collect { result ->
                loadingState = when (result) {
                    is NetworkResult.Success -> Done(result.value)
                    is NetworkResult.Failure -> Error()
                    null -> Loading()
                }
            }
        }
    }

    suspend fun loadList(id: String?) {
        if (id == null) return
        loadingState = Loading()
        shoppingListRepository.loadListById(id)
    }

    fun deleteItem(listId: String, item: Item) {
        CoroutineScope(Dispatchers.IO).launch {
            shoppingListRepository.deleteItem(listId, item)
        }
    }
}
