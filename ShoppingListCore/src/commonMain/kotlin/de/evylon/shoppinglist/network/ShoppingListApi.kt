package de.evylon.shoppinglist.network

import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

internal class ShoppingListApi {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
                filter { request ->
                    request.url.host.contains("ktor.io")
                }
            }
        }
    }

    @Throws(IOException::class, CancellationException::class, JsonConvertException::class)
    suspend fun getShoppingListById(id: String): ShoppingList {
        return httpClient.get("https://list.tilman.ninja/api/$id").body()
    }

    @Throws(IOException::class, CancellationException::class, JsonConvertException::class)
    suspend fun deleteItem(list: ShoppingList, item: Item): ShoppingList {
        delay(1.seconds)
        // TODO use listId, currently only MOCK
        return list.copy(
            id = list.id,
            title = list.title,
            items = list.items.filter { it != item }
        )
    }
}
