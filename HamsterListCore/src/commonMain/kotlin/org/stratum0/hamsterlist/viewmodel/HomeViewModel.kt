package org.stratum0.hamsterlist.viewmodel

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.stratum0.hamsterlist.business.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {
    @NativeCoroutinesState
    val uiState: StateFlow<HomeUiState> = userRepository.username
        .map { HomeUiState(it) }
        .stateIn(scope, SharingStarted.Eagerly, HomeUiState())

    fun setUsername(newName: String) {
        userRepository.setUsername(newName)
    }
}

data class HomeUiState(
    val username: String? = null,
)
