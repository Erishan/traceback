package com.erishan.traceback

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.erishan.traceback.opportunity.ui.OpportunitiesListKey
import com.erishan.traceback.opportunity.ui.OpportunityDetailKey
import com.erishan.traceback.opportunity.ui.opportunitiesEntries

@Composable
fun App() {
    val backStack = rememberNavBackStack(OpportunitiesListKey)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            opportunitiesEntries(
                onOpenOpportunity = { id -> backStack.add(OpportunityDetailKey(id)) },
                onBack = { backStack.removeLastOrNull() },
            )
        },
    )
}
