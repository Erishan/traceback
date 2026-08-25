package com.erishan.traceback.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf


@Immutable
data class TracebackMotion(
    /** State flips the eye should not have to follow: selection, colour, alpha. */
    val fast: Int = 120,
    /** The default: enter, exit, expand, collapse. */
    val medium: Int = 220,
    /** Deliberate changes worth watching: sheets, stage transitions. */
    val slow: Int = 420,
    /** One full pass of the aurora fields behind the ground. */
    val ambient: Int = 12_000,
    /** Single breath of light when a stage changes. */
    val stageBloom: Int = 420,
    val standardEasing: Easing = CubicBezierEasing(0.2f, 0.7f, 0.2f, 1f),
    /** Press feedback. Springs, because a press is interruptible. */
    val pressSpring: SpringSpec<Float> = spring<Float>(
        dampingRatio = 0.55f,
        stiffness = Spring.StiffnessMediumLow,
    ),
)

internal val DefaultMotion = TracebackMotion()

val LocalTracebackMotion = staticCompositionLocalOf { DefaultMotion }
