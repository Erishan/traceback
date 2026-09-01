package com.erishan.traceback.opportunity.ui

import com.erishan.traceback.ai.domain.BriefException
import com.erishan.traceback.ai.domain.BriefJobUseCase
import com.erishan.traceback.ai.domain.JobInput
import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.core.enums.OpportunitySource
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.me.domain.UserContextRepository
import com.erishan.traceback.opportunity.domain.Note
import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OpportunityDetailController(
    private val scope: CoroutineScope,
    private val id: String,
    private val repository: OpportunityRepository,
    private val userContextRepository: UserContextRepository,
    private val secretStore: SecretStore,
    private val briefJobUseCase: BriefJobUseCase,
) {
    private val eventsChannel = Channel<DetailEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private val mutationGate = DetailMutationGate()
    private val editMutex = Mutex()

    val uiState: StateFlow<OpportunityDetailUiState> =
        combine(
            repository.observeById(id),
            userContextRepository.observe(),
            secretStore.observe(),
            mutationGate.state,
        ) { opp, profile, key, status ->
            when {
                opp == null -> OpportunityDetailUiState.NotFound
                else -> {
                    val aboutPresent = profile.about.isNotBlank()
                    val canBrief = aboutPresent && key.hasKey
                    OpportunityDetailUiState.Content(
                        title = opp.title,
                        description = opp.description,
                        source = opp.source,
                        sourceLabel = opp.sourceLabel,
                        pipelineStage = opp.pipelineStage,
                        createdAt = opp.createdAt,
                        appliedMessage = opp.appliedMessage,
                        notes = opp.notes,
                        aiBrief = opp.aiBrief,
                        canBrief = canBrief,
                        briefInFlight = status.briefInFlight,
                        briefFailed = status.briefFailed,
                        briefGateReason = briefGateReason(
                            aboutPresent = aboutPresent,
                            hasKey = key.hasKey,
                        ),
                        isSaving = status.isSaving,
                        saveFailed = status.saveFailed,
                    )
                }
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OpportunityDetailUiState.Loading,
        )

    private fun saveEdit(transform: (Opportunity) -> Opportunity) = scope.launch {
        mutationGate.awaitBeginSave()
        var failed = false
        try {
            editMutex.withLock {
                repository.update(id, transform)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            failed = true
        } finally {
            mutationGate.endSave(failed)
        }
    }

    fun onStageChange(stage: PipelineStage) = saveEdit { it.copy(pipelineStage = stage) }

    fun onTitleChange(title: String) = saveEdit { it.copy(title = title) }

    fun onDescriptionChange(description: String) = saveEdit { it.copy(description = description) }

    fun onSourceChange(source: OpportunitySource) = saveEdit {
        it.copy(
            source = source,
            sourceLabel = if (source == OpportunitySource.OTHER) it.sourceLabel else null,
        )
    }

    fun onSourceLabelChange(label: String) = saveEdit {
        it.copy(sourceLabel = if (it.source == OpportunitySource.OTHER) label else null)
    }

    @OptIn(ExperimentalUuidApi::class)
    fun onAddNote(text: String) = saveEdit { opp ->
        opp.copy(
            notes = opp.notes + Note(
                id = Uuid.random().toString(),
                createdAt = Clock.System.now(),
                text = text,
            ),
        )
    }

    fun onDeleteNote(noteId: String) = saveEdit { opp ->
        opp.copy(notes = opp.notes.filterNot { it.id == noteId })
    }

    fun onAppliedMessageChange(message: String) = saveEdit { it.copy(appliedMessage = message) }

    fun onBrief() {
        scope.launch {
            if (!mutationGate.tryClaimBrief()) return@launch
            try {
                mutationGate.awaitNoPendingSaves()
                val profile = userContextRepository.observe().first()
                val key = secretStore.observe().first()
                if (profile.about.isBlank() || !key.hasKey) return@launch
                val opportunity = repository.observeById(id).first() ?: return@launch
                val brief = briefJobUseCase(
                    userContext = profile,
                    job = opportunity.toJobInput(),
                )
                editMutex.withLock {
                    repository.update(id) { it.copy(aiBrief = brief) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: BriefException) {
                mutationGate.markBriefFailed(e.kind.toFailureKind())
            } catch (_: Exception) {
                mutationGate.markBriefFailed(BriefFailureKind.Network)
            } finally {
                mutationGate.releaseBrief()
            }
        }
    }

    fun delete() {
        scope.launch {
            try {
                repository.delete(id)
                eventsChannel.send(DetailEvent.Deleted)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                eventsChannel.send(DetailEvent.DeleteFailed)
            }
        }
    }
}

private fun briefGateReason(aboutPresent: Boolean, hasKey: Boolean): BriefGateReason? = when {
    aboutPresent && hasKey -> null
    !aboutPresent && !hasKey -> BriefGateReason.MissingAboutAndKey
    !aboutPresent -> BriefGateReason.MissingAbout
    else -> BriefGateReason.MissingKey
}

private fun Opportunity.toJobInput() = JobInput(
    title = title,
    description = description,
    source = source.name,
    sourceLabel = sourceLabel,
    appliedMessage = appliedMessage,
)

private fun BriefException.Kind.toFailureKind(): BriefFailureKind = when (this) {
    BriefException.Kind.Unauthorized, BriefException.Kind.MissingKey -> BriefFailureKind.BadKey
    BriefException.Kind.RateLimited -> BriefFailureKind.RateLimited
    BriefException.Kind.InvalidResponse -> BriefFailureKind.InvalidResponse
    BriefException.Kind.Network -> BriefFailureKind.Network
}
