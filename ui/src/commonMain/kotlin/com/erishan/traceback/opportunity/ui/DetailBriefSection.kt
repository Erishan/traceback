package com.erishan.traceback.opportunity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.erishan.traceback.opportunity.domain.Approach
import com.erishan.traceback.opportunity.domain.JobBrief
import com.erishan.traceback.ui.BriefVerdictNo
import com.erishan.traceback.ui.BriefVerdictYes
import com.erishan.traceback.ui.briefFailureText
import com.erishan.traceback.ui.briefGateReasonText
import com.erishan.traceback.ui.components.ErrorBanner
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.TbGlassSurface
import com.erishan.traceback.ui.components.TextAction
import com.erishan.traceback.ui.durationBasisText
import com.erishan.traceback.ui.fitVerdictText
import com.erishan.traceback.ui.theme.ButtonShape
import com.erishan.traceback.ui.theme.PillShape
import com.erishan.traceback.ui.theme.Res
import com.erishan.traceback.ui.theme.*
import com.erishan.traceback.ui.theme.TracebackTheme
import org.jetbrains.compose.resources.stringResource

private val BriefSpinnerSize = 16.dp
private val BriefSpinnerStroke = 2.dp

private val SkeletonKeyHeight = 8.dp
private val SkeletonValueHeight = 15.dp
private val SkeletonSupportHeight = 9.dp

private const val BriefActionFillAlpha = 0.10f
private const val BriefActionEdgeAlpha = 0.38f

private const val SkeletonKeyWidthFraction = 0.34f
private const val SkeletonValueWidthFraction = 0.62f
private const val SkeletonSupportWidthFraction = 0.88f

private const val BoxSupportMaxLines = 2
private const val ApproachSummaryMaxLines = 2
private const val ProposalCollapsedMaxLines = 6
private const val SkeletonRows = 2

@Composable
internal fun BriefSection(
    content: OpportunityDetailUiState.Content,
    onBrief: () -> Unit,
    onUseProposalAsAppliedMessage: (String) -> Unit,
    onOpenMe: () -> Unit,
    enabled: Boolean,
) {
    val dimens = TracebackTheme.dimens
    val brief = content.aiBrief

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.spaceXs),
    ) {
        BriefHeader(
            hasBrief = brief != null,
            inFlight = content.briefInFlight,
            actionEnabled = content.briefActionEnabled,
            onBrief = onBrief,
        )

        if (content.briefFailed != null) {
            ErrorBanner(
                text = briefFailureText(content.briefFailed!!),
                actionText = stringResource(Res.string.action_try_again),
                onAction = onBrief.takeIf { content.briefActionEnabled },
            )
        }

        if (content.briefGateReason != null) {
            BriefGateCard(
                reason = content.briefGateReason,
                onOpenMe = onOpenMe,
                enabled = enabled,
            )
        }

        when {
            content.briefInFlight -> BriefSkeleton()

            brief != null -> BriefBoxes(
                brief = brief,
                onUseProposalAsAppliedMessage = onUseProposalAsAppliedMessage,
                enabled = enabled,
            )

            content.briefGateReason == null -> BriefEmptyCard()
        }
    }
}

@Composable
private fun BriefHeader(
    hasBrief: Boolean,
    inFlight: Boolean,
    actionEnabled: Boolean,
    onBrief: () -> Unit,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        FieldLabel(text = stringResource(Res.string.field_brief), spacer = false)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
        ) {
            if (inFlight) {
                CircularProgressIndicator(
                    modifier = Modifier.size(BriefSpinnerSize),
                    strokeWidth = BriefSpinnerStroke,
                    color = colors.accent,
                    trackColor = Color.Transparent,
                )
            }
            BriefActionButton(
                text = stringResource(
                    if (hasBrief) Res.string.action_brief_rerun else Res.string.action_brief
                ),
                enabled = actionEnabled,
                onClick = onBrief,
            )
        }
    }
}

@Composable
private fun BriefActionButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    TbGlassSurface(
        modifier = Modifier
            .minTouchTarget()
            .clip(ButtonShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        shape = ButtonShape,
        fill = if (enabled) colors.accent.copy(alpha = BriefActionFillAlpha) else null,
        edge = if (enabled) colors.accent.copy(alpha = BriefActionEdgeAlpha) else null,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) colors.accentText else colors.textFaint,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = dimens.spaceM),
        )
    }
}

@Composable
private fun BriefEmptyCard() {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.brief_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textFaint,
            modifier = Modifier.padding(horizontal = dimens.spaceM, vertical = dimens.spaceS),
        )
    }
}

@Composable
private fun BriefGateCard(
    reason: BriefGateReason,
    onOpenMe: () -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = dimens.spaceXs, vertical = dimens.spaceXs),
        ) {
            Text(
                text = briefGateReasonText(reason),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textFaint,
                modifier = Modifier.padding(horizontal = dimens.spaceXs),
            )
            TextAction(
                text = stringResource(Res.string.brief_open_me),
                color = colors.accentText,
                onClick = onOpenMe,
                enabled = enabled,
            )
        }
    }
}

// brief · boxes

