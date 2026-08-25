package com.erishan.traceback.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val SwatchSize = 46.dp
private val SwatchLabelWidth = 116.dp
private val RodHeight = 26.dp
private val ShapeSampleSize = 54.dp
private val SectionGap = 28.dp

private const val IndigoFieldX = 0.12f
private const val IndigoFieldY = 0.06f
private const val TealFieldX = 0.94f
private const val TealFieldY = 0.42f
private const val WarmFieldX = 0.62f
private const val WarmFieldY = 1.02f
private const val FieldRadius = 0.85f

@Composable
private fun AuroraBackdrop(modifier: Modifier = Modifier) {
    val colors = TracebackTheme.colors
    Canvas(modifier) {
        drawRect(colors.ground)
        val radius = size.minDimension * FieldRadius
        listOf(
            colors.auroraIndigo to Offset(size.width * IndigoFieldX, size.height * IndigoFieldY),
            colors.auroraTeal to Offset(size.width * TealFieldX, size.height * TealFieldY),
            colors.auroraWarm to Offset(size.width * WarmFieldX, size.height * WarmFieldY),
        ).forEach { (tint, center) ->
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(tint, Color.Transparent),
                    center = center,
                    radius = radius,
                ),
            )
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceS)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TracebackTheme.colors.accent,
        )
        content()
    }
}

@Composable
private fun SwatchRow(name: String, fill: Color, note: String) {
    val dimens = TracebackTheme.dimens
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.spaceS),
    ) {
        Box(
            Modifier
                .size(SwatchSize)
                .background(fill, MaterialTheme.shapes.extraSmall)
                .border(dimens.hairline, TracebackTheme.colors.edge, MaterialTheme.shapes.extraSmall),
        )
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            color = TracebackTheme.colors.textHigh,
            modifier = Modifier.width(SwatchLabelWidth),
        )
        Text(
            text = note,
            style = MaterialTheme.typography.bodySmall,
            color = TracebackTheme.colors.textDim,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StageRod(name: String, tint: Color) {
    val dimens = TracebackTheme.dimens
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.spaceS),
    ) {
        Box(
            Modifier
                .width(dimens.rodWidth)
                .height(RodHeight)
                .background(tint, PillShape),
        )
        Text(
            text = name.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = tint,
        )
    }
}

@Composable
private fun ShapeSample(name: String, shape: Shape) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceXxs),
    ) {
        Box(
            Modifier
                .size(ShapeSampleSize)
                .background(TracebackTheme.colors.glassStrong, shape)
                .border(TracebackTheme.dimens.hairline, TracebackTheme.colors.edgeHighlight, shape),
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = TracebackTheme.colors.textDim,
        )
    }
}

@Composable
private fun SpaceBar(name: String, width: Dp) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceXs),
    ) {
        Box(
            Modifier
                .width(width)
                .height(TracebackTheme.dimens.conduitHeight)
                .background(TracebackTheme.colors.accent, PillShape),
        )
        Text(
            text = "$name  ${width.value.toInt()}dp",
            style = MaterialTheme.typography.bodySmall,
            color = TracebackTheme.colors.textDim,
        )
    }
}

