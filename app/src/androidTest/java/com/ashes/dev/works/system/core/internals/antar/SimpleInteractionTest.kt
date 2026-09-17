package com.ashes.dev.works.system.core.internals.antar

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

/**
 * A simple Interaction (Integration) test.
 * This uses the actual MainActivity to test how components interact.
 */
class SimpleInteractionTest {

    // Starts the actual MainActivity
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appStarts_andShowsDashboard() {
        // Wait for the app to load (Dashboard tab is default)
        // We look for a unique text in the Dashboard like "INTERNAL STORAGE"
        composeTestRule.onNodeWithText("INTERNAL STORAGE").assertIsDisplayed()
        
        // Example interaction: Click on the System tab
        composeTestRule.onNodeWithText("System").performClick()
        
        // Verify we moved or changed view (System info usually contains "Android Version")
        composeTestRule.onNodeWithText("Android Version").assertIsDisplayed()
    }
}
