package de.evylon.shoppinglist.models

import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse(
    val list: SyncedShoppingList,
    // TODO add completions, categories, orders and changes
)
