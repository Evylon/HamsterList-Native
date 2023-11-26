package de.evylon.shoppinglist

import de.evylon.shoppinglist.business.ShoppingListRepository
import de.evylon.shoppinglist.reducers.shoppinglist.ShoppingListReducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ShoppingListReducerIos {
    val proxy: ShoppingListReducer = ShoppingListReducer(
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        shoppingListRepository = ShoppingListRepository.instance
    )
}
