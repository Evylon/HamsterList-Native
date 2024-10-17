package org.stratum0.hamsterlist.network

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullStateFlow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import org.stratum0.hamsterlist.business.SettingsKey
import org.stratum0.hamsterlist.business.UserRepository
import org.stratum0.hamsterlist.models.AdditionalData
import org.stratum0.hamsterlist.models.SyncRequest
import org.stratum0.hamsterlist.models.SyncResponse
import kotlin.coroutines.cancellation.CancellationException

internal class ShoppingListApi(
    private val userRepository: UserRepository,
    private val settings: ObservableSettings
) {
    private val baseUrl
        get() = "https://${serverHostName.value.orEmpty()}/api"

    @OptIn(ExperimentalSettingsApi::class)
    private val serverHostName = settings.getStringOrNullStateFlow(
        coroutineScope = CoroutineScope(Dispatchers.IO),
        key = SettingsKey.SERVER_HOST_NAME.name
    )

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    @Throws(IOException::class, CancellationException::class, JsonConvertException::class)
    suspend fun getSyncedShoppingList(listId: String): SyncResponse {
        return httpClient.get(baseUrl) {
            url {
                appendPathSegments(listId, "sync")
                parameters.append("includeInResponse", AdditionalData.orders.toString())
                parameters.append("includeInResponse", AdditionalData.categories.toString())
                parameters.append("includeInResponse", AdditionalData.completions.toString())
            }
            userRepository.username.value?.let { username ->
                headers {
                    append("X-ShoppingList-Username", username)
                }
            }
        }.body()
    }

    @Throws(IOException::class, CancellationException::class, JsonConvertException::class)
    suspend fun requestSync(listId: String, syncRequest: SyncRequest): SyncResponse {
        return httpClient.post(baseUrl) {
            url { appendPathSegments(listId, "sync") }
            contentType(ContentType.Application.Json)
            setBody(syncRequest)
            userRepository.username.value?.let { username ->
                headers {
                    append("X-ShoppingList-Username", username)
                }
            }
        }.body()
    }
}
