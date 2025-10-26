package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
data class KnownHamsterList(
    val listId: String,
    val serverHostName: String
)