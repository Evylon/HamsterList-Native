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
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import org.stratum0.hamsterlist.business.SettingsRepository
import org.stratum0.hamsterlist.models.AdditionalData
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.SyncRequest
import org.stratum0.hamsterlist.models.SyncResponse
import org.stratum0.hamsterlist.utils.isDebug
import org.stratum0.hamsterlist.utils.parseUrlLenient
import kotlin.coroutines.cancellation.CancellationException

internal class ShoppingListApi(
    private val settingsRepository: SettingsRepository
) {
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

    private fun baseUrl(hamsterList: HamsterList): Url {
        val parsedUrl = parseUrlLenient(hamsterList.serverHostName)
            ?: throw IOException("cannot parse url for $hamsterList")

        return with(URLBuilder(parsedUrl)) {
            protocol = URLProtocol.HTTPS
            appendPathSegments("api", hamsterList.listId)
            build()
        }
    }

    @Throws(IOException::class, CancellationException::class, JsonConvertException::class)
    suspend fun getSyncedShoppingList(hamsterList: HamsterList): SyncResponse {
        return httpClient.get(baseUrl(hamsterList)) {
            url {
                appendPathSegments("sync")
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
    suspend fun requestSync(
        hamsterList: HamsterList,
        syncRequest: SyncRequest
    ): SyncResponse {
        return httpClient.post(baseUrl(hamsterList)) {
            url { appendPathSegments("sync") }
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
