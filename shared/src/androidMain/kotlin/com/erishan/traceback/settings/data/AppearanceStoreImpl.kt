package com.erishan.traceback.settings.data

import android.content.Context
import com.erishan.traceback.settings.domain.AppearanceStore
import com.erishan.traceback.settings.domain.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AppearanceStoreImpl(context: Context) : AppearanceStore {
    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    private val mode = MutableStateFlow(read())

    override fun current(): ThemeMode = mode.value

    override fun observe(): Flow<ThemeMode> = mode.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        this.mode.value = mode
        withContext(Dispatchers.IO) {
            prefs.edit().putString(PREF_THEME_MODE, mode.name).apply()
        }
    }

    private fun read(): ThemeMode {
        val stored = prefs.getString(PREF_THEME_MODE, null) ?: return ThemeMode.SYSTEM
        return ThemeMode.entries.firstOrNull { it.name == stored } ?: ThemeMode.SYSTEM
    }

    private companion object {
        const val PREFS_FILE = "appearance"
        const val PREF_THEME_MODE = "theme_mode"
    }
}
