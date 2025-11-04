package org.stratum0.hamsterlist.viewmodel.home

import org.stratum0.hamsterlist.models.KnownHamsterList

sealed interface HomeSheetState {
    object ListCreation : HomeSheetState
    data class ContentSharing(val knownHamsterLists: List<KnownHamsterList>) : HomeSheetState
}