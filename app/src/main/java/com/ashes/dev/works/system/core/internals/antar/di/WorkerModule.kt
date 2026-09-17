package com.ashes.dev.works.system.core.internals.antar.di

import com.ashes.dev.works.system.core.internals.antar.data.worker.BatteryLogWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val workerModule = module {
    workerOf(::BatteryLogWorker)
}
