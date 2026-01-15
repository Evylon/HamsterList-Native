package org.stratum0.hamsterlist.network

import io.ktor.serialization.JsonConvertException
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.SyncRequest
import org.stratum0.hamsterlist.models.SyncResponse

interface ShoppingListApi {
    @Throws(IOException::class, CancellationException::class, JsonConvertException::class)
    suspend fun getSyncedShoppingList(hamsterList: HamsterList): SyncResponse

    @Throws(IOException::class, CancellationException::class, JsonConvertException::class)
    suspend fun requestSync(
        hamsterList: HamsterList,
        syncRequest: SyncRequest
    ): SyncResponse
}
