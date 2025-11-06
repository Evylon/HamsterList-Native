package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
data class CachedHamsterList(
    val hamsterList: HamsterList,
    val syncResponse: SyncResponse
)
