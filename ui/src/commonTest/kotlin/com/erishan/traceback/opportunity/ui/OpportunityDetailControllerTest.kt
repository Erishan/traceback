package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.ai.domain.BriefJobUseCase
import com.erishan.traceback.ai.domain.KeyPresence
import com.erishan.traceback.ai.domain.OpenAiClient
import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.me.domain.UserContext
import com.erishan.traceback.me.domain.UserContextRepository
import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OpportunityDetailControllerTest {

    @Test
    fun cancellingScopeBeforeBriefReturnsDoesNotWriteBrief() = runBlocking {
        val scopeJob = SupervisorJob(coroutineContext[Job])
        val scope = CoroutineScope(coroutineContext + scopeJob)
        val secretStore = FakeSecretStore(key = "sk-test")
        val repository = FakeOpportunityRepository(sampleOpportunity())
        val openAiClient = CancellationIgnoringOpenAiClient(response = modelJson)
        val controller = OpportunityDetailController(
            scope = scope,
            id = OpportunityId,
            repository = repository,
            userContextRepository = FakeUserContextRepository(),
            secretStore = secretStore,
            briefJobUseCase = BriefJobUseCase(
                secretStore = secretStore,
                openAiClient = openAiClient,
                nowEpochMillis = { 42L },
            ),
        )

        controller.onBrief()
        openAiClient.started.await()
        scope.cancel()
        scopeJob.join()

        assertEquals(0, repository.updateCount)
        assertNull(repository.current?.aiBrief)
    }

    private class FakeOpportunityRepository(
        initial: Opportunity,
    ) : OpportunityRepository {
        private val opportunity = MutableStateFlow<Opportunity?>(initial)
        var updateCount = 0
            private set
        val current: Opportunity?
            get() = opportunity.value

        override suspend fun save(opportunity: Opportunity) = Unit

        override suspend fun update(
            id: String,
            transform: (Opportunity) -> Opportunity,
        ): Boolean {
            updateCount += 1
            this.opportunity.value = this.opportunity.value?.takeIf { it.id == id }?.let(transform)
            return this.opportunity.value != null
        }

        override suspend fun delete(id: String) = Unit

        override fun observeById(id: String): Flow<Opportunity?> =
            if (id == OpportunityId) opportunity else MutableStateFlow(null)

        override fun observeAll(): Flow<List<Opportunity>> =
            MutableStateFlow(opportunity.value?.let(::listOf).orEmpty())
    }

    private class FakeUserContextRepository : UserContextRepository {
        private val userContext = MutableStateFlow(
            UserContext(
                about = "KMP freelancer",
                rateBand = "mid",
                pace = null,
            )
        )

        override fun observe(): Flow<UserContext> = userContext

        override suspend fun save(userContext: UserContext) {
            this.userContext.value = userContext
        }
    }

    private class FakeSecretStore(private val key: String?) : SecretStore {
        override fun observe(): Flow<KeyPresence> =
            MutableStateFlow(KeyPresence(hasKey = key != null, lastFour = key?.takeLast(4)))

        override suspend fun setOpenAiKey(value: String) = Unit

        override suspend fun clearOpenAiKey() = Unit

        override suspend fun openAiKey(): String? = key

        override suspend fun warmUp() = Unit
    }

    private class CancellationIgnoringOpenAiClient(
        private val response: String,
    ) : OpenAiClient {
        val started = CompletableDeferred<Unit>()
        private val neverCompletes = CompletableDeferred<String>()

        override suspend fun completeChat(
            apiKey: String,
            model: String,
            systemPrompt: String,
            userMessage: String,
        ): String {
            started.complete(Unit)
            return try {
                neverCompletes.await()
            } catch (_: CancellationException) {
                response
            }
        }
    }

    private fun sampleOpportunity() = Opportunity(
        id = OpportunityId,
        title = "iOS shell lifecycle",
        description = "Cancel brief work when the detail route leaves.",
        source = OpportunitySource.UPWORK,
        sourceLabel = null,
        pipelineStage = PipelineStage.APPLIED,
        createdAt = null,
        notes = emptyList(),
        appliedMessage = null,
    )

    private companion object {
        const val OpportunityId = "opp-1"

        val modelJson = """
            {
              "fit": {"verdict": "yes", "summary": "Stack matches."},
              "proposal": "I can take this.",
              "price": {"low": "2k", "high": "4k", "rationale": "Fits the band"},
              "duration": {"range": "2-3 weeks", "hours": "40", "basis": "typical"},
              "approach": {"summary": "Ship a slice.", "technologies": ["Compose"]}
            }
        """.trimIndent()
    }
}
