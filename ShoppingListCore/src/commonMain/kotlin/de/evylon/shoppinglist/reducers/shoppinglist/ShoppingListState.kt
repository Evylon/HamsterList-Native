package de.evylon.shoppinglist.reducers.shoppinglist

import de.evylon.shoppinglist.models.Amount
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.reducers.LoadingState

data class ShoppingListState(
    val shoppingList: ShoppingList,
    val loadingState: LoadingState
) {
    companion object {
        val inital = ShoppingListState(
            shoppingList = ShoppingList(
                id = "",
                title = "",
                items = listOf()
            ),
            loadingState = LoadingState.Loading
        )
        val mock = ShoppingListState(
            shoppingList = ShoppingList(
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
            ),
            loadingState = LoadingState.Done
        )
    }
}
