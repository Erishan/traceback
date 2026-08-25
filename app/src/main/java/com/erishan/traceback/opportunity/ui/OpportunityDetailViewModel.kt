package com.erishan.traceback.opportunity.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erishan.traceback.TracebackApp
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import kotlin.time.Clock

class OpportunityDetailViewModel(
    private val id: String,
    private val repository: OpportunityRepository,
    private val userContextRepository: UserContextRepository,
    private val secretStore: SecretStore,
    private val briefJobUseCase: BriefJobUseCase,
) : ViewModel() {
    companion object {
        fun provideFactory(id: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as TracebackApp
                OpportunityDetailViewModel(
                    id = id,
                    repository = app.container.opportunityRepository,
                    userContextRepository = app.container.userContextRepository,
                    secretStore = app.container.secretStore,
                    briefJobUseCase = app.container.briefJobUseCase,
                )
            }
        }
    }

    private val _events = Channel<DetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _status = MutableStateFlow(EditStatus())
    private val editMutex = Mutex()

    val uiState: StateFlow<OpportunityDetailUiState> =
        combine(
            repository.observeById(id),
            userContextRepository.observe(),
            secretStore.observe(),
            _status,
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
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OpportunityDetailUiState.Loading
        )

    private fun saveEdit(transform: (Opportunity) -> Opportunity) = viewModelScope.launch {
        var failed = false
        _status.update { it.copy(pendingSaves = it.pendingSaves + 1, saveFailed = false) }
        try {
            editMutex.withLock {
                repository.update(id, transform)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            failed = true
        } finally {
            _status.update {
                it.copy(
                    pendingSaves = (it.pendingSaves - 1).coerceAtLeast(0),
                    saveFailed = it.saveFailed || failed
                )
            }
        }
    }

    fun onStageChange(stage: PipelineStage) = saveEdit { it.copy(pipelineStage = stage) }

    fun onTitleChange(title: String) = saveEdit { it.copy(title = title) }

    fun onDescriptionChange(description: String) = saveEdit { it.copy(description = description) }

    fun onSourceChange(source: OpportunitySource) = saveEdit {
        it.copy(
            source = source,
            sourceLabel = if (source == OpportunitySource.OTHER) it.sourceLabel else null
        )
    }

    fun onSourceLabelChange(label: String) = saveEdit {
        it.copy(
            sourceLabel = if (it.source == OpportunitySource.OTHER) label else null
        )
    }

    fun onAddNote(text: String) = saveEdit { opp ->
        opp.copy(
            notes = opp.notes + Note(
                id = UUID.randomUUID().toString(),
                createdAt = Clock.System.now(),
                text = text,
            )
        )
    }

    fun onDeleteNote(noteId: String) = saveEdit { opp ->
        opp.copy(notes = opp.notes.filterNot { it.id == noteId })
    }

    fun onAppliedMessageChange(message: String) = saveEdit { it.copy(appliedMessage = message) }

    fun onBrief() {
        viewModelScope.launch {
            if (_status.value.briefInFlight) return@launch
            val profile = userContextRepository.observe().first()
            val key = secretStore.observe().first()
            if (profile.about.isBlank() || !key.hasKey) return@launch
            val opportunity = repository.observeById(id).first() ?: return@launch
            _status.update { it.copy(briefInFlight = true, briefFailed = null) }
            try {
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
                _status.update { it.copy(briefFailed = e.kind.toFailureKind()) }
            } catch (_: Exception) {
                _status.update { it.copy(briefFailed = BriefFailureKind.Network) }
            } finally {
                _status.update { it.copy(briefInFlight = false) }
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            try {
                repository.delete(id)
                _events.send(DetailEvent.Deleted)
            } catch (e: Exception) {
                _events.send(DetailEvent.DeleteFailed)
            }
        }
    }
}

private data class EditStatus(
    val pendingSaves: Int = 0,
    val saveFailed: Boolean = false,
    val briefInFlight: Boolean = false,
    val briefFailed: BriefFailureKind? = null,
) {
    val isSaving: Boolean
        get() = pendingSaves > 0
}

enum class DetailEvent { Deleted, DeleteFailed }

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
