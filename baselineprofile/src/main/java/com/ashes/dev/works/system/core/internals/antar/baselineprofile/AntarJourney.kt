package com.ashes.dev.works.system.core.internals.antar.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until

/** Package of the build under test (the release variant the profile ships in). */
/** Package id of the profiling builds (set in app/build.gradle.kts), so a store/release install is never touched. */
const val TARGET_PACKAGE = "com.ashes.dev.works.system.core.internals.antar.profile"

private const val UI_TIMEOUT_MS = 10_000L
private const val TAB_COUNT = 12

/** Leaves onboarding if this is a fresh install, then waits for the dashboard. */
fun MacrobenchmarkScope.reachDashboard() {
    device.wait(Until.hasObject(By.pkg(TARGET_PACKAGE).depth(0)), UI_TIMEOUT_MS)
    // The "Skip" button only exists on the first launch after install.
    device.findObject(By.text("Skip"))?.click()
    device.wait(Until.hasObject(By.textContains("RAM")), UI_TIMEOUT_MS)
}

/** Swipes through every tab of the main pager, the app's hottest code path after startup. */
fun MacrobenchmarkScope.swipeThroughTabs() {
    val width = device.displayWidth
    val height = device.displayHeight
    repeat(TAB_COUNT - 1) {
        device.swipe(width * 9 / 10, height / 2, width / 10, height / 2, 12)
        device.waitForIdle()
    }
    // Scroll the last tab so list/grid composition is profiled too.
    device.findObject(By.scrollable(true))?.fling(Direction.DOWN)
    device.waitForIdle()
}
