package org.stratum0.hamsterlist.business

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanStateFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.coroutines.getStringOrNullStateFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.stratum0.hamsterlist.models.CachedHamsterList
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.ShoppingList
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.utils.decode

class SettingsRepository(
    private val settings: ObservableSettings
) {
    @OptIn(ExperimentalSettingsApi::class)
    @NativeCoroutinesState
    val username: StateFlow<String?> = settings.getStringOrNullStateFlow(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        key = SettingsKey.USERNAME.name
    )

    @OptIn(ExperimentalSettingsApi::class)
    val autoLoadLast = settings.getBooleanStateFlow(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        key = SettingsKey.AUTO_LOAD_LAST.name,
        defaultValue = false
    )

    /**
     * Currently known list of [HamsterList] loaded by the user.
     * The list is always sorted anti-chronologically by the last time a list was loaded.
     */
    @OptIn(ExperimentalSettingsApi::class)
    val knownHamsterLists: StateFlow<List<HamsterList>> =
        settings
            .getStringOrNullFlow(key = SettingsKey.KNOWN_LISTS.name)
            .map { value ->
                value?.decode<List<HamsterList>>().orEmpty()
            }
            .stateIn(
                CoroutineScope(Dispatchers.IO),
                SharingStarted.Eagerly,
                getKnownLists()
            )

    fun setUsername(newName: String) {
        settings[SettingsKey.USERNAME.name] = newName.trim().takeIf { it.isNotBlank() }
    }

    fun setAutoLoadLast(autoLoadLast: Boolean) {
        settings[SettingsKey.AUTO_LOAD_LAST.name] = autoLoadLast
    }

    fun getKnownLists(): List<HamsterList> =
        settings
            .getStringOrNull(key = SettingsKey.KNOWN_LISTS.name)
            ?.decode<List<HamsterList>>()
            .orEmpty()

    fun addKnownList(newList: HamsterList) {
        val updatedList = knownHamsterLists.value.toMutableList()
        updatedList.add(0, newList)
        updateKnownLists(updatedList)
    }

    fun deleteKnownList(listToDelete: HamsterList) {
        val updatedList = knownHamsterLists.value.filterNot { it == listToDelete }
        updateKnownLists(updatedList)
        removeCachedList(listToDelete)
    }

    fun updateLastLoadedList(loadedList: HamsterList) {
        val oldList = knownHamsterLists.value.toMutableList()
        val updatedList = oldList.filterNot { it == loadedList }.toMutableList()
        updatedList.add(0, loadedList)
        updateKnownLists(updatedList)
    }

    fun getCachedLists(): List<CachedHamsterList> =
        settings
            .getStringOrNull(key = SettingsKey.CACHED_LISTS.name)
            ?.decode<List<CachedHamsterList>>()
            .orEmpty()

    fun updateCachedSync(hamsterList: HamsterList, newSyncResponse: SyncResponse) {
        val updatedCache = CachedHamsterList(
            hamsterList = hamsterList,
            lastSyncState = newSyncResponse,
            currentList = newSyncResponse.list.toShoppingList()
        )
        val cachedLists = getCachedLists().toMutableList()
        val updatedCachedLists = if (cachedLists.none { it.hamsterList == hamsterList }) {
            cachedLists.apply { add(updatedCache) }
        } else {
            cachedLists.map { cachedList ->
                if (cachedList.hamsterList == hamsterList) {
                    updatedCache
                } else {
                    cachedList
                }
            }
        }
        updateCache(updatedCachedLists)
    }

    /**
     * Update existing or insert new cached list into Settings.
     */
    fun updateCachedList(hamsterList: HamsterList, updatedList: ShoppingList) {
        val cachedLists = getCachedLists().toMutableList()
        if (cachedLists.none { it.hamsterList == hamsterList }) return
        val updatedCachedLists = cachedLists.map { cachedList ->
            if (cachedList.hamsterList == hamsterList) {
                cachedList.copy(currentList = updatedList)
            } else {
                cachedList
            }
        }
        updateCache(updatedCachedLists)
    }

    private fun removeCachedList(hamsterList: HamsterList) {
        updateCache(getCachedLists()
            .filterNot { it.hamsterList == hamsterList }
        )
    }

    private fun updateCache(updatedCachedLists: List<CachedHamsterList>) {
        try {
            settings[SettingsKey.CACHED_LISTS.name] = Json.encodeToString(updatedCachedLists)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateKnownLists(knownLists: List<HamsterList>) {
        try {
            val knownListsUnique = knownLists.toSet().toList()
            settings[SettingsKey.KNOWN_LISTS.name] = Json.encodeToString(knownListsUnique)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Migrate old app states to KNOWN_LISTS format.
     */
    fun migrateToKnownLists() {
        val currentListId = settings.getStringOrNull(SettingsKey.CURRENT_LIST_ID.name)
        val serverHostName = settings.getStringOrNull(SettingsKey.SERVER_HOST_NAME.name)
        val knownHamsterLists = knownHamsterLists.value
        // Check if migration is necessary
        if (currentListId.isNullOrBlank() && serverHostName.isNullOrBlank()) {
            // no old data present
            return
        }
        if (currentListId.isNullOrBlank() || serverHostName.isNullOrBlank()) {
            // only partial data present, discard
            discardLegacyData()
            return
        }
        // only migrate if we do not overwrite existing data
        if (knownHamsterLists.isEmpty()) {
            updateKnownLists(
                listOf(HamsterList(currentListId, serverHostName))
            )
        }
        // finally discard
        discardLegacyData()
    }

    private fun discardLegacyData() {
        settings[SettingsKey.CURRENT_LIST_ID.name] = null
        settings[SettingsKey.SERVER_HOST_NAME.name] = null
    }
}
