package com.erishan.traceback.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Pointer-target minimum. Constant across themes, so it is reachable outside composition too. */
val MinTouchTarget: Dp = 48.dp

fun Modifier.minTouchTarget(): Modifier =
    sizeIn(minWidth = MinTouchTarget, minHeight = MinTouchTarget)

fun Modifier.minTouchClickable(
    enabled: Boolean = true,
    role: Role = Role.Button,
    onClickLabel: String? = null,
    onClick: () -> Unit,
): Modifier = minTouchTarget().clickable(
    enabled = enabled,
    role = role,
    onClickLabel = onClickLabel,
    onClick = onClick,
)

@Immutable
data class TracebackDimens(
    // 4-point spacing scale
    val spaceXxs: Dp = 4.dp,
    val spaceXs: Dp = 8.dp,
    val spaceS: Dp = 12.dp,
    val spaceM: Dp = 16.dp,
    val spaceL: Dp = 20.dp,
    val spaceXl: Dp = 24.dp,

    /** Gutter between screen content and the window edge. */
    val screenPadding: Dp = 20.dp,
    /** Width of every hairline edge. */
    val hairline: Dp = 1.dp,

    /** The stage light source on the edge of a card, and how far its glow reaches. */
    val rodWidth: Dp = 3.dp,
    val rodGlow: Dp = 14.dp,

    /** Segmented progress conduit: segment height and the gap between segments. */
    val conduitHeight: Dp = 6.dp,
    val conduitGap: Dp = 5.dp,

    /** The one warm moment: its size and how far its glow reaches. */
    val fabSize: Dp = 54.dp,
    val fabGlow: Dp = 34.dp,

    val touchTarget: Dp = MinTouchTarget,
)

internal val DefaultDimens = TracebackDimens()

val LocalTracebackDimens = staticCompositionLocalOf { DefaultDimens }
