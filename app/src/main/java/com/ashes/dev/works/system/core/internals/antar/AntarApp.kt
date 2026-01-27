package com.ashes.dev.works.system.core.internals.antar

import android.app.Application
import com.ashes.dev.works.system.core.internals.antar.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class AntarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AntarApp)
            modules(appModule)
        }
    }
}