package org.stratum0.hamsterlist.viewmodel

sealed class LoadingState {
    data object Done : LoadingState()
    data class Error(val throwable: Throwable) : LoadingState()
    data object Loading : LoadingState()
}
