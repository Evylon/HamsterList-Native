package org.stratum0.hamsterlist.viewmodel.shoppinglist

import org.stratum0.hamsterlist.models.Amount
import org.stratum0.hamsterlist.models.CategoryDefinition
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.Order
import org.stratum0.hamsterlist.models.SyncedShoppingList
import org.stratum0.hamsterlist.viewmodel.LoadingState
import kotlin.random.Random

data class ShoppingListState(
    val shoppingList: SyncedShoppingList = SyncedShoppingList(
        id = "",
        title = "",
        token = Random.nextInt().toString(),
        changeId = Random.nextInt().toString(),
        items = emptyList()
    ),
    val categories: List<CategoryDefinition> = emptyList(),
    val orders: List<Order> = emptyList(),
    val selectedOrder: Order? = orders.firstOrNull(),
    val loadingState: LoadingState = LoadingState.Loading
) {
    companion object {
        val empty = ShoppingListState()
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
