package com.erishan.traceback.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.erishan.traceback.R

private val Inter = FontFamily(
    Font(R.font.inter_regular,  FontWeight.Normal),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)
private val AppFontFamily = Inter

val Typography = Typography(
    // screen title
    titleLarge = TextStyle(
        fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = (-0.5).sp
    ),
    // card title
    titleMedium = TextStyle(
        fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp, lineHeight = 20.sp, letterSpacing = (-0.2).sp
    ),
    // desc
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 20.sp
    ),
    // exp / secondary
    bodySmall = TextStyle(
        fontFamily = AppFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 18.sp
    ),
    // button texts
    labelLarge = TextStyle(
        fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 18.sp
    ),
    // overlines & meta
    labelSmall = TextStyle(
        fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 1.5.sp
    ),
)
