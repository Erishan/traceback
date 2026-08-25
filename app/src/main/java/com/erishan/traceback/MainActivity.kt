package com.erishan.traceback

import android.content.Context
import android.database.ContentObserver
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.erishan.traceback.ui.theme.TracebackTheme

private val NavigationBarLightScrim = Color.argb(0xE6, 0xFF, 0xFF, 0xFF)
private val NavigationBarDarkScrim = Color.argb(0x80, 0x1B, 0x1B, 0x1B)

private val AnimationScaleKeys = listOf(
    Settings.Global.ANIMATOR_DURATION_SCALE,
    Settings.Global.TRANSITION_ANIMATION_SCALE,
    Settings.Global.WINDOW_ANIMATION_SCALE,
)

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

            TracebackTheme(
                darkTheme = darkTheme,
                reducedMotion = rememberReducedMotion(),
            ) {
                App()
            }
        }
    }
}

@Composable
private fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    var reduced by remember { mutableStateOf(context.animationsDisabled()) }

    DisposableEffect(context) {
        val resolver = context.contentResolver
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                reduced = context.animationsDisabled()
            }
        }
        AnimationScaleKeys.forEach { key ->
            resolver.registerContentObserver(Settings.Global.getUriFor(key), false, observer)
        }
        onDispose { resolver.unregisterContentObserver(observer) }
    }

    return reduced
}

private fun Context.animationsDisabled(): Boolean = AnimationScaleKeys.any { key ->
    Settings.Global.getFloat(contentResolver, key, 1f) == 0f
}
