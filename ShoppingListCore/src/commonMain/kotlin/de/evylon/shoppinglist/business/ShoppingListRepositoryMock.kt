package de.evylon.shoppinglist.business

import de.evylon.shoppinglist.models.Amount
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Suppress("MagicNumber")
class ShoppingListRepositoryMock : ShoppingListRepository {

    private val mockFlow = MutableStateFlow(
        NetworkResult.Success(
            ShoppingList(
                id = "Mock",
                title = "MockList",
                items = mutableListOf(
                    Item(
                        id = "UUID1",
                        name = "Item1"
                    ),
                    Item(
                        id = "UUID2",
                        name = "Item2",
                        amount = Amount(1.5f, "kg")
                    ),
                    Item(
                        id = "UUID3",
                        name = "Item3",
                        amount = Amount(0.05f, "hPa")
                    ),
                )
            )
        )
    )
    override val shoppingListFlow: StateFlow<NetworkResult<ShoppingList>?>
        get() = mockFlow.asStateFlow()

    override suspend fun loadListById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(listId: String, item: Item) {
        TODO("Not yet implemented")
    }
}