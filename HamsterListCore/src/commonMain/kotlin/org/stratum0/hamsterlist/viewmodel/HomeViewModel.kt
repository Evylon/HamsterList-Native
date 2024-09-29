package org.stratum0.hamsterlist.viewmodel

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullStateFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.stratum0.hamsterlist.business.SettingsKeys
import org.stratum0.hamsterlist.business.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository,
    private val settings: ObservableSettings
) : BaseViewModel() {

    @OptIn(ExperimentalSettingsApi::class)
    private val currentListId = settings.getStringOrNullStateFlow(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        key = SettingsKeys.CURRENT_LIST_ID.name
    )

    @OptIn(ExperimentalSettingsApi::class)
    private val serverHostName = settings.getStringOrNullStateFlow(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        key = SettingsKeys.SERVER_HOST_NAME.name
    )

    @NativeCoroutinesState
    val uiState: StateFlow<HomeUiState> = combine(
        userRepository.username,
        currentListId,
        serverHostName,
        ::HomeUiState
    ).stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState(userRepository.username.value, currentListId.value, serverHostName.value)
    )

    fun updateSettings(newName: String, listId: String, serverHostName: String) {
        userRepository.setUsername(newName)
        settings[SettingsKeys.CURRENT_LIST_ID.name] = listId
        settings[SettingsKeys.SERVER_HOST_NAME.name] = serverHostName
    }
}
