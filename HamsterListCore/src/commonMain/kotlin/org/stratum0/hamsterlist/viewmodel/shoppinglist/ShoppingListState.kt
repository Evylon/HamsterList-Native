package org.stratum0.hamsterlist.viewmodel.shoppinglist

import org.stratum0.hamsterlist.models.Amount
import org.stratum0.hamsterlist.models.CategoryDefinition
import org.stratum0.hamsterlist.models.CompletionItem
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
    val completions: List<CompletionItem> = emptyList(),
    val selectedOrder: Order? = orders.firstOrNull(),
    val addItemInput: String = "",
    val loadingState: LoadingState = LoadingState.Loading,
) {
    val completionChooserState: CompletionChooserState = CompletionChooserState(addItemInput, completions, categories)

    companion object {
        // used on iOS
        val empty = ShoppingListState()
        val mock = ShoppingListState(
            shoppingList = SyncedShoppingList(
                id = "Mock",
                title = "MockList",
                token = Random.nextInt().toString(),
                changeId = Random.nextInt().toString(),
                items = List(15) {
                    Item(
                        id = "UUID$it",
                        name = "Item$it",
                        category = "cat${it % 2}"
                    )
                }
            ),
            orders = listOf(Order("", "SomeOrder", listOf("cat1", "cat2"))),
            completions = listOf(
                CompletionItem.mockCompletionWithCategory,
                CompletionItem.mockCompletionWithoutCategory
            ),
            categories = listOf(
                CategoryDefinition.mockLight,
                CategoryDefinition.mockDark
            ),
            loadingState = LoadingState.Done
        )
    }
}
