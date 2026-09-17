package com.ashes.dev.works.system.core.internals.antar.di

import androidx.room.Room
import com.ashes.dev.works.system.core.internals.antar.data.local.cache.AppsCacheDataSource
import com.ashes.dev.works.system.core.internals.antar.data.local.db.AntarDatabase
import com.ashes.dev.works.system.core.internals.antar.data.local.preferences.createSettingsDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    // No destructive fallback: a schema bump without a Migration must fail loudly in testing,
    // never silently wipe the 30-day battery history in production.
    single { Room.databaseBuilder(androidContext(), AntarDatabase::class.java, "antar_db").build() }
    single { get<AntarDatabase>().batteryLogDao() }
}

val dataStoreModule = module {
    single { createSettingsDataStore(androidContext(), get(IoDispatcher)) }
    single { AppsCacheDataSource(androidContext(), get(IoDispatcher)) }
}
