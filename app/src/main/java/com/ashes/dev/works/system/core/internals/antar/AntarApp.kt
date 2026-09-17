package com.ashes.dev.works.system.core.internals.antar

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ashes.dev.works.system.core.internals.antar.data.worker.BatteryLogWorker
import com.ashes.dev.works.system.core.internals.antar.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.GlobalContext.startKoin
import java.util.concurrent.TimeUnit

class AntarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AntarApp)
            // Koin creates workers, so BatteryLogWorker gets its repository by constructor.
            workManagerFactory()
            modules(appModules)
        }

        scheduleBatteryLogging()
    }

    private fun scheduleBatteryLogging() {
        val workRequest = PeriodicWorkRequestBuilder<BatteryLogWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "battery_log_worker",
            ExistingPeriodicWorkPolicy.UPDATE, // interval/constraint changes reach existing installs
            workRequest
        )
    }
}
