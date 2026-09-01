package com.erishan.traceback

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.erishan.traceback.opportunity.ui.CreateSheetShowcase
import com.erishan.traceback.opportunity.ui.DetailScreenShowcase
import com.erishan.traceback.opportunity.ui.ListScreenShowcase
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenshotTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun listDark() = capture("list-dark") { ListScreenShowcase(darkTheme = true) }

    @Test
    fun listLight() = capture("list-light") { ListScreenShowcase(darkTheme = false) }

    @Test
    fun detailDark() = capture("detail-dark") { DetailScreenShowcase(darkTheme = true) }

    @Test
    fun detailLight() = capture("detail-light") { DetailScreenShowcase(darkTheme = false) }

    @Test
    fun createDark() = capture("create-dark") { CreateSheetShowcase(darkTheme = true) }

    @Test
    fun createLight() = capture("create-light") { CreateSheetShowcase(darkTheme = false) }

    private fun capture(name: String, content: @Composable () -> Unit) {
        compose.setContent(content)
        compose.waitForIdle()

        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val directory = outputDirectory()
        directory.mkdirs()
        val file = File(directory, "$name.png")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, PngQuality, out)
        }
        check(file.length() > 0) { "wrote an empty file at " + file.absolutePath }
    }

    private fun outputDirectory(): File {
        val fromGradle = InstrumentationRegistry.getArguments().getString(AdditionalOutputArg)
        if (fromGradle != null) return File(fromGradle)
        return File(
            InstrumentationRegistry.getInstrumentation().targetContext.filesDir,
            "screenshots",
        )
    }

    private companion object {
        /** Ignored for PNG, but the API demands a number. */
        const val PngQuality = 100

        /** Set by AGP; the directory it collects from the device after the run. */
        const val AdditionalOutputArg = "additionalTestOutputDir"
    }
}
