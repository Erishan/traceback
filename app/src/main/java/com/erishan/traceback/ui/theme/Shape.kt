package com.erishan.traceback.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),  // stage pill
    small      = RoundedCornerShape(10.dp), // input & chip
    medium     = RoundedCornerShape(16.dp), // card
    large      = RoundedCornerShape(26.dp), // bottom sheet
)
