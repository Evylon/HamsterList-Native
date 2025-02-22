package org.stratum0.hamsterlist.utils

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.io.IOException

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
        FetchState.Failure(HamsterListConnectionException(e))
    } catch (e: JsonConvertException) {
        FetchState.Failure(HamsterListDataException(e))
    } catch (e: Exception) {
        FetchState.Failure(e)
    }
}

@NativeCoroutines
suspend fun <T> MutableStateFlow<FetchState<T>>.loadCatchingAndEmit(apiCall: suspend () -> T) {
    this.emit(FetchState.Loading)
    this.emit(loadCatching(apiCall))
}

data class HamsterListDataException(
    override val cause: Throwable?,
    override val message: String = "Unable to read server response"
) : Exception(message, cause)

data class HamsterListConnectionException(
    override val cause: Throwable?,
    override val message: String = "A problem with the network connection occured",
) : Exception(message, cause)
