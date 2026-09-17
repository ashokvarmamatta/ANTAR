package com.ashes.dev.works.system.core.internals.antar

import android.content.Context
import com.ashes.dev.works.system.core.internals.antar.di.appModule
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

/**
 * Koin resolves dependencies at runtime, so a missing or mis-typed binding only shows up as a crash
 * when a screen opens. This checks every definition's constructor against the graph at test time.
 */
@OptIn(KoinExperimentalAPI::class)
class KoinModulesTest {
    @Test
    fun appModule_isComplete() {
        appModule.verify(extraTypes = listOf(Context::class))
    }
}
