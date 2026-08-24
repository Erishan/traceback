package com.erishan.traceback.me.ui

data class MeUiState(
    val about: String = "",
    val rateBand: String? = null,
    val pace: String? = null,
    val hasKey: Boolean = false,
    val lastFour: String? = null,
    val isLoaded: Boolean = false,
    val isSavingProfile: Boolean = false,
    val isSavingKey: Boolean = false,
    val profileSaveFailed: Boolean = false,
    val keySaveFailed: Boolean = false,
    val keyRejectedBlank: Boolean = false,
)
