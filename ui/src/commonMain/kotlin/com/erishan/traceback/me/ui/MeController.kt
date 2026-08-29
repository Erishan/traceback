package com.erishan.traceback.me.ui

import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.ai.domain.trimmedOpenAiKey
import com.erishan.traceback.me.domain.UserContext
import com.erishan.traceback.me.domain.UserContextRepository
import com.erishan.traceback.settings.domain.AppearanceStore
import com.erishan.traceback.settings.domain.ThemeMode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MeController(
    private val scope: CoroutineScope,
    private val userContextRepository: UserContextRepository,
    private val secretStore: SecretStore,
    private val appearanceStore: AppearanceStore,
) {
    private val status = MutableStateFlow(MeStatus())

    val uiState: StateFlow<MeUiState> =
        combine(
            userContextRepository.observe(),
            secretStore.observe(),
            appearanceStore.observe(),
            status,
        ) { profile, key, themeMode, currentStatus ->
            MeUiState(
                about = profile.about,
                rateBand = profile.rateBand,
                pace = profile.pace,
                hasKey = key.hasKey,
                lastFour = key.lastFour,
                isLoaded = true,
                isSavingProfile = currentStatus.isSavingProfile,
                isSavingKey = currentStatus.isSavingKey,
                profileSaveFailed = currentStatus.profileSaveFailed,
                keySaveFailed = currentStatus.keySaveFailed,
                keyRejectedBlank = currentStatus.keyRejectedBlank,
                themeMode = themeMode,
            )
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MeUiState(),
        )

    fun onSaveProfile(about: String, rateBand: String, pace: String) {
        scope.launch {
            var failed = false
            status.update { it.copy(isSavingProfile = true, profileSaveFailed = false) }
            try {
                userContextRepository.save(
                    UserContext(
                        about = about.trim(),
                        rateBand = rateBand.trim().takeIf { it.isNotEmpty() },
                        pace = pace.trim().takeIf { it.isNotEmpty() },
                    )
                )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                failed = true
            } finally {
                status.update { it.copy(isSavingProfile = false, profileSaveFailed = failed) }
            }
        }
    }

    fun onSaveKey(value: String) {
        val trimmed = trimmedOpenAiKey(value)
        if (trimmed == null) {
            status.update { it.copy(keyRejectedBlank = true, keySaveFailed = false) }
            return
        }
        scope.launch {
            var failed = false
            status.update {
                it.copy(isSavingKey = true, keySaveFailed = false, keyRejectedBlank = false)
            }
            try {
                secretStore.setOpenAiKey(trimmed)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                failed = true
            } finally {
                status.update { it.copy(isSavingKey = false, keySaveFailed = failed) }
            }
        }
    }

    fun onThemeModeChange(mode: ThemeMode) {
        scope.launch { appearanceStore.setThemeMode(mode) }
    }

    fun onClearKey() {
        scope.launch {
            var failed = false
            status.update {
                it.copy(isSavingKey = true, keySaveFailed = false, keyRejectedBlank = false)
            }
            try {
                secretStore.clearOpenAiKey()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                failed = true
            } finally {
                status.update { it.copy(isSavingKey = false, keySaveFailed = failed) }
            }
        }
    }
}

private data class MeStatus(
    val isSavingProfile: Boolean = false,
    val isSavingKey: Boolean = false,
    val profileSaveFailed: Boolean = false,
    val keySaveFailed: Boolean = false,
    val keyRejectedBlank: Boolean = false,
)
