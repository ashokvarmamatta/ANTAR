package com.ashes.dev.works.system.core.internals.antar.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

/** Qualifier for the IO dispatcher injected into every repository and data source. */
val IoDispatcher = named("io")

val dispatcherModule = module {
    single<CoroutineDispatcher>(IoDispatcher) { Dispatchers.IO }
}
