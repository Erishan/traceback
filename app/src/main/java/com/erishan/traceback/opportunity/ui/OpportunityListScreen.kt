package com.erishan.traceback.opportunity.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.R
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.ui.components.ChoiceChip
import com.erishan.traceback.ui.components.EmptyState
import com.erishan.traceback.ui.components.GlowFab
import com.erishan.traceback.ui.components.LoadingState
import com.erishan.traceback.ui.components.StageRod
import com.erishan.traceback.ui.components.TbBarIconButton
import com.erishan.traceback.ui.components.TbGlassSurface
import com.erishan.traceback.ui.components.TbScaffold
import com.erishan.traceback.ui.theme.PillShape
import com.erishan.traceback.ui.theme.TracebackTheme
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Instant

private val StripHeight = 5.dp
private val StripGap = 3.dp
private val StripBloomReach = 7.dp
private const val StripBloomAlpha = 0.30f
private const val StripTerminalAlpha = 0.35f
private const val StripLegendCount = 3
private val LegendDot = 6.dp

private val MetaDot = 5.dp
private const val StagePillFill = 0.14f
private const val StagePillEdge = 0.30f

private val ListBottomInset = 104.dp

@Composable
fun OpportunityListScreen(
    uiState: OpportunityListUiState,
    onAddClick: () -> Unit,
    onFilterSelected: (OpportunityFilter) -> Unit,
    onOpenOpportunity: (String) -> Unit,
    onOpenMe: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimens = TracebackTheme.dimens

    TbScaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            GlowFab(
                onClick = onAddClick,
                contentDescription = stringResource(R.string.new_opportunity),
                icon = Icons.Default.Add,
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimens.screenPadding)
        ) {
            Spacer(Modifier.height(dimens.spaceS))
            ListHeader(distribution = uiState.distribution, onOpenMe = onOpenMe)

            if (!uiState.isLoading && !uiState.distribution.isEmpty) {
                Spacer(Modifier.height(dimens.spaceM))
                SignalStrip(uiState.distribution)
            }

            Spacer(Modifier.height(dimens.spaceM))
            FilterRow(selected = uiState.selectedFilter, onSelect = onFilterSelected)
            Spacer(Modifier.height(dimens.spaceS))

            when {
                uiState.isLoading -> LoadingState()

                uiState.opportunities.isEmpty() -> EmptyState(
                    title = stringResource(
                        if (uiState.distribution.isEmpty) R.string.empty_opportunities_list_title
                        else R.string.empty_filter_title
                    ),
                    message = stringResource(
                        if (uiState.distribution.isEmpty) R.string.empty_opportunities_list_message
                        else R.string.empty_filter_message
                    ),
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = ListBottomInset),
                    verticalArrangement = Arrangement.spacedBy(dimens.spaceS),
                ) {
                    items(items = uiState.opportunities, key = { it.id }) { opportunity ->
                        OpportunityCard(
                            opportunity = opportunity,
                            onClick = { onOpenOpportunity(opportunity.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ListHeader(distribution: StageDistribution, onOpenMe: () -> Unit) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.pipeline_overline).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.accentText,
            )
            Spacer(Modifier.height(dimens.spaceXxs))
            Text(
                text = stringResource(R.string.opportunities_title),
                style = MaterialTheme.typography.titleLarge,
                color = colors.textHigh,
            )
            Spacer(Modifier.height(dimens.spaceXxs))
            Text(
                text = countLine(distribution),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textDim,
            )
        }
        Spacer(Modifier.width(dimens.spaceS))
        TbBarIconButton(
            icon = Icons.Outlined.Person,
            contentDescription = stringResource(R.string.cd_open_me),
            onClick = onOpenMe,
        )
    }
}

private val NumeralRun = Regex("\\p{Nd}+")

@Composable
private fun countLine(distribution: StageDistribution): AnnotatedString {
    val text = stringResource(
        R.string.opportunities_count,
        distribution.total,
        distribution.active,
    )
    val bright = SpanStyle(color = TracebackTheme.colors.textHigh)
    return buildAnnotatedString {
        append(text)
        NumeralRun.findAll(text).forEach { addStyle(bright, it.range.first, it.range.last + 1) }
    }
}

@Composable
private fun SignalStrip(distribution: StageDistribution, modifier: Modifier = Modifier) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val segments = distribution.present.map { entry -> entry to stageColor(entry.stage) }
    val total = distribution.total.toFloat()

    Column(modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(StripHeight + StripBloomReach * 2)
        ) {
            if (segments.isEmpty()) return@Canvas

            val reach = StripBloomReach.toPx()
            val centerY = size.height / 2f

            val gap = minOf(StripGap.toPx(), size.width / (segments.size * 2f))
            val room = (size.width - gap * (segments.size - 1)).coerceAtLeast(0f)
            val cap = minOf(StripHeight.toPx(), room / segments.size)
            val slack = (room - cap * segments.size).coerceAtLeast(0f)
            val radius = cap / 2f

            var x = 0f
            segments.forEach { (entry, color) ->
                val width = cap + slack * (entry.count / total)
                if (entry.stage.isTerminal) {
                    drawSegment(color.copy(alpha = color.alpha * StripTerminalAlpha), x, width, centerY, cap, radius)
                } else {
                    drawSegmentBloom(color, x, width, centerY, reach)
                    drawSegment(color, x, width, centerY, cap, radius)
                }
                x += width + gap
            }
        }

        Spacer(Modifier.height(dimens.spaceXs))

        Row(horizontalArrangement = Arrangement.spacedBy(dimens.spaceM)) {
            distribution.busiest(StripLegendCount).forEach { entry ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.spaceXxs),
                ) {
                    Box(
                        Modifier
                            .size(LegendDot)
                            .clip(CircleShape)
                            .background(stageColor(entry.stage))
                    )
                    Text(
                        text = stringResource(stageLabelRes(entry.stage)).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textFaint,
                        maxLines = 1,
                    )
                    Text(
                        text = entry.count.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textDim,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawSegment(
    color: Color,
    left: Float,
    width: Float,
    centerY: Float,
    thickness: Float,
    radius: Float,
) {
    drawLine(
        color = color,
        start = Offset(left + radius, centerY),
        end = Offset(left + width - radius, centerY),
        strokeWidth = thickness,
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawSegmentBloom(
    color: Color,
    left: Float,
    width: Float,
    centerY: Float,
    reach: Float,
) {
    drawRect(
        brush = Brush.verticalGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                0.5f to color.copy(alpha = StripBloomAlpha),
                1f to Color.Transparent,
            ),
            startY = centerY - reach,
            endY = centerY + reach,
        ),
        topLeft = Offset(left, centerY - reach),
        size = Size(width, reach * 2f),
    )
}

@Composable
private fun FilterRow(selected: OpportunityFilter, onSelect: (OpportunityFilter) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceXs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OpportunityFilter.entries.forEach { filter ->
            ChoiceChip(
                label = stringResource(filter.labelRes).uppercase(),
                selected = filter == selected,
                onClick = { onSelect(filter) },
            )
        }
    }
}

@Composable
private fun OpportunityCard(opportunity: Opportunity, onClick: () -> Unit) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val shape = MaterialTheme.shapes.medium

    TbGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            StageRod(color = stageColor(opportunity.pipelineStage))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimens.spaceM, vertical = dimens.spaceS)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = opportunity.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textHigh,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(dimens.spaceXs))
                    StagePill(opportunity.pipelineStage)
                }

                if (opportunity.description != null) {
                    Spacer(Modifier.height(dimens.spaceXxs))
                    Text(
                        text = opportunity.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textDim,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(Modifier.height(dimens.spaceS))
                CardMeta(opportunity)
            }
        }
    }
}

