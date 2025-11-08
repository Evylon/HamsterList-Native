package org.stratum0.hamsterlist.koin

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.observable.makeObservable
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.stratum0.hamsterlist.business.SettingsRepository
import org.stratum0.hamsterlist.business.SharedContentManager
import org.stratum0.hamsterlist.network.LocalShoppingListApi
import org.stratum0.hamsterlist.network.RemoteShoppingListApi

expect val viewModelModule: Module

@OptIn(ExperimentalSettingsApi::class)
fun hamsterListModules() = viewModelModule + module {
    singleOf(::RemoteShoppingListApi)
    singleOf(::LocalShoppingListApi)
    singleOf(::SettingsRepository)
    singleOf(::SharedContentManager)
    single<ObservableSettings> { Settings().makeObservable() }
}
