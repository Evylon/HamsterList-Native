package org.stratum0.hamsterlist.business

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullStateFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.StateFlow

class UserRepository(
    private val settings: ObservableSettings
) {
    @OptIn(ExperimentalSettingsApi::class)
    @NativeCoroutinesState
    val username: StateFlow<String?> = settings.getStringOrNullStateFlow(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        key = SettingsKeys.USERNAME.name
    )

    fun setUsername(newName: String) {
        settings[SettingsKeys.USERNAME.name] = newName.trim().takeIf { it.isNotBlank() }
    }
}