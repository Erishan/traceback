package com.erishan.traceback.opportunity.ui

import androidx.compose.ui.graphics.Color
import com.erishan.traceback.core.enums.PipelineStage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StagePipelineTest {

    @Test
    fun `terminal rail fades to 30 percent per design identity`() {
        assertEquals(0.30f, ExitedRailAlpha)
        assertEquals(0.30f, pipeRailAlpha(isTerminal = true))
        assertEquals(1f, pipeRailAlpha(isTerminal = false))
    }

    @Test
    fun `completed segments keep their colour and do not share the terminal fade`() {
        assertEquals(0.45f, CompletedSegmentAlpha)
        assertNotEquals(CompletedSegmentAlpha, ExitedRailAlpha)
    }

    @Test
    fun `terminal stages empty every pipe segment back to the track`() {
        val lost = Color(0xFFF87171)
        val track = Color(0xFF22262E)

        PipelineStage.entries
            .filter { it.isTerminal }
            .forEach { stage ->
                PipelineStage.track.indices.forEach { segmentIndex ->
                    assertEquals(
                        PipeSegmentTone.Exited,
                        pipeSegmentTone(stage.trackIndex, segmentIndex),
                        "${stage.name} should not imply progress at segment $segmentIndex",
                    )
                }
            }

        assertEquals(track, pipeSegmentColor(PipeSegmentTone.Exited, stageColor = lost, trackColor = track))
        assertEquals(track, pipeSegmentColor(PipeSegmentTone.Empty, stageColor = lost, trackColor = track))
        assertEquals(lost, pipeSegmentColor(PipeSegmentTone.Current, stageColor = lost, trackColor = track))
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
