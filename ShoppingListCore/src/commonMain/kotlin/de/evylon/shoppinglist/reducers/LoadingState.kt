package de.evylon.shoppinglist.reducers

sealed class LoadingState {
    data object Done : LoadingState()
    data object Error : LoadingState()
    data object Loading : LoadingState()
}
