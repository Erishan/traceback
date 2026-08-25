package com.erishan.traceback.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val TracebackShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),  // chip, small tile
    small = RoundedCornerShape(12.dp),      // input, button
    medium = RoundedCornerShape(18.dp),     // glass card
    large = RoundedCornerShape(26.dp),      // bottom sheet
)

/** Corners that carry identity rather than a size step. */
val FabShape = RoundedCornerShape(19.dp)
val PillShape = RoundedCornerShape(7.dp)
val ButtonShape = TracebackShapes.small
