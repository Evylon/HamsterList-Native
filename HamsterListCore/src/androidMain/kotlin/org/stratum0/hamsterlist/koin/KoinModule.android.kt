package org.stratum0.hamsterlist.koin

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import org.stratum0.hamsterlist.viewmodel.HomeViewModel
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListViewModel

actual val viewModelModule = module {
    viewModel { parameters -> ShoppingListViewModel(parameters.get(), get()) }
    viewModelOf(::HomeViewModel)
}