@Composable
private fun BriefBoxes(
    brief: JobBrief,
    onUseProposalAsAppliedMessage: (String) -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceXs)) {
        BriefRow {
            BriefBox(
                label = stringResource(Res.string.field_fit),
                value = fitVerdictText(brief.fit.verdict),
                valueColor = fitVerdictColor(brief.fit.verdict),
                support = brief.fit.summary,
                modifier = Modifier.weight(1f),
            )
            BriefBox(
                label = stringResource(Res.string.field_price),
                value = stringResource(
                    Res.string.brief_price_range,
                    brief.price.low,
                    brief.price.high,
                ),
                valueColor = colors.textHigh,
                support = brief.price.rationale,
                modifier = Modifier.weight(1f),
            )
        }
        BriefRow {
            BriefBox(
                label = stringResource(Res.string.field_duration),
                value = stringResource(Res.string.brief_duration_hours, brief.duration.hours),
                valueColor = colors.textHigh,
                support = stringResource(
                    Res.string.brief_duration_support,
                    brief.duration.range,
                    durationBasisText(brief.duration.basis),
                ),
                modifier = Modifier.weight(1f),
            )
            ApproachBox(approach = brief.approach, modifier = Modifier.weight(1f))
        }
        ProposalCard(
            proposal = brief.proposal,
            onUseAsAppliedMessage = onUseProposalAsAppliedMessage,
            enabled = enabled,
        )
    }
}

@Composable
private fun BriefRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(TracebackTheme.dimens.spaceXs),
        content = content,
    )
}

@Composable
private fun BriefBox(
    label: String,
    value: String,
    valueColor: Color,
    support: String?,
    modifier: Modifier = Modifier,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.spaceS, vertical = dimens.spaceS),
        ) {
            FieldLabel(label)
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = valueColor,
                maxLines = BoxSupportMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
            if (support != null) {
                Spacer(Modifier.height(dimens.spaceXxs))
                Text(
                    text = support,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textDim,
                    maxLines = BoxSupportMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ApproachBox(approach: Approach, modifier: Modifier = Modifier) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.spaceS, vertical = dimens.spaceS),
        ) {
            FieldLabel(stringResource(Res.string.field_approach))
            Text(
                text = approach.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textHigh,
                maxLines = ApproachSummaryMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
            if (approach.technologies.isNotEmpty()) {
                Spacer(Modifier.height(dimens.spaceXxs))
                Text(
                    text = approach.technologies.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textDim,
                    maxLines = BoxSupportMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ProposalCard(
    proposal: String,
    onUseAsAppliedMessage: (String) -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    var expanded by remember { mutableStateOf(false) }
    var clipped by remember { mutableStateOf(false) }

    TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.spaceXs, vertical = dimens.spaceXs),
        ) {
            Column(modifier = Modifier.padding(horizontal = dimens.spaceXs)) {
                FieldLabel(stringResource(Res.string.field_proposal))
                Text(
                    text = proposal,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textHigh,
                    maxLines = if (expanded) Int.MAX_VALUE else ProposalCollapsedMaxLines,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { if (!expanded) clipped = it.hasVisualOverflow },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextAction(
                    text = stringResource(Res.string.brief_use_as_applied),
                    color = colors.textDim,
                    onClick = { onUseAsAppliedMessage(proposal) },
                    enabled = enabled,
                )
                if (clipped) {
                    TextAction(
                        text = stringResource(
                            if (expanded) Res.string.action_show_less else Res.string.action_show_more
                        ),
                        color = colors.textDim,
                        onClick = { expanded = !expanded },
                    )
                }
            }
        }
    }
}

// brief · loading

@Composable
private fun BriefSkeleton() {
    val dimens = TracebackTheme.dimens

    Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceXs)) {
        repeat(SkeletonRows) {
            BriefRow {
                SkeletonBox(Modifier.weight(1f))
                SkeletonBox(Modifier.weight(1f))
            }
        }
        SkeletonBox(Modifier.fillMaxWidth())
    }
}

// Every skeleton box holds the same fixed bars, so they line up without filling the row -
// and filling it under the screen's scrolling column would ask for an infinite height.
@Composable
private fun SkeletonBox(modifier: Modifier = Modifier) {
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.spaceS, vertical = dimens.spaceS),
            verticalArrangement = Arrangement.spacedBy(dimens.spaceXs),
        ) {
            SkeletonBar(widthFraction = SkeletonKeyWidthFraction, height = SkeletonKeyHeight)
            SkeletonBar(widthFraction = SkeletonValueWidthFraction, height = SkeletonValueHeight)
            SkeletonBar(
                widthFraction = SkeletonSupportWidthFraction,
                height = SkeletonSupportHeight,
            )
        }
    }
}

@Composable
private fun SkeletonBar(widthFraction: Float, height: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(PillShape)
            .background(TracebackTheme.colors.track)
    )
}

@Composable
private fun fitVerdictColor(verdict: String): Color = with(TracebackTheme.colors) {
    when (verdict) {
        BriefVerdictYes -> stageHired
        BriefVerdictNo -> stageLost
        else -> stageInConversation
    }
}
