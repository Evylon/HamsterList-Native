package org.stratum0.hamsterlist.network

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import io.ktor.serialization.JsonConvertException
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.SyncRequest
import org.stratum0.hamsterlist.models.SyncResponse

interface ShoppingListApi {
    @NativeCoroutines
    @Throws(IOException::class, CancellationException::class, JsonConvertException::class)
    suspend fun getSyncedShoppingList(hamsterList: HamsterList): SyncResponse

    @NativeCoroutines
    @Throws(IOException::class, CancellationException::class, JsonConvertException::class)
    suspend fun requestSync(
        hamsterList: HamsterList,
        syncRequest: SyncRequest
    ): SyncResponse
}
