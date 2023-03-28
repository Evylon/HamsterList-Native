package de.evylon.shoppinglist.reducers.shoppinglist

import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList

sealed class ShoppingListAction {
    class FetchList(val listId: String) : ShoppingListAction()
    class UpdateList(val shoppingList: ShoppingList) : ShoppingListAction()
    class DeleteItem(val item: Item) : ShoppingListAction()
}
