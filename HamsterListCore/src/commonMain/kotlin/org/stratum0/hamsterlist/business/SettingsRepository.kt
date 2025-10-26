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
import org.stratum0.hamsterlist.models.KnownHamsterList

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
    val loadedListId = settings.getStringOrNullStateFlow(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        key = SettingsKey.LOADED_LIST_ID.name
    )

    @OptIn(ExperimentalSettingsApi::class)
    val autoLoadLast = settings.getBooleanStateFlow(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        key = SettingsKey.AUTO_LOAD_LAST.name,
        defaultValue = false
    )

    @OptIn(ExperimentalSettingsApi::class)
    val knownHamsterLists: StateFlow<List<KnownHamsterList>> =
        settings
            .getStringOrNullFlow(key = SettingsKey.KNOWN_LISTS.name)
            .map { value ->
                value?.decodeKnownLists().orEmpty()
            }
            .stateIn(
                CoroutineScope(Dispatchers.IO),
                SharingStarted.Eagerly,
                emptyList()
            )

    fun setUsername(newName: String) {
        settings[SettingsKey.USERNAME.name] = newName.trim().takeIf { it.isNotBlank() }
    }

    fun setLoadedListId(listId: String) {
        settings[SettingsKey.LOADED_LIST_ID.name] = listId.trim().takeIf { it.isNotBlank() }
    }

    fun setAutoLoadLast(autoLoadLast: Boolean) {
        settings[SettingsKey.AUTO_LOAD_LAST.name] = autoLoadLast
    }

    fun addKnownList(newList: KnownHamsterList) {
        val updatedList = knownHamsterLists.value.toMutableList()
        updatedList.add(newList)
        updatedList.sortBy { it.listId }
        try {
            updateKnownLists(updatedList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteKnownList(listToDelete: KnownHamsterList) {
        val updatedList = knownHamsterLists.value.toMutableList()
        updatedList.remove(listToDelete)
        updatedList.sortBy { it.listId }
        try {
            updateKnownLists(updatedList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateKnownLists(knownLists: List<KnownHamsterList>) {
        try {
            settings[SettingsKey.KNOWN_LISTS.name] = Json.encodeToString(knownLists)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun String.decodeKnownLists(): List<KnownHamsterList>? {
        return try {
            Json.decodeFromString<List<KnownHamsterList>>(this)
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
                listOf(KnownHamsterList(currentListId, serverHostName))
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
