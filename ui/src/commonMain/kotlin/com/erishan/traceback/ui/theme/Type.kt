package com.erishan.traceback.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font

/**
 * The closed type scale. Nine roles, one family, one numeral style.
 *
 * Numbers carry meaning in this app - every role
 * requests tabular figures. Columns of figures line up and a changing value does not shift
 * the text around it.
 */
private const val TabularFigures = "tnum"

private fun tracebackStyle(
    fontFamily: FontFamily,
    size: Float,
    lineHeight: Float,
    weight: FontWeight,
    letterSpacing: Float = 0f,
) = TextStyle(
    fontFamily = fontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp,
    fontFeatureSettings = TabularFigures,
)

@Composable
internal fun rememberTracebackTypography(): Typography {
    val fontFamily = FontFamily(
        Font(Res.font.manrope_regular, FontWeight.Normal),
        Font(Res.font.manrope_medium, FontWeight.Medium),
        Font(Res.font.manrope_bold, FontWeight.Bold),
        Font(Res.font.manrope_extrabold, FontWeight.ExtraBold),
    )
    return remember(fontFamily) {
        Typography(
            // screen title
            titleLarge = tracebackStyle(fontFamily, 26f, 32f, FontWeight.Bold, -0.8f),
            // detail title
            titleMedium = tracebackStyle(fontFamily, 20f, 26f, FontWeight.Bold, -0.5f),
            // card title
            titleSmall = tracebackStyle(fontFamily, 13f, 18f, FontWeight.Bold, -0.3f),
            // default text style: Material provides this one as LocalTextStyle
            bodyLarge = tracebackStyle(fontFamily, 14f, 20f, FontWeight.Normal),
            // description and value text
            bodyMedium = tracebackStyle(fontFamily, 12.5f, 18f, FontWeight.Normal),
            // secondary and meta text
            bodySmall = tracebackStyle(fontFamily, 11.5f, 17f, FontWeight.Normal),
            // buttons
            labelLarge = tracebackStyle(fontFamily, 13f, 18f, FontWeight.Bold),
            // pills - uppercased at the call site
            labelMedium = tracebackStyle(fontFamily, 10f, 14f, FontWeight.ExtraBold, 1.0f),
            // overlines - uppercased at the call site
            labelSmall = tracebackStyle(fontFamily, 9.5f, 13f, FontWeight.ExtraBold, 2.0f),
        )
    }
}
