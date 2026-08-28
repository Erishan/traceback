package com.erishan.traceback.opportunity.ui

import androidx.compose.ui.unit.dp
import com.erishan.traceback.ui.theme.MinTouchTarget
import org.junit.Assert.assertEquals
import org.junit.Test

class StageTriggerTest {

    @Test
    fun `stage picker hit area is the material 48dp minimum`() {
        assertEquals(MinTouchTarget, MinStagePickerSize)
        assertEquals(48.dp, MinTouchTarget)
    }
}
