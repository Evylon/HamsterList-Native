package de.evylon.shoppinglist.utils

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.MutableStateFlow

sealed class FetchState<out S> {
    data class Success<S>(val value: S) : FetchState<S>()
    data object Loading : FetchState<Nothing>()
    data class Failure(val throwable: Throwable) : FetchState<Nothing>()
}

@NativeCoroutines
@Suppress("TooGenericExceptionCaught")
suspend fun <T> loadCatching(apiCall: suspend () -> T): FetchState<T> {
    return try {
        FetchState.Success(apiCall())
    } catch (e: Exception) {
        FetchState.Failure(e)
    }
}

@NativeCoroutines
suspend fun <T> MutableStateFlow<FetchState<T>>.loadCatchingAndEmit(apiCall: suspend () -> T) {
    this.emit(FetchState.Loading)
    this.emit(loadCatching(apiCall))
}
