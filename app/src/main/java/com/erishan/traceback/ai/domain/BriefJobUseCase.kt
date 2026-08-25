package com.erishan.traceback.ai.domain

import com.erishan.traceback.me.domain.UserContext
import com.erishan.traceback.opportunity.domain.JobBrief
import kotlin.time.Clock

class BriefJobUseCase(
    private val secretStore: SecretStore,
    private val openAiClient: OpenAiClient,
    private val nowEpochMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) {
    suspend operator fun invoke(userContext: UserContext, job: JobInput): JobBrief {
        val apiKey = secretStore.openAiKey() ?: throw BriefException(BriefException.Kind.MissingKey)
        val content = openAiClient.completeChat(
            apiKey = apiKey,
            model = OPENAI_MODEL,
            systemPrompt = briefSystemPrompt(userContext),
            userMessage = briefUserMessage(job),
        )
        return jobBriefFromModelJson(
            json = content,
            generatedAtEpochMillis = nowEpochMillis(),
            model = OPENAI_MODEL,
            pace = userContext.pace,
        ) ?: throw BriefException(BriefException.Kind.InvalidResponse)
    }
}
