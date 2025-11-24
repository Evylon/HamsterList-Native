package org.stratum0.hamsterlist.network

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.set
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.stratum0.hamsterlist.business.SettingsKey
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.HamsterListDataException
import org.stratum0.hamsterlist.models.SyncRequest
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.models.SyncedShoppingList
import org.stratum0.hamsterlist.utils.decode

class LocalShoppingListApi(
    private val settings: ObservableSettings
) : ShoppingListApi {
    @Throws(HamsterListDataException::class)
    override suspend fun getSyncedShoppingList(hamsterList: HamsterList): SyncResponse {
        return getLocalList(hamsterList).syncResponse
    }

    @Throws(HamsterListDataException::class)
    override suspend fun requestSync(
        hamsterList: HamsterList,
        syncRequest: SyncRequest
    ): SyncResponse {
        val currentList = getLocalList(hamsterList)
        val updatedList = currentList.copy(
            syncResponse = SyncResponse(
                list = syncRequest.previousSync.copy(
                    id = syncRequest.currentState.id,
                    title = syncRequest.currentState.title,
                    items = syncRequest.currentState.items
                ),
                orders = currentList.syncResponse.orders,
                categories = currentList.syncResponse.categories,
                completions = currentList.syncResponse.completions,
            )
        )
        writeLocalList(updatedList)
        return updatedList.syncResponse
    }

    private fun getLocalList(hamsterList: HamsterList): LocalHamsterList {
        val localList = settings
            .getStringOrNull(key = SettingsKey.LOCAL_LISTS.name)
            ?.decode<List<LocalHamsterList>>()
            ?.find { it.hamsterList == hamsterList }
        return if (localList != null) {
            localList
        } else {
            val newList = createNewLocalList(hamsterList)
            writeLocalList(newList)
            newList
        }
    }

    private fun createNewLocalList(hamsterList: HamsterList) = LocalHamsterList(
        hamsterList = hamsterList,
        syncResponse = SyncResponse(
            list = SyncedShoppingList(
                id = hamsterList.listId,
                title = hamsterList.titleOrId,
            ),
            orders = emptyList(),
            categories = LocalHamsterList.defaultCategories,
            completions = emptyList()
        )
    )


    private fun writeLocalList(updatedList: LocalHamsterList) {
        val localLists = settings
            .getStringOrNull(key = SettingsKey.LOCAL_LISTS.name)
            ?.decode<List<LocalHamsterList>>()
            .orEmpty()
        val updatedLists = if (localLists.any { it.hamsterList == updatedList.hamsterList }) {
            localLists.map {
                if (it.hamsterList == updatedList.hamsterList) {
                    updatedList
                } else {
                    it
                }
            }
        } else {
            localLists.toMutableList().apply { add(0, updatedList) }
        }
        return try {
            settings[SettingsKey.LOCAL_LISTS.name] = Json.encodeToString(updatedLists)
        } catch (e: Exception) {
            e.printStackTrace()
            throw HamsterListDataException(e, "Unable to write HamsterList to Storage")
        }
    }
}

