package com.erishan.traceback.ai.domain

interface OpenAiClient {
    suspend fun completeChat(
        apiKey: String,
        model: String,
        systemPrompt: String,
        userMessage: String,
    ): String
}
