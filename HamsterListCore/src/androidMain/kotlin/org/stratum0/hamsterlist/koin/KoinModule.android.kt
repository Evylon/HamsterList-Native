package org.stratum0.hamsterlist.koin

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.stratum0.hamsterlist.viewmodel.home.HomeViewModel
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListViewModel

actual val viewModelModule = module {
    viewModel { parameters -> ShoppingListViewModel(parameters.get(), get()) }
    viewModelOf(::HomeViewModel)
}
