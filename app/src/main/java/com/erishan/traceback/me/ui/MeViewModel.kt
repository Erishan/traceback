package com.erishan.traceback.me.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erishan.traceback.TracebackApp
import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.me.domain.UserContextRepository
import com.erishan.traceback.settings.domain.AppearanceStore
import com.erishan.traceback.settings.domain.ThemeMode
import kotlinx.coroutines.flow.StateFlow

class MeViewModel(
    userContextRepository: UserContextRepository,
    secretStore: SecretStore,
    appearanceStore: AppearanceStore,
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

    private val controller = MeController(
        scope = viewModelScope,
        userContextRepository = userContextRepository,
        secretStore = secretStore,
        appearanceStore = appearanceStore,
    )

    val uiState: StateFlow<MeUiState> = controller.uiState

    fun onSaveProfile(about: String, rateBand: String, pace: String) =
        controller.onSaveProfile(about, rateBand, pace)

    fun onSaveKey(value: String) = controller.onSaveKey(value)

    fun onThemeModeChange(mode: ThemeMode) = controller.onThemeModeChange(mode)

    fun onClearKey() = controller.onClearKey()
}
