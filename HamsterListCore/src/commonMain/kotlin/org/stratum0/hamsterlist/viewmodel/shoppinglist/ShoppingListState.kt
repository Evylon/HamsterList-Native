package org.stratum0.hamsterlist.viewmodel.shoppinglist

import org.stratum0.hamsterlist.models.CategoryDefinition
import org.stratum0.hamsterlist.models.CompletionItem
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.Order
import org.stratum0.hamsterlist.models.ShoppingList
import org.stratum0.hamsterlist.viewmodel.LoadingState

data class ShoppingListState(
    val shoppingList: ShoppingList = ShoppingList(
        id = "",
        title = "",
        items = emptyList()
    ),
    val categories: List<CategoryDefinition> = emptyList(),
    val orders: List<Order> = emptyList(),
    val completions: List<CompletionItem> = emptyList(),
    val selectedOrder: Order? = orders.firstOrNull(),
    val addItemInput: String = "",
    val loadingState: LoadingState = LoadingState.Loading,
    val categoryChooserItem: Item? = null,
    val isLocalList: Boolean = false
) {
    val completionChooserState: CompletionChooserState =
        CompletionChooserState(addItemInput, completions, categories)
    val categoryChooserState: CategoryChooserState? = categoryChooserItem?.let { selectedItem ->
        CategoryChooserState(selectedItem, categories)
    }

    companion object {
        // used on iOS
        val empty = ShoppingListState()
        val mock = ShoppingListState(
            shoppingList = ShoppingList(
                id = "Mock",
                title = "MockList",
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
