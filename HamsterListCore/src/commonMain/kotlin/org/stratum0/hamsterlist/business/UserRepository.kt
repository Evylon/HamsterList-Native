package org.stratum0.hamsterlist.business

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserRepository {
    private val _username = MutableStateFlow<String?>(null)

    @NativeCoroutinesState
    val username: StateFlow<String?> = _username.asStateFlow()

    fun setUsername(newName: String) {
        _username.update {
            newName.trim().takeIf { it.isNotBlank() }
        }
    }
}