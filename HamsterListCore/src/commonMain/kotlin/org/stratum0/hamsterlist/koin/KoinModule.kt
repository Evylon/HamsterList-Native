package org.stratum0.hamsterlist.koin

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.stratum0.hamsterlist.business.ShoppingListRepository
import org.stratum0.hamsterlist.business.ShoppingListRepositoryImpl
import org.stratum0.hamsterlist.business.UserRepository
import org.stratum0.hamsterlist.network.ShoppingListApi

expect val viewModelModule: Module

fun hamsterListModules() = viewModelModule + module {
    single<ShoppingListRepository> { ShoppingListRepositoryImpl(get()) }
    singleOf(::ShoppingListApi)
    singleOf(::UserRepository)
}
