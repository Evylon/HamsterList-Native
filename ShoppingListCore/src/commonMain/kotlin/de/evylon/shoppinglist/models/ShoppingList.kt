package de.evylon.shoppinglist.models

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingList(
    val id: String,
    val title: String,
    var items: List<Item>
) {
    override fun toString() = "$title: $items"
}
