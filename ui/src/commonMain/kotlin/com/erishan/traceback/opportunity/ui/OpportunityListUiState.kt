package com.erishan.traceback.opportunity.ui

import androidx.compose.runtime.Immutable
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Opportunity

@Immutable
data class StageDistribution(val counts: Map<PipelineStage, Int>) {
    val total: Int = counts.values.sum()

    val active: Int = counts.entries.sumOf { (stage, count) -> if (stage.isTerminal) 0 else count }

    val isEmpty: Boolean get() = total == 0

    val present: List<StageCount> = PipelineStage.entries.mapNotNull { stage ->
        counts[stage]?.takeIf { it > 0 }?.let { StageCount(stage, it) }
    }

    fun busiest(n: Int): List<StageCount> = present.sortedByDescending { it.count }.take(n)

    companion object {
        val Empty = StageDistribution(emptyMap())
    }
}

@Immutable
data class StageCount(val stage: PipelineStage, val count: Int)

data class OpportunityListUiState(
    val opportunities: List<Opportunity> = emptyList(),
    val selectedFilter: OpportunityFilter = OpportunityFilter.All,
    val distribution: StageDistribution = StageDistribution.Empty,
    val isLoading: Boolean = true,
)

fun listUiState(
    all: List<Opportunity>,
    filter: OpportunityFilter,
): OpportunityListUiState = OpportunityListUiState(
    opportunities = all.filter { filter.matches(it.pipelineStage) },
    selectedFilter = filter,
    distribution = StageDistribution(all.groupingBy { it.pipelineStage }.eachCount()),
    isLoading = false,
)
