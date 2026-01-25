package org.stratum0.hamsterlist.business

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow
import org.stratum0.hamsterlist.models.CachedHamsterList
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.ShoppingList
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.viewmodel.LoadingState

interface ShoppingListRepository {
    @NativeCoroutinesState
    val syncState: StateFlow<LoadingState>

    @NativeCoroutinesState
    val lastSync: StateFlow<SyncResponse?>

    @NativeCoroutines
    suspend fun loadHamsterList(hamsterList: HamsterList)
    fun deleteItem(hamsterList: HamsterList, currentList: ShoppingList, item: Item): ShoppingList
    fun addItemInput(
        hamsterList: HamsterList,
        currentList: ShoppingList,
        itemInput: String
    ): ShoppingList

    fun addItem(hamsterList: HamsterList, currentList: ShoppingList, item: Item): ShoppingList
    fun addItems(
        hamsterList: HamsterList,
        currentList: ShoppingList,
        items: List<Item>,
        skipQueue: Boolean = false
    ): ShoppingList

    fun changeItem(hamsterList: HamsterList, currentList: ShoppingList, item: Item): ShoppingList

    fun handleSharedItems(
        cachedList: CachedHamsterList,
        items: List<String>
    ): ShoppingList

    fun changeListTitle(
        hamsterList: HamsterList,
        currentList: ShoppingList,
        newTitle: String,
        skipQueue: Boolean = false
    ): ShoppingList

    fun clear()
}
