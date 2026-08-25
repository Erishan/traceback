package com.erishan.traceback

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.erishan.traceback.ui.components.LocalReducedMotion
import com.erishan.traceback.ui.theme.TracebackTheme

private val NavigationBarLightScrim = Color.argb(0xE6, 0xFF, 0xFF, 0xFF)
private val NavigationBarDarkScrim = Color.argb(0x80, 0x1B, 0x1B, 0x1B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkTheme = isSystemInDarkTheme()

            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = Color.TRANSPARENT,
                        darkScrim = Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = NavigationBarLightScrim,
                        darkScrim = NavigationBarDarkScrim,
                    ) { darkTheme },
                )
                onDispose {}
            }

            val reducedMotion = remember { animationsDisabled() }
            CompositionLocalProvider(LocalReducedMotion provides reducedMotion) {
                TracebackTheme(darkTheme = darkTheme) {
                    App()
                }
            }
        }
    }
}

private fun Context.animationsDisabled(): Boolean =
    Settings.Global.getFloat(
        contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f,
    ) == 0f
