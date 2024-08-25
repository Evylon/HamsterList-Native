package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse(
    val list: SyncedShoppingList,
    val orders: List<Order>,
    val categories: List<CategoryDefinition>
    // TODO add completions and changes
)
