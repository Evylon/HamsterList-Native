package org.stratum0.hamsterlist.utils

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import io.ktor.serialization.JsonConvertException
import io.ktor.utils.io.CancellationException
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.flow.MutableStateFlow

sealed class FetchState<out S> {
    data class Success<S>(val value: S) : FetchState<S>()
    data object Loading : FetchState<Nothing>()
    data class Failure(val throwable: Throwable) : FetchState<Nothing>()
}

@NativeCoroutines
suspend fun <T> loadCatching(apiCall: suspend () -> T): FetchState<T> {
    return try {
        FetchState.Success(apiCall())
    } catch (e: IOException) {
        FetchState.Failure(e)
    } catch (e: CancellationException) {
        FetchState.Failure(e)
    } catch (e: JsonConvertException) {
        FetchState.Failure(e)
    }
}

@NativeCoroutines
suspend fun <T> MutableStateFlow<FetchState<T>>.loadCatchingAndEmit(apiCall: suspend () -> T) {
    this.emit(FetchState.Loading)
    this.emit(loadCatching(apiCall))
}
