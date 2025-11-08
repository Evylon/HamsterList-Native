package org.stratum0.hamsterlist.utils

import kotlinx.serialization.json.Json

inline fun <reified T> String.decode(): T? {
    return try {
        Json.decodeFromString<T>(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
