package org.stratum0.hamsterlist.network

import kotlinx.serialization.Serializable
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.models.SyncResponse

@Serializable
data class LocalHamsterList(
    val hamsterList: HamsterList,
    val syncResponse: SyncResponse
)
