@file:Suppress("DEPRECATION")

package com.erishan.traceback.ai.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.erishan.traceback.ai.domain.KeyPresence
import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.ai.domain.lastFourOf
import com.erishan.traceback.ai.domain.trimmedOpenAiKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SecretStoreImpl(
    context: Context,
) : SecretStore {
    private val prefs: SharedPreferences
    private val presence = MutableStateFlow(KeyPresence(hasKey = false, lastFour = null))

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        presence.value = readPresence()
    }

    init {
        val appContext = context.applicationContext
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        prefs = EncryptedSharedPreferences.create(
            appContext,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        prefs.registerOnSharedPreferenceChangeListener(listener)
        presence.value = readPresence()
    }

    override fun observe(): Flow<KeyPresence> = presence.asStateFlow()

    override suspend fun setOpenAiKey(value: String) {
        val trimmed = trimmedOpenAiKey(value)
            ?: throw IllegalArgumentException("OpenAI key must not be blank")
        val stored = withContext(Dispatchers.IO) {
            prefs.edit().putString(PREF_OPENAI_KEY, trimmed).commit()
        }
        if (!stored) {
            error("Could not store OpenAI key")
        }
        presence.value = KeyPresence(hasKey = true, lastFour = lastFourOf(trimmed))
    }

    override suspend fun clearOpenAiKey() {
        val cleared = withContext(Dispatchers.IO) {
            prefs.edit().remove(PREF_OPENAI_KEY).commit()
        }
        if (!cleared) {
            error("Could not clear OpenAI key")
        }
        presence.value = KeyPresence(hasKey = false, lastFour = null)
    }

    private fun readPresence(): KeyPresence {
        val stored = prefs.getString(PREF_OPENAI_KEY, null)
        return if (stored.isNullOrEmpty()) {
            KeyPresence(hasKey = false, lastFour = null)
        } else {
            KeyPresence(hasKey = true, lastFour = lastFourOf(stored))
        }
    }

    private companion object {
        const val PREFS_FILE = "ai_secrets"
        const val PREF_OPENAI_KEY = "openai_api_key"
    }
}
