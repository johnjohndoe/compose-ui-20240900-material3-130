package com.example.app.screenshots.tests

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app.ExampleScreenPreview
import com.example.app.screenshots.utils.TestDeviceConfiguration.PIXEL_4_XL
import com.example.app.screenshots.utils.TestParameter
import com.example.app.screenshots.utils.captureForDevice
import com.example.app.screenshots.utils.getScreenshotName
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class ExampleHomeScreenTests {

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val groupName = "1.0 Example"

    @Test
    fun `example bottom sheet preview test`() {
        val parameter = TestParameter(PIXEL_4_XL)
        composeTestRule.captureForDevice(parameter, groupName, getScreenshotName()) {
            ExampleScreenPreview()
        }
    }
}
