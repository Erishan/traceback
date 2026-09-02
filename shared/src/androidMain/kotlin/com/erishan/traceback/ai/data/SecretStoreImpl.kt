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
    private val appContext = context.applicationContext
    private val presence = MutableStateFlow(KeyPresence(hasKey = false, lastFour = null))

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        presence.value = readPresence()
    }

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val encryptedPrefs = EncryptedSharedPreferences.create(
            appContext,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        encryptedPrefs.registerOnSharedPreferenceChangeListener(listener)
        presence.value = readPresence(encryptedPrefs)
        encryptedPrefs
    }

    override fun observe(): Flow<KeyPresence> {
        prefs
        return presence.asStateFlow()
    }

    override suspend fun warmUp() {
        withContext(Dispatchers.IO) {
            prefs
        }
    }

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

    override suspend fun openAiKey(): String? = withContext(Dispatchers.IO) {
        prefs.getString(PREF_OPENAI_KEY, null)?.takeIf { it.isNotEmpty() }
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

    private fun readPresence(store: SharedPreferences = prefs): KeyPresence {
        val stored = store.getString(PREF_OPENAI_KEY, null)
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
