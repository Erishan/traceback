package com.erishan.traceback.opportunity.android

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.opportunitiesEntries(
    onOpenOpportunity: (String) -> Unit,
    onOpenMe: () -> Unit,
    onBack: () -> Unit,
) {
    entry<OpportunitiesListKey> {
        OpportunityListRoute(
            onOpenOpportunity = onOpenOpportunity,
            onOpenMe = onOpenMe,
        )
    }
    entry<OpportunityDetailKey> { key ->
        OpportunityDetailRoute(id = key.id, onBack = onBack, onOpenMe = onOpenMe)
    }
}
