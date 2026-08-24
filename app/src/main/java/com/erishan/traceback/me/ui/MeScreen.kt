package com.erishan.traceback.me.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erishan.traceback.R
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.LoadingState
import com.erishan.traceback.ui.components.TbScaffold
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.theme.ButtonShape
import com.erishan.traceback.ui.theme.TracebackTheme

@Composable
fun MeScreen(
    uiState: MeUiState,
    onBack: () -> Unit,
    onSaveProfile: (about: String, rateBand: String, pace: String) -> Unit,
    onSaveKey: (String) -> Unit,
    onClearKey: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TbScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(R.string.me_title),
        navigationIcon = {
            IconButton(
                onClick = onBack,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = Color.Transparent,
                ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                )
            }
        },
    ) { innerPadding ->
        if (!uiState.isLoaded) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) { LoadingState() }
        } else {
            MeForm(
                uiState = uiState,
                contentPaddingModifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onSaveProfile = onSaveProfile,
                onSaveKey = onSaveKey,
                onClearKey = onClearKey,
            )
        }
    }
}

@Composable
private fun MeForm(
    uiState: MeUiState,
    contentPaddingModifier: Modifier,
    onSaveProfile: (about: String, rateBand: String, pace: String) -> Unit,
    onSaveKey: (String) -> Unit,
    onClearKey: () -> Unit,
) {
    var about by remember { mutableStateOf(uiState.about) }
    var rateBand by remember { mutableStateOf(uiState.rateBand.orEmpty()) }
    var pace by remember { mutableStateOf(uiState.pace.orEmpty()) }
    var keyDraft by remember { mutableStateOf("") }
    var profileDirty by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.about, uiState.rateBand, uiState.pace, profileDirty) {
        if (!profileDirty) {
            about = uiState.about
            rateBand = uiState.rateBand.orEmpty()
            pace = uiState.pace.orEmpty()
        }
    }

    LaunchedEffect(uiState.hasKey) {
        if (uiState.hasKey) keyDraft = ""
    }

    val profileEnabled = !uiState.isSavingProfile
    val keyEnabled = !uiState.isSavingKey

    Column(
        modifier = contentPaddingModifier
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 18.dp),
    ) {
        Spacer(Modifier.height(12.dp))
        FieldLabel(stringResource(R.string.field_about))
        TbTextField(
            value = about,
            onValueChange = {
                about = it
                profileDirty = true
            },
            placeholder = stringResource(R.string.me_about_hint),
            singleLine = false,
            minLines = 3,
            maxLines = 8,
            imeAction = ImeAction.Default,
            enabled = profileEnabled,
        )
        Spacer(Modifier.height(14.dp))

        FieldLabel(
            text = stringResource(R.string.field_rate_band),
            trailing = stringResource(R.string.field_optional),
        )
        TbTextField(
            value = rateBand,
            onValueChange = {
                rateBand = it
                profileDirty = true
            },
            placeholder = stringResource(R.string.me_rate_band_hint),
            imeAction = ImeAction.Next,
            enabled = profileEnabled,
        )
        Spacer(Modifier.height(14.dp))

        FieldLabel(
            text = stringResource(R.string.field_pace),
            trailing = stringResource(R.string.field_optional),
        )
        TbTextField(
            value = pace,
            onValueChange = {
                pace = it
                profileDirty = true
            },
            placeholder = stringResource(R.string.me_pace_hint),
            imeAction = ImeAction.Done,
            enabled = profileEnabled,
        )
        Spacer(Modifier.height(18.dp))

        if (uiState.profileSaveFailed) {
            ErrorBanner(text = stringResource(R.string.me_profile_could_not_save))
            Spacer(Modifier.height(12.dp))
        }

        Button(
            onClick = { onSaveProfile(about, rateBand, pace) },
            enabled = profileEnabled,
            modifier = Modifier.fillMaxWidth(),
            shape = ButtonShape,
        ) {
            Text(stringResource(R.string.action_save_profile))
        }

        Spacer(Modifier.height(28.dp))

        FieldLabel(stringResource(R.string.field_openai_key))
        if (uiState.hasKey) {
            Text(
                text = stringResource(R.string.me_key_saved, uiState.lastFour.orEmpty()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            TextButton(
                onClick = onClearKey,
                enabled = keyEnabled,
            ) {
                Text(stringResource(R.string.action_clear_key))
            }
        } else {
            TbTextField(
                value = keyDraft,
                onValueChange = { keyDraft = it },
                placeholder = stringResource(R.string.me_key_hint),
                enabled = keyEnabled,
                visualTransformation = PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password,
                capitalization = KeyboardCapitalization.None,
            )
            Spacer(Modifier.height(12.dp))
            if (uiState.keyRejectedBlank) {
                ErrorBanner(text = stringResource(R.string.me_key_blank))
                Spacer(Modifier.height(12.dp))
            }
            Button(
                onClick = { onSaveKey(keyDraft) },
                enabled = keyEnabled && keyDraft.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = ButtonShape,
            ) {
                Text(stringResource(R.string.action_save_key))
            }
        }

        if (uiState.keySaveFailed) {
            Spacer(Modifier.height(12.dp))
            ErrorBanner(text = stringResource(R.string.me_key_could_not_save))
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ErrorBanner(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0B0D, widthDp = 360)
@Composable
private fun MeScreenPreview() {
    TracebackTheme {
        MeScreen(
            uiState = MeUiState(
                about = "Android + Compose. No blockchain. Direct, short emails.",
                rateBand = "mid",
                pace = "one client at a time",
                hasKey = true,
                lastFour = "ABCD",
                isLoaded = true,
            ),
            onBack = {},
            onSaveProfile = { _, _, _ -> },
            onSaveKey = {},
            onClearKey = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0B0D, widthDp = 360)
@Composable
private fun MeScreenEmptyKeyPreview() {
    TracebackTheme {
        MeScreen(
            uiState = MeUiState(isLoaded = true),
            onBack = {},
            onSaveProfile = { _, _, _ -> },
            onSaveKey = {},
            onClearKey = {},
        )
    }
}
