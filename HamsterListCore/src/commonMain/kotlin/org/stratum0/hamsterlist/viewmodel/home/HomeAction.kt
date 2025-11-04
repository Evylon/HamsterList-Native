package org.stratum0.hamsterlist.viewmodel.home

import org.stratum0.hamsterlist.models.DialogState
import org.stratum0.hamsterlist.models.KnownHamsterList

sealed interface HomeAction {
    data class UpdateAutoLoadLast(val autoLoadLast: Boolean) : HomeAction
    data class UpdateUsername(val username: String) : HomeAction
    data class DeleteHamsterList(
        val knownHamsterList: KnownHamsterList
    ) : HomeAction

    data class LoadHamsterlist(
        val selectedList: KnownHamsterList,
        val navigateToList: () -> Unit
    ) : HomeAction

    object OpenListCreationSheet : HomeAction
    object OpenShareContentSheet : HomeAction
    object DismissSheet : HomeAction

    data class OpenDialog(val dialogState: DialogState) : HomeAction
    object DismissDialog : HomeAction
}