package de.evylon.shoppinglist.android.gui.utils

sealed class LoadingState<T> {
    class Error<T> : LoadingState<T>()
    class Loading<T> : LoadingState<T>()
    class Done<T>(val value: T) : LoadingState<T>()
}
