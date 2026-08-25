package com.erishan.traceback.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.erishan.traceback.ui.theme.TracebackTheme

@Composable
fun TbScaffold(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    snackbarHostState: SnackbarHostState? = null,
    floatingActionButton: (@Composable () -> Unit)? = null,
    auroraTint: Color? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AuroraBackground(tint = auroraTint)
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = TracebackTheme.colors.textHigh,
            topBar = {
                if (title != null || navigationIcon != null || actions != null) {
                    TbTopAppBar(title = title, navigationIcon = navigationIcon, actions = actions)
                }
            },
            snackbarHost = {
                if (snackbarHostState != null) {
                    SnackbarHost(snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            shape = MaterialTheme.shapes.small,
                            containerColor = TracebackTheme.colors.glassStrong,
                            contentColor = TracebackTheme.colors.textHigh,
                            actionColor = TracebackTheme.colors.accent,
                        )
                    }
                }
            },
            floatingActionButton = { floatingActionButton?.invoke() },
            content = content,
        )
    }
}
