package de.evylon.shoppinglist.viewmodel.shoppinglist

import de.evylon.shoppinglist.models.Amount
import de.evylon.shoppinglist.models.CategoryDefinition
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.Order
import de.evylon.shoppinglist.models.SyncedShoppingList
import de.evylon.shoppinglist.viewmodel.LoadingState
import kotlin.random.Random

data class ShoppingListState(
    val shoppingList: SyncedShoppingList,
    val categories: List<CategoryDefinition>,
    val orders: List<Order>,
    val loadingState: LoadingState
) {
    companion object {
        val empty = ShoppingListState(
            shoppingList = SyncedShoppingList(
                id = "",
                title = "",
                token = Random.nextInt().toString(),
                changeId = Random.nextInt().toString(),
                items = emptyList()
            ),
            categories = emptyList(),
            orders = emptyList(),
            loadingState = LoadingState.Loading
        )
        val mock = ShoppingListState(
            shoppingList = SyncedShoppingList(
                id = "Mock",
                title = "MockList",
                token = Random.nextInt().toString(),
                changeId = Random.nextInt().toString(),
                items = mutableListOf(
                    Item(
                        id = "UUID1",
                        name = "Item1",
                        category = "cat1"
                    ),
                    Item(
                        id = "UUID2",
                        name = "Item2",
                        amount = Amount(1.5, "kg"),
                        category = "cat2"
                    ),
                    Item(
                        id = "UUID3",
                        name = "Item3",
                        amount = Amount(0.05, "hPa"),
                        category = null
                    ),
                )
            ),
            orders = listOf(Order("", "SomeOrder", listOf("cat1", "cat2"))),
            categories = listOf(
                CategoryDefinition("", "cat1", "c1", "lime", false),
                CategoryDefinition("", "cat2", "c2", "black", true)
            ),
            loadingState = LoadingState.Done
        )
    }
}
