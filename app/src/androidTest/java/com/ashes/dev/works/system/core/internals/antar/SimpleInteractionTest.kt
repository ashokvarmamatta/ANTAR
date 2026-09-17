package com.ashes.dev.works.system.core.internals.antar

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

/**
 * Launches the real MainActivity: gets past the onboarding (shown on a fresh install), checks the
 * dashboard loads, then switches to the System tab. Texts come from string resources, so the test
 * follows copy changes and translations.
 */
class SimpleInteractionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun text(@StringRes id: Int): String = composeTestRule.activity.getString(id)

    private fun isShown(text: String): Boolean =
        composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

    private fun waitFor(text: String) = composeTestRule.waitUntil(TIMEOUT_MS) { isShown(text) }

    @Test
    fun appStarts_andShowsDashboard() {
        val skip = text(R.string.intro_skip)
        val storage = text(R.string.dashboard_internal_storage)

        composeTestRule.waitUntil(TIMEOUT_MS) { isShown(skip) || isShown(storage) }
        if (isShown(skip)) composeTestRule.onAllNodesWithText(skip).onFirst().performClick()

        waitFor(storage)
        composeTestRule.onAllNodesWithText(storage).onFirst().assertIsDisplayed()

        composeTestRule.onAllNodesWithText(text(R.string.tab_system)).onFirst().performClick()

        val androidVersion = text(R.string.system_label_android_version)
        waitFor(androidVersion)
        composeTestRule.onAllNodesWithText(androidVersion).onFirst().assertIsDisplayed()
    }

    private companion object {
        const val TIMEOUT_MS = 15_000L
    }
}
