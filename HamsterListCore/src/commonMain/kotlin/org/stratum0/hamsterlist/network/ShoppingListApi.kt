package org.stratum0.hamsterlist.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import org.stratum0.hamsterlist.business.SettingsRepository
import org.stratum0.hamsterlist.models.AdditionalData
import org.stratum0.hamsterlist.models.SyncRequest
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.utils.isDebug
import kotlin.coroutines.cancellation.CancellationException

internal class ShoppingListApi(
    private val settingsRepository: SettingsRepository
) {
    private val baseUrl
        get() = "https://${serverHostName.value.orEmpty()}/api"

    // TODO create a new API instance for each server, to support background sync with multiple servers
    private val serverHostName = combine(
        settingsRepository.loadedListId,
        settingsRepository.knownHamsterLists
    ) { loadedListId, knownHamsterLists ->
        knownHamsterLists.find { it.listId == loadedListId }?.serverHostName
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = null
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
        if (isDebug) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
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
            settingsRepository.username.value?.let { username ->
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
            settingsRepository.username.value?.let { username ->
                headers {
                    append("X-ShoppingList-Username", username)
                }
            }
        }.body()
    }
}
