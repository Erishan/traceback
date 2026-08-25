package com.erishan.traceback.opportunity.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.theme.MinTouchTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class StagePipelineTest {

    @Test
    fun `stage picker hit area is the material 48dp minimum`() {
        assertEquals(MinTouchTarget, MinStagePickerSize)
        assertEquals(48.dp, MinTouchTarget)
    }

    @Test
    fun `terminal rail fades to 30 percent per design identity`() {
        assertEquals(0.30f, ExitedRailAlpha, 0f)
        assertEquals(0.30f, pipeRailAlpha(isTerminal = true), 0f)
        assertEquals(1f, pipeRailAlpha(isTerminal = false), 0f)
    }

    @Test
    fun `completed segments stay at 50 percent and do not share the terminal fade`() {
        assertEquals(0.50f, CompletedSegmentAlpha, 0f)
        assertNotEquals(CompletedSegmentAlpha, ExitedRailAlpha)
    }

    @Test
    fun `terminal stages paint every pipe segment as an exit in the stage color`() {
        val lost = Color(0xFFF87171)
        val track = Color(0xFF22262E)

        PipelineStage.entries
            .filter { it.isTerminal }
            .forEach { stage ->
                PipelineStage.track.indices.forEach { segmentIndex ->
                    assertEquals(
                        "${stage.name} should not imply progress at segment $segmentIndex",
                        PipeSegmentTone.Exited,
                        pipeSegmentTone(stage.trackIndex, segmentIndex),
                    )
                }
            }

        assertEquals(lost, pipeSegmentColor(PipeSegmentTone.Exited, stageColor = lost, trackColor = track))
        assertEquals(track, pipeSegmentColor(PipeSegmentTone.Empty, stageColor = lost, trackColor = track))
    }

    @Test
    fun `non terminal stages mark previous current and upcoming pipe segments`() {
        val tones = PipelineStage.track.indices.map { segmentIndex ->
            pipeSegmentTone(PipelineStage.INTERVIEW.trackIndex, segmentIndex)
        }

        assertEquals(
            listOf(
                PipeSegmentTone.Completed,
                PipeSegmentTone.Completed,
                PipeSegmentTone.Completed,
                PipeSegmentTone.Current,
                PipeSegmentTone.Empty,
                PipeSegmentTone.Empty,
            ),
            tones,
        )
    }
}
