package com.erishan.traceback.ui.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class MinTouchTargetTest {

    @Test
    fun `pointer targets share a single 48dp material minimum`() {
        assertEquals(48.dp, MinTouchTarget)
    }
}
