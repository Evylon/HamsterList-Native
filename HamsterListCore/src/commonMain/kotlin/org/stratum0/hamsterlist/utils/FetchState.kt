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

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(val throwable: Throwable) : Result<Nothing>()

    fun toFetchState(): FetchState<T> = when (this) {
        is Success -> FetchState.Success(this.value)
        is Failure -> FetchState.Failure(this.throwable)
    }
}

@NativeCoroutines
suspend fun <T> loadCatching(apiCall: suspend () -> T): Result<T> {
    return try {
        Result.Success(apiCall())
    } catch (e: IOException) {
        Result.Failure(HamsterListConnectionException(e))
    } catch (e: JsonConvertException) {
        Result.Failure(HamsterListDataException(e))
    } catch (e: Exception) {
        Result.Failure(e)
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
