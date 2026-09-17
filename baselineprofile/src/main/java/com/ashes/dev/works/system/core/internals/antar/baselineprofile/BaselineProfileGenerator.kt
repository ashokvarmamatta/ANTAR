package com.ashes.dev.works.system.core.internals.antar.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Records the classes and methods used at startup and while moving through the tabs, so ART
 * compiles them ahead of time on install instead of interpreting them on first use.
 *
 * Generate with: `./gradlew :app:generateReleaseBaselineProfile` (Android 13+ device, or rooted 9+).
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(
        packageName = TARGET_PACKAGE,
        includeInStartupProfile = true
    ) {
        pressHome()
        startActivityAndWait()
        reachDashboard()
        swipeThroughTabs()
    }
}
