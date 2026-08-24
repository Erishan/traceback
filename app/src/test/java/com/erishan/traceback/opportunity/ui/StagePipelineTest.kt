package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.core.enums.PipelineStage
import org.junit.Assert.assertEquals
import org.junit.Test

class StagePipelineTest {

    @Test
    fun `terminal stages leave every pipe segment empty`() {
        PipelineStage.entries
            .filter { it.isTerminal }
            .forEach { stage ->
                PipelineStage.track.indices.forEach { segmentIndex ->
                    assertEquals(
                        "${stage.name} should not imply progress at segment $segmentIndex",
                        PipeSegmentTone.Empty,
                        pipeSegmentTone(stage.trackIndex, segmentIndex),
                    )
                }
            }
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
