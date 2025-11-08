package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
data class HamsterList(
    val listId: String,
    val serverHostName: String,
    val title: String? = null,
    val isLocal: Boolean = false
) {
    val titleOrId = title ?: listId
}