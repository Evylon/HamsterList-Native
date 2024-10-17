package org.stratum0.hamsterlist.viewmodel.home

data class HomeUiState(
    val username: String? = null,
    val currentListId: String? = null,
    val serverHostName: String? = null,
    val autoLoadLast: Boolean? = false
)
