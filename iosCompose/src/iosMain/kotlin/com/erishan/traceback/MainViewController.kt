package com.erishan.traceback

import androidx.compose.ui.window.ComposeUIViewController
import com.erishan.traceback.core.di.createIosSharedContainer
import com.erishan.traceback.shell.IosShellApp
import platform.UIKit.UIViewController

private val iosContainer by lazy { createIosSharedContainer() }

fun MainViewController(): UIViewController = ComposeUIViewController {
    IosShellApp(iosContainer)
}