@Composable
fun TokenSheet(modifier: Modifier = Modifier) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val motion = TracebackTheme.motion

    Box(modifier.fillMaxSize()) {
        AuroraBackdrop(Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(SectionGap),
        ) {
            Text(
                text = "Aurora tokens",
                style = MaterialTheme.typography.titleLarge,
                color = colors.textHigh,
            )

            Section("Surface") {
                SwatchRow("ground", colors.ground, "page, behind the light")
                SwatchRow("glass", colors.glass, "card fill")
                SwatchRow("glassStrong", colors.glassStrong, "sheet, input")
                SwatchRow("edge", colors.edge, "hairline")
                SwatchRow("edgeHighlight", colors.edgeHighlight, "inner top edge")
                SwatchRow("track", colors.track, "unfilled conduit")
            }

            Section("Aurora") {
                SwatchRow("auroraIndigo", colors.auroraIndigo, "upper left field")
                SwatchRow("auroraTeal", colors.auroraTeal, "right field")
                SwatchRow("auroraWarm", colors.auroraWarm, "lower field")
            }

            Section("Text") {
                Text("textHigh - values and titles - 12:1 floor", style = MaterialTheme.typography.bodyMedium, color = colors.textHigh)
                Text("textDim - labels and meta - 7:1 floor", style = MaterialTheme.typography.bodyMedium, color = colors.textDim)
                Text("textFaint - third rank - 4.5:1 floor", style = MaterialTheme.typography.bodyMedium, color = colors.textFaint)
            }

            Section("Accent") {
                SwatchRow("accent", colors.accent, "FAB and one primary action")
                SwatchRow("accentText", colors.accentText, "accent read as text")
                SwatchRow("accentDim", colors.accentDim, "selected wash")
                Box(
                    Modifier
                        .background(colors.accent, FabShape)
                        .padding(horizontal = dimens.spaceM, vertical = dimens.spaceS),
                ) {
                    Text("onAccent", style = MaterialTheme.typography.labelLarge, color = colors.onAccent)
                }
            }

            Section("Stage") {
                Row(horizontalArrangement = Arrangement.spacedBy(dimens.spaceL)) {
                    Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceXs)) {
                        StageRod("draft", colors.stageDraft)
                        StageRod("applied", colors.stageApplied)
                        StageRod("in conversation", colors.stageInConversation)
                        StageRod("interview", colors.stageInterview)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceXs)) {
                        StageRod("hired", colors.stageHired)
                        StageRod("delivered", colors.stageDelivered)
                        StageRod("closed", colors.stageClosed)
                        StageRod("lost", colors.stageLost)
                    }
                }
            }

            Section("Type") {
                Text("titleLarge 26", style = MaterialTheme.typography.titleLarge, color = colors.textHigh)
                Text("titleMedium 20", style = MaterialTheme.typography.titleMedium, color = colors.textHigh)
                Text("titleSmall 13", style = MaterialTheme.typography.titleSmall, color = colors.textHigh)
                Text("bodyLarge 14 - default", style = MaterialTheme.typography.bodyLarge, color = colors.textDim)
                Text("bodyMedium 12.5 - 0123456789", style = MaterialTheme.typography.bodyMedium, color = colors.textDim)
                Text("bodySmall 11.5 - 0123456789", style = MaterialTheme.typography.bodySmall, color = colors.textDim)
                Text("labelLarge 13", style = MaterialTheme.typography.labelLarge, color = colors.textHigh)
                Text("labelMedium 10".uppercase(), style = MaterialTheme.typography.labelMedium, color = colors.textDim)
                Text("labelSmall 9.5".uppercase(), style = MaterialTheme.typography.labelSmall, color = colors.textDim)
            }

            Section("Shape") {
                Row(horizontalArrangement = Arrangement.spacedBy(dimens.spaceS)) {
                    ShapeSample("xs 8", MaterialTheme.shapes.extraSmall)
                    ShapeSample("s 12", MaterialTheme.shapes.small)
                    ShapeSample("m 18", MaterialTheme.shapes.medium)
                    ShapeSample("l 26", MaterialTheme.shapes.large)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(dimens.spaceS)) {
                    ShapeSample("fab 19", FabShape)
                    ShapeSample("pill 7", PillShape)
                    ShapeSample("button 12", ButtonShape)
                }
            }

            Section("Space") {
                SpaceBar("spaceXxs", dimens.spaceXxs)
                SpaceBar("spaceXs", dimens.spaceXs)
                SpaceBar("spaceS", dimens.spaceS)
                SpaceBar("spaceM", dimens.spaceM)
                SpaceBar("spaceL", dimens.spaceL)
                SpaceBar("spaceXl", dimens.spaceXl)
                SpaceBar("screenPadding", dimens.screenPadding)
            }

            Section("Measure") {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(dimens.spaceM),
                ) {
                    Box(
                        Modifier
                            .size(dimens.fabSize)
                            .background(colors.accent, FabShape),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceXxs)) {
                        Text("fabSize ${dimens.fabSize.value.toInt()}dp / glow ${dimens.fabGlow.value.toInt()}dp", style = MaterialTheme.typography.bodySmall, color = colors.textDim)
                        Text("rodWidth ${dimens.rodWidth.value.toInt()}dp / glow ${dimens.rodGlow.value.toInt()}dp", style = MaterialTheme.typography.bodySmall, color = colors.textDim)
                        Text("hairline ${dimens.hairline.value.toInt()}dp / touchTarget ${dimens.touchTarget.value.toInt()}dp", style = MaterialTheme.typography.bodySmall, color = colors.textDim)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.conduitGap),
                ) {
                    listOf(colors.stageApplied, colors.stageApplied, colors.track, colors.track).forEach { segment ->
                        Box(
                            Modifier
                                .weight(1f)
                                .height(dimens.conduitHeight)
                                .background(segment, PillShape),
                        )
                    }
                }
                Text(
                    "conduitHeight ${dimens.conduitHeight.value.toInt()}dp / gap ${dimens.conduitGap.value.toInt()}dp",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textDim,
                )
            }

            Section("Motion") {
                Text("fast ${motion.fast}ms - selection, colour, alpha", style = MaterialTheme.typography.bodySmall, color = colors.textDim)
                Text("medium ${motion.medium}ms - enter, exit, expand", style = MaterialTheme.typography.bodySmall, color = colors.textDim)
                Text("slow ${motion.slow}ms - sheets, stage change", style = MaterialTheme.typography.bodySmall, color = colors.textDim)
                Text("stageBloom ${motion.stageBloom}ms - one breath of light", style = MaterialTheme.typography.bodySmall, color = colors.textDim)
                Text("ambient ${motion.ambient}ms - one aurora pass", style = MaterialTheme.typography.bodySmall, color = colors.textDim)
                Text("standardEasing cubic-bezier(0.2, 0.7, 0.2, 1)", style = MaterialTheme.typography.bodySmall, color = colors.textDim)
                Text("pressSpring damping 0.55 / stiffness medium-low", style = MaterialTheme.typography.bodySmall, color = colors.textDim)
            }
        }
    }
}

@Preview(name = "Tokens - dark", widthDp = 380, heightDp = 1800)
@Composable
private fun TokenSheetDarkPreview() {
    TracebackTheme(darkTheme = true) { TokenSheet() }
}

@Preview(name = "Tokens - light", widthDp = 380, heightDp = 1800)
@Composable
private fun TokenSheetLightPreview() {
    TracebackTheme(darkTheme = false) { TokenSheet() }
}
