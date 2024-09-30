package org.stratum0.hamsterlist.models

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class SyncedShoppingList(
    val id: String = "",
    val title: String = "",
    val token: String = "",
    val changeId: String? = null,
    @EncodeDefault
    var items: List<Item> = emptyList()
) {
    fun toShoppingList(): ShoppingList = ShoppingList(
        id = id,
        title = title,
        items = items
    )
}
