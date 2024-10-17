package org.stratum0.hamsterlist.viewmodel.home

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanOrNullStateFlow
import com.russhwolf.settings.coroutines.getStringOrNullStateFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.stratum0.hamsterlist.business.SettingsKey
import org.stratum0.hamsterlist.business.UserRepository
import org.stratum0.hamsterlist.viewmodel.BaseViewModel

class HomeViewModel(
    private val userRepository: UserRepository,
    private val settings: ObservableSettings
) : BaseViewModel() {

    @OptIn(ExperimentalSettingsApi::class)
    private val currentListId = settings.getStringOrNullStateFlow(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        key = SettingsKey.CURRENT_LIST_ID.name
    )

    @OptIn(ExperimentalSettingsApi::class)
    private val serverHostName = settings.getStringOrNullStateFlow(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        key = SettingsKey.SERVER_HOST_NAME.name
    )

    @OptIn(ExperimentalSettingsApi::class)
    private val autoLoadLast = settings.getBooleanOrNullStateFlow(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        key = SettingsKey.AUTO_LOAD_LAST.name
    )

    @NativeCoroutinesState
    val uiState: StateFlow<HomeUiState> = combine(
        userRepository.username,
        currentListId,
        serverHostName,
        autoLoadLast,
        ::HomeUiState
    ).stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState(
            userRepository.username.value,
            currentListId.value,
            serverHostName.value,
            autoLoadLast.value
        )
    )

    fun updateSettings(newName: String, listId: String, serverHostName: String, autoLoadLast: Boolean) {
        userRepository.setUsername(newName)
        settings[SettingsKey.CURRENT_LIST_ID.name] = listId
        settings[SettingsKey.SERVER_HOST_NAME.name] = serverHostName
        settings[SettingsKey.AUTO_LOAD_LAST.name] = autoLoadLast
    }
}
