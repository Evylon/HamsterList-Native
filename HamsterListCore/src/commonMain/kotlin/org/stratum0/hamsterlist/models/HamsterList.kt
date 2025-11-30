package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable
import org.stratum0.hamsterlist.utils.randomUUID

@Serializable
data class HamsterList(
    val listId: String,
    val serverHostName: String,
    val title: String? = null,
    val isLocal: Boolean = false
) {
    constructor(
        serverHostName: String,
        title: String,
        isLocal: Boolean = false
    ) : this(
        listId = randomUUID(),
        serverHostName = serverHostName,
        title = title,
        isLocal = isLocal
    )
    val titleOrId = title ?: listId
}