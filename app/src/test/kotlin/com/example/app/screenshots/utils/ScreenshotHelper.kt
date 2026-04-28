package com.example.app.screenshots.utils

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalRoborazziApi::class)
val DefaultRoborazziOptions =
    RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f),
        recordOptions = RoborazziOptions.RecordOptions(resizeScale = 0.5),
    )

enum class TestDeviceConfiguration(val deviceName: String, val width: Int, val height: Int, val dpi: Int) {
    PIXEL_4_XL("Pixel_4_XL", width = 411, height = 869, dpi = 537),
}

data class TestParameter(
    val deviceConfiguration: TestDeviceConfiguration,
    val isNightMode: Boolean = false,
    val locale: String = "en-rEN",
    val fontScale: Float = 1f,
)

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureForDevice(
    testParameter: TestParameter,
    groupName: String,
    screenshotName: String,
    roborazziOptions: RoborazziOptions = DefaultRoborazziOptions,
    body: @Composable () -> Unit,
) {
    val deviceConfiguration = testParameter.deviceConfiguration

    RuntimeEnvironment.setQualifiers("${testParameter.locale}-w${deviceConfiguration.width}dp-h${deviceConfiguration.height}dp-${deviceConfiguration.dpi}dpi")
    RuntimeEnvironment.setFontScale(testParameter.fontScale)

    activity.resources.updateConfiguration(activity.resources.configuration.apply {
        uiMode = if (testParameter.isNightMode) UI_MODE_NIGHT_YES else UI_MODE_NIGHT_NO
    }, activity.resources.displayMetrics)

    activity.recreate()

    activity.setContent {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
        ) {
            body()
        }
    }

    // Roborazzi only uses full-screen capture (including modal windows) when Robolectric reports
    // more than one window root. Material3 ModalBottomSheet attaches its window after layout;
    // capturing immediately yields a single root and a bitmap of only the activity ComposeView —
    // the sheet is missing, so the image looks empty. Idle the main looper so the sheet window
    // exists before capture (see Roborazzi captureScreenIfMultipleWindows / issue #694).
    waitForIdle()
    ShadowLooper.shadowMainLooper().idle()
    ShadowLooper.shadowMainLooper().idleFor(400, TimeUnit.MILLISECONDS)
    waitForIdle()

    onRoot().captureRoboImage(
        buildString {
            append("src/test/screenshots/")
            append("${testParameter.deviceConfiguration.deviceName}_${if (testParameter.isNightMode) "dark" else "light"}/")
            append("${testParameter.locale}/")
            append("fontScale_${testParameter.fontScale}/")
            append("$groupName/")
            append("$screenshotName.png")
        },
        roborazziOptions = roborazziOptions,
    )
}
