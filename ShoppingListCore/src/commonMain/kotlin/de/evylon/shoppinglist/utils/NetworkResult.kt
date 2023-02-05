package de.evylon.shoppinglist.utils

sealed class NetworkResult<S> {
    class Success<S>(val value: S) : NetworkResult<S>()
    class Failure<S>(val throwable: Throwable) : NetworkResult<S>()
}