package de.evylon.shoppinglist.models

import kotlinx.serialization.Serializable

@Serializable
data class SyncRequest(
    val previousSync: SyncedShoppingList,
    val currentState: ShoppingList,
    val includeInResponse: List<AdditionalData> = emptyList(),
    // TODO add categories, orders, deleteCompletions and addCompletions
)
