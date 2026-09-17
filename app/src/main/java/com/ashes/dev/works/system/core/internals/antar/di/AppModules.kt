package com.ashes.dev.works.system.core.internals.antar.di

/** The whole Koin graph, in dependency order. Verified by KoinModulesTest. */
val appModules = listOf(
    dispatcherModule,
    databaseModule,
    dataStoreModule,
    repositoryModule,
    useCaseModule,
    viewModelModule,
    workerModule
)
