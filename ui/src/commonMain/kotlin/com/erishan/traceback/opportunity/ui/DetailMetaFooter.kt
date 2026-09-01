package com.erishan.traceback.opportunity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.ui.components.ChoiceChip
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.TbGlassSurface
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.label
import com.erishan.traceback.ui.platform.formatDetailTimestamp
import com.erishan.traceback.ui.theme.PillShape
import com.erishan.traceback.ui.theme.Res
import com.erishan.traceback.ui.theme.*
import com.erishan.traceback.ui.theme.TracebackTheme
import kotlin.time.Instant
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MetaFooter(
    source: OpportunitySource,
    sourceLabel: String?,
    createdAt: Instant?,
    onSourceClick: () -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    Column(Modifier.fillMaxWidth()) {
        HorizontalDivider(thickness = dimens.hairline, color = colors.edge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.spaceXxs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SourcePill(
                source = source,
                sourceLabel = sourceLabel,
                onClick = onSourceClick,
                enabled = enabled,
            )
            Spacer(Modifier.width(dimens.spaceS))
            Text(
                text = formatTimestampOrUnknown(createdAt),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textFaint,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SourcePill(
    source: OpportunitySource,
    sourceLabel: String?,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    val label = sourceLabel?.takeIf { source == OpportunitySource.OTHER && it.isNotBlank() }
        ?: source.label()
    val changeSource = stringResource(Res.string.cd_change_source)

    Box(
        modifier = Modifier
            .minTouchTarget()
            .clip(PillShape)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClickLabel = changeSource,
                onClick = onClick,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        TbGlassSurface(
            shape = PillShape,
            strong = true,
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textDim,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(
                    horizontal = dimens.spaceXs,
                    vertical = dimens.spaceXxs,
                ),
            )
        }
    }
}

@Composable
internal fun SourcePicker(
    source: OpportunitySource,
    sourceLabel: String?,
    onSourceChange: (OpportunitySource) -> Unit,
    onSourceLabelChange: (String) -> Unit,
    enabled: Boolean,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens
    Column {
        Spacer(Modifier.height(dimens.spaceS))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
        ) {
            OpportunitySource.entries.forEach { entry ->
                ChoiceChip(
                    label = entry.label().uppercase(),
                    selected = source == entry,
                    onClick = { onSourceChange(entry) },
                    selectionColor = colors.textHigh,
                    selectedFill = colors.glassStrong,
                    enabled = enabled,
                )
            }
        }
        AnimatedVisibility(visible = source == OpportunitySource.OTHER) {
            Column {
                Spacer(Modifier.height(dimens.spaceS))
                FieldLabel(stringResource(Res.string.field_source_label))
                TbTextField(
                    value = sourceLabel.orEmpty(),
                    onValueChange = onSourceLabelChange,
                    placeholder = stringResource(Res.string.create_source_label_hint),
                    enabled = enabled,
                )
            }
        }
    }
}

@Composable
internal fun formatTimestampOrUnknown(instant: Instant?): String =
    instant?.let { formatDetailTimestamp(it) } ?: stringResource(Res.string.date_unknown)
