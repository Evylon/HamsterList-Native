package org.stratum0.hamsterlist.business

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.utils.FetchState

interface ShoppingListRepository {
    @NativeCoroutinesState
    val syncState: StateFlow<FetchState<SyncResponse>>

    val sharedItems: StateFlow<List<String>?>

    @NativeCoroutines
    suspend fun loadHamsterList(hamsterList: HamsterList)

    @NativeCoroutines
    suspend fun deleteItem(hamsterList: HamsterList, item: Item)

    @NativeCoroutines
    suspend fun addItem(hamsterList: HamsterList, item: Item)

    @NativeCoroutines
    suspend fun addItems(hamsterList: HamsterList, items: List<Item>)

    @NativeCoroutines
    suspend fun handleSharedItems(hamsterList: HamsterList, items: List<Item>)

    fun enqueueSharedContent(content: String)

    @NativeCoroutines
    suspend fun changeItem(hamsterList: HamsterList, item: Item)

    fun clear()
}
