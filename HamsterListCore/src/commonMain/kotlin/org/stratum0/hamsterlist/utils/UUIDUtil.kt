package org.stratum0.hamsterlist.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

expect fun randomUUID(): String

@OptIn(ExperimentalUuidApi::class)
fun String.isUUID(): Boolean = try {
    Uuid.parse(this)
    true
} catch (e: IllegalArgumentException) {
    false
}
