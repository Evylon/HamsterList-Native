package org.stratum0.hamsterlist.android

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.stratum0.hamsterlist.koin.hamsterListModules

class HamsterListApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HamsterListApplication)
            modules(hamsterListModules())
        }
    }
}
