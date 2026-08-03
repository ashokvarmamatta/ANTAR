package com.ashes.dev.works.system.core.internals.antar

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * A simple JUnit test case.
 * JUnit tests run on your computer (JVM) and are very fast.
 * Use these for testing logic that doesn't need the Android system.
 */
class SimpleLogicTest {

    @Test
    fun testPercentageCalculation() {
        val total = 1000f
        val used = 450f
        
        // Simple logic: (used / total) * 100
        val percentage = (used / total) * 100
        
        assertEquals(45f, percentage, 0.1f)
    }
}
