package com.erishan.traceback.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The closed type scale. Nine roles, one family, one numeral style.
 *
 * Numbers carry meaning in this app - every role
 * requests tabular figures. Columns of figures line up and a changing value does not shift
 * the text around it.
 */
private const val TabularFigures = "tnum"

private fun tracebackStyle(
    size: Float,
    lineHeight: Float,
    weight: FontWeight,
    letterSpacing: Float = 0f,
) = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp,
    fontFeatureSettings = TabularFigures,
)

val TracebackTypography = Typography(
    // screen title
    titleLarge = tracebackStyle(26f, 32f, FontWeight.Bold, -0.8f),
    // detail title
    titleMedium = tracebackStyle(20f, 26f, FontWeight.Bold, -0.5f),
    // card title
    titleSmall = tracebackStyle(13f, 18f, FontWeight.Bold, -0.3f),
    // default text style: Material provides this one as LocalTextStyle
    bodyLarge = tracebackStyle(14f, 20f, FontWeight.Normal),
    // description and value text
    bodyMedium = tracebackStyle(12.5f, 18f, FontWeight.Normal),
    // secondary and meta text
    bodySmall = tracebackStyle(11.5f, 17f, FontWeight.Normal),
    // buttons
    labelLarge = tracebackStyle(13f, 18f, FontWeight.Bold),
    // pills - uppercased at the call site
    labelMedium = tracebackStyle(10f, 14f, FontWeight.ExtraBold, 1.0f),
    // overlines - uppercased at the call site
    labelSmall = tracebackStyle(9.5f, 13f, FontWeight.ExtraBold, 2.0f),
)
