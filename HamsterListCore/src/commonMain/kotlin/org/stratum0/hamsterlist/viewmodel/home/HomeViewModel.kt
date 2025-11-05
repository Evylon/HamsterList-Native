package org.stratum0.hamsterlist.viewmodel.home

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import org.stratum0.hamsterlist.business.SettingsRepository
import org.stratum0.hamsterlist.models.DialogState
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.utils.parseUrlLenient
import org.stratum0.hamsterlist.viewmodel.BaseViewModel

class HomeViewModel(
    private val settingsRepository: SettingsRepository,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            username = settingsRepository.username.value,
            knownHamsterLists = settingsRepository.knownHamsterLists.value,
            autoLoadLast = settingsRepository.autoLoadLast.value
        )
    )

    @NativeCoroutinesState
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        settingsRepository.migrateToKnownLists()
        subscribeSettings()
    }

    fun handleHomeAction(action: HomeAction) {
        when (action) {
            is HomeAction.UpdateUsername -> updateUsername(username = action.username)
            is HomeAction.UpdateAutoLoadLast -> updateAutoLoadLast(autoLoadLast = action.autoLoadLast)
            is HomeAction.DeleteHamsterList -> deleteKnownList(action.hamsterList)
            is HomeAction.LoadHamsterlist -> loadHamsterList(action = action)

            is HomeAction.OpenListCreationSheet -> updateSheetState(HomeSheetState.ListCreation)
            is HomeAction.OpenShareContentSheet -> {
                updateSheetState(HomeSheetState.ContentSharing(uiState.value.knownHamsterLists))
            }

            is HomeAction.DismissSheet -> updateSheetState(null)

            is HomeAction.OpenDialog -> updateDialogState(action.dialogState)
            is HomeAction.DismissDialog -> updateDialogState(null)
        }
    }

    private fun subscribeSettings() {
        combine(
            settingsRepository.username,
            settingsRepository.knownHamsterLists,
            settingsRepository.autoLoadLast
        ) { username, knownHamsterLists, autoLoadLast ->
            _uiState.update { oldValue ->
                oldValue.copy(
                    username = username,
                    knownHamsterLists = knownHamsterLists,
                    autoLoadLast = autoLoadLast
                )
            }
        }.launchIn(scope)
    }

    private fun loadHamsterList(action: HomeAction.LoadHamsterlist) {
        handleHomeAction(HomeAction.DismissSheet)
        val selectedList = action.selectedList
        val uiState = uiState.value
        if (uiState.username.isNullOrBlank()) {
            updateDialogState(DialogState.UsernameMissing)
        } else if (parseUrlLenient(selectedList.serverHostName) == null) {
            updateDialogState(DialogState.ServerInvalid)
        } else {
            updateSettings(
                username = uiState.username,
                loadedList = selectedList,
                autoLoadLast = uiState.autoLoadLast
            )
            action.navigateToList()
        }
    }

    private fun updateUsername(username: String) {
        _uiState.update { oldValue ->
            oldValue.copy(username = username)
        }
    }

    private fun updateAutoLoadLast(autoLoadLast: Boolean) {
        _uiState.update { oldValue ->
            oldValue.copy(autoLoadLast = autoLoadLast)
        }
    }

    private fun updateSettings(
        username: String,
        loadedList: HamsterList,
        autoLoadLast: Boolean
    ) {
        settingsRepository.setUsername(username)
        settingsRepository.setAutoLoadLast(autoLoadLast)
        if (!settingsRepository.knownHamsterLists.value.contains(loadedList)) {
            settingsRepository.addKnownList(loadedList)
        } else {
            settingsRepository.updateLastLoadedList(loadedList = loadedList)
        }
    }

    private fun deleteKnownList(hamsterList: HamsterList) {
        settingsRepository.deleteKnownList(hamsterList)
    }

    private fun updateSheetState(sheetState: HomeSheetState?) {
        _uiState.update { oldState ->
            oldState.copy(sheetState = sheetState)
        }
    }

    private fun updateDialogState(dialogState: DialogState?) {
        _uiState.update { oldState ->
            oldState.copy(dialogState = dialogState)
        }
    }
}
