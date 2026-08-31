package com.erishan.traceback.shell

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import com.erishan.traceback.ui.theme.TracebackTheme
import kotlin.math.abs

private const val SwipeBackThresholdFraction = 0.25f

@Composable
fun Modifier.iosSwipeBack(
    enabled: Boolean,
    onBack: () -> Unit,
): Modifier {
    if (!enabled) return this

    val dimens = TracebackTheme.dimens
    return iosEdgeSwipeBack(
        edgeWidth = dimens.spaceL,
        minSwipeThreshold = dimens.spaceXl * 2,
        onBack = onBack,
    )
}

private fun Modifier.iosEdgeSwipeBack(
    edgeWidth: Dp,
    minSwipeThreshold: Dp,
    onBack: () -> Unit,
): Modifier = pointerInput(onBack, edgeWidth, minSwipeThreshold) {
    val edgeWidthPx = edgeWidth.toPx()
    val minThresholdPx = minSwipeThreshold.toPx()
    val thresholdPx = maxOf(minThresholdPx, size.width * SwipeBackThresholdFraction)
    val touchSlop = viewConfiguration.touchSlop

    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        if (down.position.x > edgeWidthPx) return@awaitEachGesture

        val pointerId = down.id
        var totalDragX = 0f
        var totalDragY = 0f
        var dragging = false

        while (true) {
            val event = awaitPointerEvent(pass = PointerEventPass.Initial)
            val change = event.changes.firstOrNull { it.id == pointerId } ?: break

            if (!change.pressed) {
                if (dragging && totalDragX >= thresholdPx) {
                    onBack()
                }
                break
            }

            val dx = change.position.x - change.previousPosition.x
            val dy = change.position.y - change.previousPosition.y
            totalDragX += dx
            totalDragY += dy

            if (!dragging) {
                val absX = abs(totalDragX)
                val absY = abs(totalDragY)
                if (absX > touchSlop || absY > touchSlop) {
                    if (absY > absX || totalDragX < 0f) break
                    dragging = true
                }
            }

            if (dragging && dx > 0f) {
                change.consume()
            }
        }
    }
}
