package com.erishan.traceback.ai.domain

import com.erishan.traceback.me.domain.UserContext
import com.erishan.traceback.opportunity.domain.JobBrief
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BriefJobUseCaseTest {

    private val profile = UserContext(
        about = "Android freelancer. No PHP.",
        rateBand = "mid",
        pace = "one client at a time",
    )
    private val job = JobInput(
        title = "SaaS onboarding",
        description = "Rework signup",
        source = "UPWORK",
        sourceLabel = null,
        appliedMessage = null,
    )
    private val modelJson = """
        {
          "fit": {"verdict": "yes", "summary": "Stack matches."},
          "proposal": "I can take this.",
          "price": {"low": "2k", "high": "4k", "rationale": "Fits the band"},
          "duration": {"range": "2-3 weeks", "hours": "40", "basis": "typical"},
          "approach": {"summary": "Ship a slice.", "technologies": ["Compose"]}
        }
    """.trimIndent()

    @Test
    fun missingKey_throwsMissingKey() = runBlocking {
        val useCase = BriefJobUseCase(
            secretStore = FakeSecretStore(key = null),
            openAiClient = FakeOpenAiClient { _, _, _, _ -> error("should not call OpenAI") },
        )

        val error = runCatching { useCase(profile, job) }.exceptionOrNull()

        assertTrue(error is BriefException)
        assertEquals(BriefException.Kind.MissingKey, (error as BriefException).kind)
    }

    @Test
    fun validResponse_returnsBriefWithInjectedMetadata() = runBlocking {
        val captured = mutableListOf<String>()
        val useCase = BriefJobUseCase(
            secretStore = FakeSecretStore(key = "sk-test"),
            openAiClient = FakeOpenAiClient { apiKey, model, system, user ->
                captured += listOf(apiKey, model, system, user)
                modelJson
            },
            nowEpochMillis = { 99L },
        )

        val brief: JobBrief = useCase(profile, job)

        assertEquals("sk-test", captured[0])
        assertEquals(OPENAI_MODEL, captured[1])
        assertTrue(captured[2].contains("Android freelancer"))
        assertTrue(captured[3].contains("SaaS onboarding"))
        assertEquals(99L, brief.generatedAtEpochMillis)
        assertEquals(OPENAI_MODEL, brief.model)
        assertEquals(BASIS_PROFILE, brief.duration.basis)
        assertEquals("I can take this.", brief.proposal)
    }

    @Test
    fun invalidJson_throwsInvalidResponseAndDoesNotInventABrief() = runBlocking {
        val useCase = BriefJobUseCase(
            secretStore = FakeSecretStore(key = "sk-test"),
            openAiClient = FakeOpenAiClient { _, _, _, _ -> "{not-a-brief}" },
            nowEpochMillis = { 1L },
        )

        val error = runCatching { useCase(profile, job) }.exceptionOrNull()

        assertTrue(error is BriefException)
        assertEquals(BriefException.Kind.InvalidResponse, (error as BriefException).kind)
    }

    @Test
    fun httpFailure_isSurfaced() = runBlocking {
        val useCase = BriefJobUseCase(
            secretStore = FakeSecretStore(key = "sk-test"),
            openAiClient = FakeOpenAiClient { _, _, _, _ ->
                throw BriefException(BriefException.Kind.Unauthorized)
            },
        )

        val error = runCatching { useCase(profile, job) }.exceptionOrNull()

        assertEquals(BriefException.Kind.Unauthorized, (error as BriefException).kind)
    }

    private class FakeSecretStore(private val key: String?) : SecretStore {
        override fun observe(): Flow<KeyPresence> =
            MutableStateFlow(KeyPresence(hasKey = key != null, lastFour = key?.takeLast(4)))

        override suspend fun setOpenAiKey(value: String) = Unit

        override suspend fun clearOpenAiKey() = Unit

        override suspend fun openAiKey(): String? = key

        override suspend fun warmUp() = Unit
    }

    private class FakeOpenAiClient(
        private val complete: suspend (
            apiKey: String,
            model: String,
            systemPrompt: String,
            userMessage: String,
        ) -> String,
    ) : OpenAiClient {
        override suspend fun completeChat(
            apiKey: String,
            model: String,
            systemPrompt: String,
            userMessage: String,
        ): String = complete(apiKey, model, systemPrompt, userMessage)
    }
}
