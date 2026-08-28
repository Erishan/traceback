package com.erishan.traceback.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.erishan.traceback.ui.theme.TracebackTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val IndigoCenter = Offset(0.00f, -0.12f)
private val TealCenter = Offset(1.28f, 0.04f)
private val WarmCenter = Offset(-0.14f, 1.24f)

private const val IndigoRadius = 0.44f
private const val TealRadius = 0.26f
private const val WarmRadius = 0.52f

private const val FieldMidStop = 0.30f
private const val FieldMidAlphaScale = 0.28f

private const val DriftAmplitude = 0.004f
private const val DriftSkew = 1.7f
private val IndigoPhase = 0f
private val TealPhase = (PI * 2.0 / 3.0).toFloat()
private val WarmPhase = (PI * 4.0 / 3.0).toFloat()
private val FullTurn = (PI * 2.0).toFloat()

@Composable
fun AuroraBackground(modifier: Modifier = Modifier, tint: Color? = null) {
    val colors = TracebackTheme.colors
    val motion = TracebackTheme.motion

    val phase = if (motion.stilled) {
        0f
    } else {
        val transition = rememberInfiniteTransition(label = "aurora")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = FullTurn,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = motion.ambient, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "auroraDrift",
        ).value
    }

    val warm = tint?.copy(alpha = colors.auroraWarm.alpha) ?: colors.auroraWarm

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(colors.ground)
        drawField(colors.auroraIndigo, IndigoCenter, IndigoRadius, drift(phase, IndigoPhase))
        drawField(colors.auroraTeal, TealCenter, TealRadius, drift(phase, TealPhase))
        drawField(warm, WarmCenter, WarmRadius, drift(phase, WarmPhase))
    }
}

private fun drift(phase: Float, offset: Float): Offset = Offset(
    x = sin(phase + offset) * DriftAmplitude,
    y = cos(phase * DriftSkew + offset) * DriftAmplitude,
)

private fun DrawScope.drawField(
    color: Color,
    center: Offset,
    radiusFraction: Float,
    drift: Offset,
) {
    val origin = Offset(
        x = (center.x + drift.x) * size.width,
        y = (center.y + drift.y) * size.height,
    )
    val radius = size.maxDimension * radiusFraction
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to color,
                FieldMidStop to color.copy(alpha = color.alpha * FieldMidAlphaScale),
                1f to Color.Transparent,
            ),
            center = origin,
            radius = radius,
        ),
        radius = radius,
        center = origin,
    )
}
