package com.erishan.traceback.settings.data

import com.erishan.traceback.settings.domain.AppearanceStore
import com.erishan.traceback.settings.domain.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults

class AppearanceStoreImpl : AppearanceStore {
    private val defaults = NSUserDefaults.standardUserDefaults
    private val mode = MutableStateFlow(read())

    override fun current(): ThemeMode = mode.value

    override fun observe(): Flow<ThemeMode> = mode.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        this.mode.value = mode
        withContext(Dispatchers.IO) {
            defaults.setObject(mode.name, forKey = PREF_THEME_MODE)
            defaults.synchronize()
        }
    }

    private fun read(): ThemeMode {
        val stored = defaults.stringForKey(PREF_THEME_MODE) ?: return ThemeMode.SYSTEM
        return ThemeMode.entries.firstOrNull { it.name == stored } ?: ThemeMode.SYSTEM
    }

    private companion object {
        const val PREF_THEME_MODE = "theme_mode"
    }
}
