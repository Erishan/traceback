package com.erishan.traceback.ai.data

import com.erishan.traceback.ai.domain.KeyPresence
import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.ai.domain.lastFourOf
import com.erishan.traceback.ai.domain.trimmedOpenAiKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults

class SecretStoreImpl : SecretStore {
    private val defaults = NSUserDefaults.standardUserDefaults
    private val presence = MutableStateFlow(readPresence())

    override fun observe(): Flow<KeyPresence> = presence.asStateFlow()

    override suspend fun setOpenAiKey(value: String) {
        val trimmed = trimmedOpenAiKey(value)
            ?: throw IllegalArgumentException("OpenAI key must not be blank")
        withContext(Dispatchers.IO) {
            defaults.setObject(trimmed, forKey = PREF_OPENAI_KEY)
            defaults.synchronize()
        }
        presence.value = KeyPresence(hasKey = true, lastFour = lastFourOf(trimmed))
    }

    override suspend fun openAiKey(): String? = withContext(Dispatchers.IO) {
        defaults.stringForKey(PREF_OPENAI_KEY)?.takeIf { it.isNotEmpty() }
    }

    override suspend fun clearOpenAiKey() {
        withContext(Dispatchers.IO) {
            defaults.removeObjectForKey(PREF_OPENAI_KEY)
            defaults.synchronize()
        }
        presence.value = KeyPresence(hasKey = false, lastFour = null)
    }

    private fun readPresence(): KeyPresence {
        val stored = defaults.stringForKey(PREF_OPENAI_KEY)
        return if (stored.isNullOrEmpty()) {
            KeyPresence(hasKey = false, lastFour = null)
        } else {
            KeyPresence(hasKey = true, lastFour = lastFourOf(stored))
        }
    }

    private companion object {
        const val PREF_OPENAI_KEY = "openai_api_key"
    }
}
