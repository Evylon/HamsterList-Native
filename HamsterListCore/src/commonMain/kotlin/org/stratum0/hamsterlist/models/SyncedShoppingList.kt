package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
data class SyncedShoppingList(
    val id: String = "",
    val title: String = "",
    val token: String = "",
    val changeId: String? = null,
    var items: List<Item> = emptyList()
) {
    fun toShoppingList(): ShoppingList = ShoppingList(
        id = id,
        title = title,
        items = items
    )
}
