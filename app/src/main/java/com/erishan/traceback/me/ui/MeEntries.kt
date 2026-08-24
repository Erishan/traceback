package com.erishan.traceback.me.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.meEntries(
    onBack: () -> Unit,
) {
    entry<MeKey> {
        MeRoute(onBack = onBack)
    }
}
