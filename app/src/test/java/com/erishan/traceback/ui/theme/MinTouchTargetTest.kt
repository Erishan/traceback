package com.erishan.traceback.ui.theme

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class MinTouchTargetTest {

    @Test
    fun `pointer targets share a single 48dp material minimum`() {
        assertEquals(48.dp, MinTouchTarget)
    }
}
