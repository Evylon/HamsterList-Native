package org.stratum0.hamsterlist.business

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SharedContentManager {
    private val _sharedItemsFlow = MutableStateFlow<List<String>?>(null)
    @NativeCoroutinesState
    val sharedItems: StateFlow<List<String>?> = _sharedItemsFlow.asStateFlow()

    fun enqueueSharedContent(content: String) {
        _sharedItemsFlow.update {
            content.split("\n")
        }
    }

    fun clearSharedItems() {
        _sharedItemsFlow.update { null }
    }
}