package de.evylon.shoppinglist.business

import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.network.ShoppingListApi
import de.evylon.shoppinglist.utils.NetworkResult
import io.ktor.serialization.JsonConvertException
import io.ktor.utils.io.CancellationException
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShoppingListRepositoryImpl : ShoppingListRepository {

    private val shoppingListApi = ShoppingListApi()

    // Flows

    private val _shoppingListFlow = MutableStateFlow<NetworkResult<ShoppingList>?>(null)
    override val shoppingListFlow = _shoppingListFlow.asStateFlow()

    // Service Calls

    override suspend fun loadListById(id: String) {
        try {
            val shoppingList = shoppingListApi.getShoppingListById(id)
            _shoppingListFlow.emit(
                NetworkResult.Success(shoppingList)
            )
        } catch (e: IOException) {
            _shoppingListFlow.emit(NetworkResult.Failure(e))
        } catch (e: CancellationException) {
            _shoppingListFlow.emit(NetworkResult.Failure(e))
        } catch (e: JsonConvertException) {
            _shoppingListFlow.emit(NetworkResult.Failure(e))
        }
    }

    override suspend fun deleteItem(listId: String, item: Item) {
        try {
            val list = (_shoppingListFlow.value as? NetworkResult.Success)?.value
            if (list == null || list.id != listId) return // TODO
            val updatedList = shoppingListApi.deleteItem(list, item)
            _shoppingListFlow.emit(NetworkResult.Success(updatedList))
        } catch (e: IOException) {
            _shoppingListFlow.emit(NetworkResult.Failure(e))
        } catch (e: CancellationException) {
            _shoppingListFlow.emit(NetworkResult.Failure(e))
        } catch (e: JsonConvertException) {
            _shoppingListFlow.emit(NetworkResult.Failure(e))
        }
    }
}
