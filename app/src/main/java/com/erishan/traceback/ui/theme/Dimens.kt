package com.erishan.traceback.ui.theme

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Android / Material accessibility minimum for pointer targets. */
val MinTouchTarget = 48.dp

fun Modifier.minTouchTarget(): Modifier =
    sizeIn(minWidth = MinTouchTarget, minHeight = MinTouchTarget)
