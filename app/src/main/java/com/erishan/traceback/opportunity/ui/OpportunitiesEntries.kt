package com.erishan.traceback.opportunity.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.opportunitiesEntries(
    onOpenOpportunity: (String) -> Unit,
    onBack: () -> Unit,
) {
    entry<OpportunitiesListKey> {
        OpportunityListRoute(onOpenOpportunity = onOpenOpportunity)
    }
    entry<OpportunityDetailKey> { key ->
        OpportunityDetailRoute(id = key.id, onBack = onBack)
    }
}
