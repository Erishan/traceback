package com.erishan.traceback.ai.domain

import kotlinx.coroutines.flow.Flow

interface SecretStore {
    fun observe(): Flow<KeyPresence>
    suspend fun setOpenAiKey(value: String)
    suspend fun clearOpenAiKey()
}
