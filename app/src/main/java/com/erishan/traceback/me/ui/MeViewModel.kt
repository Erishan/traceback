package com.erishan.traceback.me.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erishan.traceback.TracebackApp
import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.ai.domain.trimmedOpenAiKey
import com.erishan.traceback.me.domain.UserContext
import com.erishan.traceback.me.domain.UserContextRepository
import com.erishan.traceback.settings.domain.AppearanceStore
import com.erishan.traceback.settings.domain.ThemeMode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MeViewModel(
    private val userContextRepository: UserContextRepository,
    private val secretStore: SecretStore,
    private val appearanceStore: AppearanceStore,
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as TracebackApp
                MeViewModel(
                    userContextRepository = app.container.userContextRepository,
                    secretStore = app.container.secretStore,
                    appearanceStore = app.container.appearanceStore,
                )
            }
        }
    }

    private val _status = MutableStateFlow(MeStatus())

    val uiState: StateFlow<MeUiState> =
        combine(
            userContextRepository.observe(),
            secretStore.observe(),
            appearanceStore.observe(),
            _status,
        ) { profile, key, themeMode, status ->
            MeUiState(
                about = profile.about,
                rateBand = profile.rateBand,
                pace = profile.pace,
                hasKey = key.hasKey,
                lastFour = key.lastFour,
                isLoaded = true,
                isSavingProfile = status.isSavingProfile,
                isSavingKey = status.isSavingKey,
                profileSaveFailed = status.profileSaveFailed,
                keySaveFailed = status.keySaveFailed,
                keyRejectedBlank = status.keyRejectedBlank,
                themeMode = themeMode,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MeUiState(),
        )

    fun onSaveProfile(about: String, rateBand: String, pace: String) {
        viewModelScope.launch {
            var failed = false
            _status.update {
                it.copy(isSavingProfile = true, profileSaveFailed = false)
            }
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
            } catch (e: Exception) {
                failed = true
            } finally {
                _status.update {
                    it.copy(isSavingProfile = false, profileSaveFailed = failed)
                }
            }
        }
    }

    fun onSaveKey(value: String) {
        val trimmed = trimmedOpenAiKey(value)
        if (trimmed == null) {
            _status.update { it.copy(keyRejectedBlank = true, keySaveFailed = false) }
            return
        }
        viewModelScope.launch {
            var failed = false
            _status.update {
                it.copy(isSavingKey = true, keySaveFailed = false, keyRejectedBlank = false)
            }
            try {
                secretStore.setOpenAiKey(trimmed)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                failed = true
            } finally {
                _status.update {
                    it.copy(isSavingKey = false, keySaveFailed = failed)
                }
            }
        }
    }

    fun onThemeModeChange(mode: ThemeMode) {
        viewModelScope.launch { appearanceStore.setThemeMode(mode) }
    }

    fun onClearKey() {
        viewModelScope.launch {
            var failed = false
            _status.update {
                it.copy(isSavingKey = true, keySaveFailed = false, keyRejectedBlank = false)
            }
            try {
                secretStore.clearOpenAiKey()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                failed = true
            } finally {
                _status.update {
                    it.copy(isSavingKey = false, keySaveFailed = failed)
                }
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
