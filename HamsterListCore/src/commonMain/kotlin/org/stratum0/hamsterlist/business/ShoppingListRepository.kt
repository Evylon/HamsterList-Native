package org.stratum0.hamsterlist.business

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.utils.FetchState

interface ShoppingListRepository {
    @NativeCoroutinesState
    val syncState: StateFlow<FetchState<SyncResponse>>

    val sharedItems: StateFlow<List<String>?>

    @NativeCoroutines
    suspend fun loadListById(id: String)

    @NativeCoroutines
    suspend fun deleteItem(listId: String, item: Item)

    @NativeCoroutines
    suspend fun addItem(listId: String, item: Item)

    @NativeCoroutines
    suspend fun handleSharedItems(listId: String, items: List<Item>)

    fun enqueueSharedContent(content: String)

    @NativeCoroutines
    suspend fun changeItem(listId: String, item: Item)

    fun clear()
}
