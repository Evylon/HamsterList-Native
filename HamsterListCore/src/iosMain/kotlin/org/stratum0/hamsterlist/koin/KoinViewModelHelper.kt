package org.stratum0.hamsterlist.koin

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.stratum0.hamsterlist.viewmodel.home.HomeViewModel
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListViewModel

class KoinViewModelHelper : KoinComponent {
    val homeViewModel: HomeViewModel by inject()

    fun shoppingListViewModel(listId: String): ShoppingListViewModel {
        val shoppingListViewModel by inject<ShoppingListViewModel> { parametersOf(listId) }
        return shoppingListViewModel
    }
}