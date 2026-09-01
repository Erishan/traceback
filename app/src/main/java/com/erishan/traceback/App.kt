package com.erishan.traceback

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.erishan.traceback.me.android.MeKey
import com.erishan.traceback.me.android.meEntries
import com.erishan.traceback.opportunity.android.OpportunitiesListKey
import com.erishan.traceback.opportunity.android.OpportunityDetailKey
import com.erishan.traceback.opportunity.android.opportunitiesEntries

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
                onOpenMe = { backStack.add(MeKey) },
                onBack = { backStack.removeLastOrNull() },
            )
            meEntries(
                onBack = { backStack.removeLastOrNull() },
            )
        },
    )
}
