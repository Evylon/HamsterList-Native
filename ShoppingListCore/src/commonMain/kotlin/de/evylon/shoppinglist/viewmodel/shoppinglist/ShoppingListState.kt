package de.evylon.shoppinglist.viewmodel.shoppinglist

import de.evylon.shoppinglist.models.Amount
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.SyncedShoppingList
import de.evylon.shoppinglist.viewmodel.LoadingState
import kotlin.random.Random

data class ShoppingListState(
    val shoppingList: SyncedShoppingList,
    val loadingState: LoadingState
) {
    companion object {
        val empty = ShoppingListState(
            shoppingList = SyncedShoppingList(
                id = "",
                title = "",
                token = Random.nextInt().toString(),
                changeId = Random.nextInt().toString(),
                items = listOf()
            ),
            loadingState = LoadingState.Loading
        )
        val mock = ShoppingListState(
            shoppingList = SyncedShoppingList(
                id = "Mock",
                title = "MockList",
                token = Random.nextInt().toString(),
                changeId = Random.nextInt().toString(),
                items = mutableListOf(
                    Item.Data(
                        id = "UUID1",
                        name = "Item1"
                    ),
                    Item.Data(
                        id = "UUID2",
                        name = "Item2",
                        amount = Amount(1.5, "kg")
                    ),
                    Item.Data(
                        id = "UUID3",
                        name = "Item3",
                        amount = Amount(0.05, "hPa")
                    ),
                )
            ),
            loadingState = LoadingState.Done
        )
    }
}
