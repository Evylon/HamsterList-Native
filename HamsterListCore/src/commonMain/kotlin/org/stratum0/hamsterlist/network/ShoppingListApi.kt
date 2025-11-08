package org.stratum0.hamsterlist.network

import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.SyncRequest
import org.stratum0.hamsterlist.models.SyncResponse

interface ShoppingListApi {
    suspend fun getSyncedShoppingList(hamsterList: HamsterList): SyncResponse

    suspend fun requestSync(
        hamsterList: HamsterList,
        syncRequest: SyncRequest
    ): SyncResponse
}
