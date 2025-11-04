package org.stratum0.hamsterlist.viewmodel.home

import org.stratum0.hamsterlist.models.DialogState
import org.stratum0.hamsterlist.models.KnownHamsterList

data class HomeUiState(
    val username: String? = null,
    val knownHamsterLists: List<KnownHamsterList> = emptyList(),
    val loadedListId: String? = null,
    val autoLoadLast: Boolean = false,
    val sheetState: HomeSheetState? = null,
    val dialogState: DialogState? = null
) {
    val lastLoadedServer = knownHamsterLists.find { it.listId == loadedListId }?.serverHostName
}
