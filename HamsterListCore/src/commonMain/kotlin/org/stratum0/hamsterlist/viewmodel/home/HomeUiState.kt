package org.stratum0.hamsterlist.viewmodel.home

import org.stratum0.hamsterlist.models.DialogState
import org.stratum0.hamsterlist.models.HamsterList

data class HomeUiState(
    val username: String? = null,
    val knownHamsterLists: List<HamsterList> = emptyList(),
    val autoLoadLast: Boolean = false,
    val sheetState: HomeSheetState? = null,
    val dialogState: DialogState? = null
)
