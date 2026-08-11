package com.erishan.traceback

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    Scaffold { innerPadding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                opportunitiesEntries(
                    onOpenOpportunity = { id -> backStack.add(OpportunityDetailKey(id)) }, // ileri = push
                    onBack = { backStack.removeLastOrNull() },                              // geri = pop
                )
            },
        )
    }
}
