package org.stratum0.hamsterlist.models

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import io.ktor.serialization.JsonConvertException
import kotlinx.io.IOException
import kotlinx.serialization.Serializable

@Serializable
sealed interface Result<out T> {
    data class Success<T>(val value: T) : Result<T>
    data class Failure(val exception: Exception) : Result<Nothing>

    fun <S> mapSuccess(transform: (T) -> S): Result<S> = when (this) {
        is Failure -> Failure(this.exception)
        is Success<T> -> Success(transform(this.value))
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

data class HamsterListDataException(
    override val cause: Throwable?,
    override val message: String = "Unable to read server response"
) : Exception(message, cause)

data class HamsterListConnectionException(
    override val cause: Throwable?,
    override val message: String = "A problem with the network connection occured",
) : Exception(message, cause)
