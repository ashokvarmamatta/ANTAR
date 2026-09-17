package com.ashes.dev.works.system.core.internals.antar

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Debug builds carry the ".debug" application id suffix, so match the base id as a prefix.
        val packageName = InstrumentationRegistry.getInstrumentation().targetContext.packageName
        assertTrue(packageName, packageName.startsWith("com.ashes.dev.works.system.core.internals.antar"))
    }
}
