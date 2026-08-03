package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

/**
 * A simple Compose UI test.
 * These run on an Android device or emulator.
 * Use these to verify that your UI elements appear correctly.
 */
class CommonUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun infoRow_displaysLabelAndValue() {
        // Given a label and a value
        val testLabel = "Manufacturer"
        val testValue = "POCO"

        // When we render the InfoRow component
        composeTestRule.setContent {
            InfoRow(label = testLabel, value = testValue)
        }

        // Then verify both pieces of text are displayed on the screen
        composeTestRule.onNodeWithText(testLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(testValue).assertIsDisplayed()
    }
}
