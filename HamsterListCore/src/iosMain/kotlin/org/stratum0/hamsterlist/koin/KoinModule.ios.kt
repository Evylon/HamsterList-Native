package org.stratum0.hamsterlist.koin

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.stratum0.hamsterlist.viewmodel.home.HomeViewModel
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListViewModel

actual val viewModelModule = module {
    factory { parameters -> ShoppingListViewModel(parameters.get(), get(), get(), get(), get()) }
    factoryOf(::HomeViewModel)
}

fun initKoin() {
    startKoin {
        modules(hamsterListModules())
    }
}
