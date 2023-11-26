package de.evylon.shoppinglist.reducers

sealed class LoadingState {
    object Done : LoadingState()
    object Error : LoadingState()
    object Loading : LoadingState()
}
