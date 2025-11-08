package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
data class SyncRequest(
    val previousSync: SyncedShoppingList,
    val currentState: ShoppingList,
    val includeInResponse: List<AdditionalData> = emptyList(),
    // TODO add categories, orders, deleteCompletions and addCompletions
) {
    constructor(
        previousSync: SyncResponse,
        updatedList: ShoppingList
    ) : this(
        previousSync = previousSync.list,
        currentState = updatedList,
        includeInResponse = listOf(
            AdditionalData.orders,
            AdditionalData.categories,
            AdditionalData.completions
        )
    )

    constructor(cachedState: CachedHamsterList) : this(
        previousSync = cachedState.lastSyncState.list,
        currentState = cachedState.currentList,
        includeInResponse = listOf(
            AdditionalData.orders,
            AdditionalData.categories,
            AdditionalData.completions
        )
    )
}