@Composable
private fun StagePill(stage: PipelineStage) {
    val color = stageColor(stage)
    Box(
        modifier = Modifier
            .clip(PillShape)
            .background(color.copy(alpha = StagePillFill))
            .border(TracebackTheme.dimens.hairline, color.copy(alpha = StagePillEdge), PillShape)
            .padding(
                horizontal = TracebackTheme.dimens.spaceXs,
                vertical = TracebackTheme.dimens.spaceXxs,
            )
    ) {
        Text(
            text = stringResource(stageLabelRes(stage)).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            maxLines = 1,
        )
    }
}

@Composable
private fun CardMeta(opportunity: Opportunity) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceXxs),
        ) {
            Box(
                Modifier
                    .size(MetaDot)
                    .clip(CircleShape)
                    .background(colors.accent)
            )
            Text(
                text = sourceText(opportunity).uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = colors.textFaint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(dimens.spaceXs))
        Text(
            text = createdText(opportunity.createdAt),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = colors.textFaint,
            maxLines = 1,
        )
    }
}

@Composable
private fun sourceText(opportunity: Opportunity): String =
    opportunity.sourceLabel?.takeIf { opportunity.source == OpportunitySource.OTHER }
        ?: stringResource(sourceLabelRes(opportunity.source))

private val ListDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)

