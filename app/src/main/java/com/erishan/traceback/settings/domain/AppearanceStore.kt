package com.erishan.traceback.settings.domain

import kotlinx.coroutines.flow.Flow

interface AppearanceStore {
    fun current(): ThemeMode

    fun observe(): Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)
}
