package com.erishan.traceback.core.enums

enum class PipelineStage {
    DRAFT,
    APPLIED,
    IN_CONVERSATION,
    INTERVIEW,
    HIRED,
    DELIVERED,
    CLOSED,
    LOST;

    /** True when the opportunity has left the track. Terminal stages carry no progress. */
    val isTerminal: Boolean
        get() = when (this) {
            CLOSED, LOST -> true
            DRAFT, APPLIED, IN_CONVERSATION, INTERVIEW, HIRED, DELIVERED -> false
        }

    /** Position on [track], or null for terminal stages. */
    val trackIndex: Int?
        get() = track.indexOf(this).takeIf { it >= 0 }

    companion object {
        val track: List<PipelineStage> = entries.filterNot { it.isTerminal }
    }
}