@Composable
private fun createdText(instant: Instant?): String =
    instant?.let {
        val platform = java.time.Instant.ofEpochMilli(it.toEpochMilliseconds())
        ListDateFormatter.format(java.time.LocalDateTime.ofInstant(platform, ZoneId.systemDefault()))
    } ?: stringResource(R.string.date_unknown)

// previews

private val PreviewCreatedAt = Instant.fromEpochMilliseconds(1_723_600_000_000L)

private fun previewOpportunity(
    id: String,
    title: String,
    description: String?,
    source: OpportunitySource,
    stage: PipelineStage,
) = Opportunity(
    id = id,
    title = title,
    description = description,
    source = source,
    sourceLabel = null,
    pipelineStage = stage,
    createdAt = PreviewCreatedAt,
    notes = emptyList(),
    appliedMessage = null,
)

private val PreviewOpportunities = listOf(
    previewOpportunity(
        id = "1",
        title = "SaaS onboarding flow redesign",
        description = "Rework the multi-step signup, reduce mobile drop-off.",
        source = OpportunitySource.UPWORK,
        stage = PipelineStage.APPLIED,
    ),
    previewOpportunity(
        id = "2",
        // Long title, no description: the two ways a card breaks.
        title = "Compose migration for a fintech dashboard with twelve legacy XML screens and a custom charting layer",
        description = null,
        source = OpportunitySource.LINKEDIN,
        stage = PipelineStage.IN_CONVERSATION,
    ),
    previewOpportunity(
        id = "3",
        title = "Crash triage for release build",
        description = "R8-only NPE on startup. Needs mapping-file analysis.",
        source = OpportunitySource.REFERRAL,
        stage = PipelineStage.INTERVIEW,
    ),
    previewOpportunity(
        id = "4",
        title = "Offline sync for a field-inspection app",
        description = null,
        source = OpportunitySource.OTHER,
        stage = PipelineStage.HIRED,
    ),
    previewOpportunity(
        id = "5",
        title = "Wear OS companion for a running app",
        description = "Watch face plus a tile, sharing the phone's session store.",
        source = OpportunitySource.UPWORK,
        stage = PipelineStage.APPLIED,
    ),
    previewOpportunity(
        id = "6",
        title = "Kiosk build for a museum installation",
        description = "Went quiet after the second call.",
        source = OpportunitySource.LINKEDIN,
        stage = PipelineStage.LOST,
    ),
)

private val PreviewDistribution = StageDistribution(
    mapOf(
        PipelineStage.DRAFT to 1,
        PipelineStage.APPLIED to 5,
        PipelineStage.IN_CONVERSATION to 3,
        PipelineStage.INTERVIEW to 2,
        PipelineStage.HIRED to 1,
        PipelineStage.LOST to 4,
    )
)

@Composable
private fun ListScreenPreview(darkTheme: Boolean, uiState: OpportunityListUiState) {
    TracebackTheme(darkTheme = darkTheme) {
        OpportunityListScreen(
            uiState = uiState,
            onAddClick = {},
            onFilterSelected = {},
            onOpenOpportunity = {},
            onOpenMe = {},
        )
    }
}

@Composable
internal fun ListScreenShowcase(darkTheme: Boolean) {
    TracebackTheme(darkTheme = darkTheme, reducedMotion = true) {
        OpportunityListScreen(
            uiState = OpportunityListUiState(
                opportunities = PreviewOpportunities,
                distribution = PreviewDistribution,
                isLoading = false,
            ),
            onAddClick = {},
            onFilterSelected = {},
            onOpenOpportunity = {},
            onOpenMe = {},
        )
    }
}

@Preview(name = "dark", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListDarkPreview() = ListScreenShowcase(darkTheme = true)

@Preview(name = "light", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListLightPreview() = ListScreenShowcase(darkTheme = false)

@Preview(name = "empty dark", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListEmptyDarkPreview() {
    ListScreenPreview(darkTheme = true, uiState = OpportunityListUiState(isLoading = false))
}

@Preview(name = "empty light", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListEmptyLightPreview() {
    ListScreenPreview(darkTheme = false, uiState = OpportunityListUiState(isLoading = false))
}

/** Filter matched nothing, but the pipeline is not empty - the strip still has something to say. */
@Preview(name = "filtered empty", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListFilteredEmptyPreview() {
    ListScreenPreview(
        darkTheme = true,
        uiState = OpportunityListUiState(
            selectedFilter = OpportunityFilter.Won,
            distribution = PreviewDistribution,
            isLoading = false,
        ),
    )
}

@Preview(name = "loading", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListLoadingPreview() {
    ListScreenPreview(darkTheme = true, uiState = OpportunityListUiState())
}
