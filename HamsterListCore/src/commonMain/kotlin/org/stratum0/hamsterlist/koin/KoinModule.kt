package org.stratum0.hamsterlist.koin

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.observable.makeObservable
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.stratum0.hamsterlist.business.ShoppingListRepository
import org.stratum0.hamsterlist.business.ShoppingListRepositoryImpl
import org.stratum0.hamsterlist.business.UserRepository
import org.stratum0.hamsterlist.network.ShoppingListApi

expect val viewModelModule: Module

@OptIn(ExperimentalSettingsApi::class)
fun hamsterListModules() = viewModelModule + module {
    single<ShoppingListRepository> { ShoppingListRepositoryImpl(get()) }
    singleOf(::ShoppingListApi)
    singleOf(::UserRepository)
    single<ObservableSettings> { Settings().makeObservable() }
}
