package org.stratum0.hamsterlist.viewmodel.shoppinglist

import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.Order

sealed interface ShoppingListAction {
    data class AddItem(val input: String) : ShoppingListAction
    data class AddItemByCompletion(val completionItem: Item) : ShoppingListAction
    data class ChangeItem(val oldItem: Item, val newItem: String) : ShoppingListAction
    data class ChangeCategoryForItem(val item: Item, val newCategoryId: String) : ShoppingListAction
    data class DeleteItem(val item: Item) : ShoppingListAction
    object FetchList : ShoppingListAction
    data class SelectOrder(val order: Order) : ShoppingListAction
    data class UpdateAddItemInput(val input: String) : ShoppingListAction
}