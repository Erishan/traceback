package com.erishan.traceback.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erishan.traceback.ui.components.TbBarIconButton
import com.erishan.traceback.ui.theme.MinTouchTarget
import com.erishan.traceback.ui.theme.TracebackTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MinTouchTargetSemanticsTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun barIconButton_clickableSemanticsAreAtLeast48dp() {
        compose.setContent {
            TracebackTheme {
                TbBarIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = {},
                )
            }
        }
        compose.onNodeWithContentDescription("Back").apply {
            assertWidthIsAtLeast(MinTouchTarget)
            assertHeightIsAtLeast(MinTouchTarget)
        }
    }
}
