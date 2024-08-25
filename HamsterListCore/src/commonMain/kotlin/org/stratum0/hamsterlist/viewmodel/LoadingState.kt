package org.stratum0.hamsterlist.viewmodel

sealed class LoadingState {
    data object Done : LoadingState()
    data object Error : LoadingState()
    data object Loading : LoadingState()
}
