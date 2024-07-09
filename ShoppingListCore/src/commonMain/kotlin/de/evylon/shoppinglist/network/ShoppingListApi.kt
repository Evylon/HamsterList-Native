package de.evylon.shoppinglist.network

import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.models.SyncRequest
import de.evylon.shoppinglist.models.SyncResponse
import de.evylon.shoppinglist.models.SyncedShoppingList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

internal class ShoppingListApi {
    private val baseUrl = "https://list.tilman.ninja/api"
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
    suspend fun getShoppingListById(id: String): ShoppingList {
        return httpClient.get("$baseUrl/$id").body()
    }

    @Throws(IOException::class, CancellationException::class, JsonConvertException::class)
    suspend fun getSyncedShoppingList(listId: String): SyncedShoppingList {
        // TODO add optional query parameter includeInResponse
        return httpClient.get("$baseUrl/$listId/sync").body()
    }

    @Throws(IOException::class, CancellationException::class, JsonConvertException::class)
    suspend fun requestSync(listId: String, syncRequest: SyncRequest): SyncedShoppingList {
        return httpClient.post("$baseUrl/$listId/sync") {
            contentType(ContentType.Application.Json)
            setBody(syncRequest)
        }.body()
    }
}
