package com.erishan.traceback.me.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.erishan.traceback.R
import com.erishan.traceback.ui.components.ErrorBanner
import com.erishan.traceback.ui.components.FieldLabel
import com.erishan.traceback.ui.components.LoadingState
import com.erishan.traceback.ui.components.PrimaryButton
import com.erishan.traceback.ui.components.TbBarIconButton
import com.erishan.traceback.ui.components.TbGlassSurface
import com.erishan.traceback.ui.components.TbScaffold
import com.erishan.traceback.ui.components.TbTextField
import com.erishan.traceback.ui.components.TextAction
import com.erishan.traceback.ui.theme.TracebackTheme

private const val AboutMinLines = 3
private const val AboutMaxLines = 8

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
            TbBarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
                onClick = onBack,
            )
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
                innerPadding = innerPadding,
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
    innerPadding: PaddingValues,
    onSaveProfile: (about: String, rateBand: String, pace: String) -> Unit,
    onSaveKey: (String) -> Unit,
    onClearKey: () -> Unit,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

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
        modifier = Modifier
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding())
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimens.screenPadding),
    ) {
        Spacer(Modifier.height(dimens.spaceXs))

        Text(
            text = stringResource(R.string.me_gate_hint),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textFaint,
        )

        Spacer(Modifier.height(dimens.spaceL))

        FieldLabel(stringResource(R.string.field_profile))
        ProfileCard(
            about = about,
            rateBand = rateBand,
            pace = pace,
            enabled = profileEnabled,
            saving = uiState.isSavingProfile,
            saveFailed = uiState.profileSaveFailed,
            onAboutChange = {
                about = it
                profileDirty = true
            },
            onRateBandChange = {
                rateBand = it
                profileDirty = true
            },
            onPaceChange = {
                pace = it
                profileDirty = true
            },
            onSave = { onSaveProfile(about, rateBand, pace) },
        )

        Spacer(Modifier.height(dimens.spaceL))

        FieldLabel(stringResource(R.string.field_openai_key))
        KeyCard(
            uiState = uiState,
            keyDraft = keyDraft,
            enabled = keyEnabled,
            onKeyDraftChange = { keyDraft = it },
            onSaveKey = { onSaveKey(keyDraft) },
            onClearKey = onClearKey,
        )

        Spacer(Modifier.height(dimens.spaceXl))
    }
}

@Composable
private fun ProfileCard(
    about: String,
    rateBand: String,
    pace: String,
    enabled: Boolean,
    saving: Boolean,
    saveFailed: Boolean,
    onAboutChange: (String) -> Unit,
    onRateBandChange: (String) -> Unit,
    onPaceChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = dimens.spaceM, vertical = dimens.spaceS)) {
            FieldLabel(stringResource(R.string.field_about))
            TbTextField(
                value = about,
                onValueChange = onAboutChange,
                placeholder = stringResource(R.string.me_about_hint),
                singleLine = false,
                minLines = AboutMinLines,
                maxLines = AboutMaxLines,
                imeAction = ImeAction.Default,
                enabled = enabled,
            )
            Spacer(Modifier.height(dimens.spaceS))

            FieldLabel(
                text = stringResource(R.string.field_rate_band),
                trailing = stringResource(R.string.field_optional),
            )
            TbTextField(
                value = rateBand,
                onValueChange = onRateBandChange,
                placeholder = stringResource(R.string.me_rate_band_hint),
                imeAction = ImeAction.Next,
                enabled = enabled,
            )
            Spacer(Modifier.height(dimens.spaceS))

            FieldLabel(
                text = stringResource(R.string.field_pace),
                trailing = stringResource(R.string.field_optional),
            )
            TbTextField(
                value = pace,
                onValueChange = onPaceChange,
                placeholder = stringResource(R.string.me_pace_hint),
                imeAction = ImeAction.Done,
                enabled = enabled,
            )
            Spacer(Modifier.height(dimens.spaceM))

            if (saveFailed) {
                ErrorBanner(text = stringResource(R.string.me_profile_could_not_save))
                Spacer(Modifier.height(dimens.spaceS))
            }

            PrimaryButton(
                text = stringResource(R.string.action_save_profile),
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                busy = saving,
            )
        }
    }
}

@Composable
private fun KeyCard(
    uiState: MeUiState,
    keyDraft: String,
    enabled: Boolean,
    onKeyDraftChange: (String) -> Unit,
    onSaveKey: () -> Unit,
    onClearKey: () -> Unit,
) {
    val colors = TracebackTheme.colors
    val dimens = TracebackTheme.dimens

    TbGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = dimens.spaceM, vertical = dimens.spaceS)) {
            if (uiState.hasKey) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.spaceXs),
                ) {
                    Text(
                        text = stringResource(R.string.me_key_saved, uiState.lastFour.orEmpty()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textHigh,
                        modifier = Modifier.weight(1f),
                    )
                    TextAction(
                        text = stringResource(R.string.action_clear_key),
                        color = colors.textDim,
                        onClick = onClearKey,
                        enabled = enabled,
                    )
                }
            } else {
                TbTextField(
                    value = keyDraft,
                    onValueChange = onKeyDraftChange,
                    placeholder = stringResource(R.string.me_key_hint),
                    enabled = enabled,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardType = KeyboardType.Password,
                    capitalization = KeyboardCapitalization.None,
                )
                Spacer(Modifier.height(dimens.spaceS))

                if (uiState.keyRejectedBlank) {
                    ErrorBanner(text = stringResource(R.string.me_key_blank))
                    Spacer(Modifier.height(dimens.spaceS))
                }

                PrimaryButton(
                    text = stringResource(R.string.action_save_key),
                    onClick = onSaveKey,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled && keyDraft.isNotBlank(),
                    busy = uiState.isSavingKey,
                )
            }

            if (uiState.keySaveFailed) {
                Spacer(Modifier.height(dimens.spaceS))
                ErrorBanner(text = stringResource(R.string.me_key_could_not_save))
            }
        }
    }
}

private val SavedKeyState = MeUiState(
    about = "Android + Compose. No blockchain. Direct, short emails.",
    rateBand = "mid",
    pace = "one client at a time",
    hasKey = true,
    lastFour = "4H2K",
    isLoaded = true,
)

private val NoKeyState = MeUiState(isLoaded = true)

@Composable
private fun MeScreenPreview(darkTheme: Boolean, uiState: MeUiState) {
    TracebackTheme(darkTheme = darkTheme) {
        MeScreen(
            uiState = uiState,
            onBack = {},
            onSaveProfile = { _, _, _ -> },
            onSaveKey = {},
            onClearKey = {},
        )
    }
}

@Preview(name = "key saved - dark", widthDp = 400, heightDp = 880)
@Composable
private fun MeScreenSavedKeyDarkPreview() = MeScreenPreview(true, SavedKeyState)

@Preview(name = "key saved - light", widthDp = 400, heightDp = 880)
@Composable
private fun MeScreenSavedKeyLightPreview() = MeScreenPreview(false, SavedKeyState)

@Preview(name = "no key - dark", widthDp = 400, heightDp = 880)
@Composable
private fun MeScreenNoKeyDarkPreview() = MeScreenPreview(true, NoKeyState)

@Preview(name = "no key - light", widthDp = 400, heightDp = 880)
@Composable
private fun MeScreenNoKeyLightPreview() = MeScreenPreview(false, NoKeyState)
