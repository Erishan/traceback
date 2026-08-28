package com.erishan.traceback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.erishan.traceback.core.di.SharedContainer
import com.erishan.traceback.core.di.createIosSharedContainer
import com.erishan.traceback.shell.IosShellApp
import com.erishan.traceback.ui.theme.TracebackTheme
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    IosCrashDiagnostics.install()
    return ComposeUIViewController {
        Bootstrap()
    }
}

@Composable
private fun Bootstrap() {
    var container by remember { mutableStateOf<SharedContainer?>(null) }
    var failure by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        runCatching { createIosSharedContainer() }
            .onSuccess { container = it }
            .onFailure { t ->
                t.printStackTrace()
                failure = t.message ?: t.toString()
            }
    }

    TracebackTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when {
                failure != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text("Traceback failed to start")
                        Text(failure.orEmpty())
                    }
                }
                container != null -> IosShellApp(checkNotNull(container))
                else -> {
                    Text(
                        "Starting…",
                        modifier = Modifier.padding(24.dp),
                    )
                }
            }
        }
    }
}
