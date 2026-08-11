package com.erishan.traceback.opportunity.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object OpportunitiesListKey: NavKey

@Serializable
data class OpportunityDetailKey(val id: String): NavKey