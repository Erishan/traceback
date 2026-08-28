package com.erishan.traceback

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.setUnhandledExceptionHook

/**
 * Logs Kotlin/Native crashes to the Xcode console before SIGABRT hides the message.
 */
@OptIn(ExperimentalNativeApi::class)
internal object IosCrashDiagnostics {
    private var installed = false

    fun install() {
        if (installed) return
        installed = true

        setUnhandledExceptionHook { throwable ->
            val report = buildString {
                appendLine("=== Traceback unhandled Kotlin exception ===")
                appendLine("type: ${throwable::class.simpleName}")
                appendLine("message: ${throwable.message ?: "(none)"}")
                appendLine("cause: ${throwable.cause?.message ?: "(none)"}")
                appendLine("stacktrace:")
                appendLine(throwable.stackTraceToString())
                appendLine("=== end ===")
            }
            println(report)
        }
    }
}
