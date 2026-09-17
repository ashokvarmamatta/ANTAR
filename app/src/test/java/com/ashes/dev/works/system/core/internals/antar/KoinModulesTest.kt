package com.ashes.dev.works.system.core.internals.antar

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkerParameters
import com.ashes.dev.works.system.core.internals.antar.di.appModules
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

/**
 * Koin resolves dependencies at runtime, so a missing or mis-typed binding only shows up as a crash
 * when a screen opens. This checks every definition's constructor against the graph at test time.
 */
@OptIn(KoinExperimentalAPI::class)
class KoinModulesTest {
    @Test
    fun appModule_isComplete() {
        module { includes(appModules) }.verify(
            extraTypes = listOf(Context::class, SavedStateHandle::class, WorkerParameters::class)
        )
    }
}
