package org.stratum0.hamsterlist.viewmodel.home

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.stratum0.hamsterlist.business.SettingsRepository
import org.stratum0.hamsterlist.models.KnownHamsterList
import org.stratum0.hamsterlist.viewmodel.BaseViewModel

class HomeViewModel(
    private val settingsRepository: SettingsRepository,
) : BaseViewModel() {

    init {
        settingsRepository.migrateToKnownLists()
    }

    @NativeCoroutinesState
    val uiState: StateFlow<HomeUiState> = combine(
        settingsRepository.username,
        settingsRepository.knownHamsterLists,
        settingsRepository.loadedListId,
        settingsRepository.autoLoadLast,
        ::HomeUiState
    ).stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState(
            settingsRepository.username.value,
            settingsRepository.knownHamsterLists.value,
            settingsRepository.loadedListId.value,
            settingsRepository.autoLoadLast.value
        )
    )

    fun updateSettings(
        newName: String,
        loadedList: KnownHamsterList,
        autoLoadLast: Boolean
    ) {
        settingsRepository.setUsername(newName)
        settingsRepository.setAutoLoadLast(autoLoadLast)
        settingsRepository.setLoadedListId(loadedList.listId)
        if (!settingsRepository.knownHamsterLists.value.contains(loadedList)) {
            settingsRepository.addKnownList(loadedList)
        }
    }

    fun deleteKnownList(knownHamsterList: KnownHamsterList) {
        settingsRepository.deleteKnownList(knownHamsterList)
    }
}
