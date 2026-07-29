package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.opportunity.domain.Opportunity

data class OpportunityListUiState(
    val opportunities: List<Opportunity> = emptyList(),
    val isLoading: Boolean = true,
)