package com.reptrack.app.ui.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reptrack.app.data.db.SessionSetEntity
import com.reptrack.app.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseGroup(
    val exerciseDefId: Long,
    val exerciseName: String,
    val inputType: String,
    val sets: List<SessionSetEntity>
)

data class ActiveSessionUiState(
    val sessionId: Long = -1L,
    val templateName: String = "",
    val startedAt: Long = 0L,
    val exerciseGroups: List<ExerciseGroup> = emptyList(),
    val isLoading: Boolean = true,
    val isFinished: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ActiveSessionViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    private val _uiState = MutableStateFlow(ActiveSessionUiState(sessionId = sessionId))
    val uiState: StateFlow<ActiveSessionUiState> = _uiState.asStateFlow()

    init {
        loadSession()
        observeSets()
    }

    private fun loadSession() {
        viewModelScope.launch {
            val session = repository.getSessionById(sessionId)
            if (session != null) {
                _uiState.update {
                    it.copy(
                        templateName = session.templateName,
                        startedAt = session.startedAt,
                        isFinished = session.finishedAt != null
                    )
                }
            }
        }
    }

    private fun observeSets() {
        viewModelScope.launch {
            repository.getSessionSets(sessionId).collect { sets ->
                val groups = sets
                    .groupBy { it.exerciseDefId }
                    .map { (defId, groupSets) ->
                        val first = groupSets.first()
                        ExerciseGroup(
                            exerciseDefId = defId,
                            exerciseName = first.exerciseName,
                            inputType = first.inputType,
                            sets = groupSets.sortedBy { it.setNumber }
                        )
                    }
                    .sortedBy { it.exerciseName }
                _uiState.update { it.copy(exerciseGroups = groups, isLoading = false) }
            }
        }
    }

    fun updateSet(set: SessionSetEntity, weightKg: Float?, reps: Int?, durationSeconds: Int?) {
        viewModelScope.launch {
            repository.updateSessionSet(
                set.copy(
                    weightKg = weightKg,
                    reps = reps,
                    durationSeconds = durationSeconds
                )
            )
        }
    }

    fun deleteSet(set: SessionSetEntity) {
        viewModelScope.launch {
            repository.deleteSessionSet(set)
        }
    }

    fun addSet(group: ExerciseGroup) {
        viewModelScope.launch {
            val nextSetNumber = (group.sets.maxOfOrNull { it.setNumber } ?: 0) + 1
            val lastSet = group.sets.lastOrNull()
            repository.insertSessionSet(
                SessionSetEntity(
                    sessionId = sessionId,
                    exerciseDefId = group.exerciseDefId,
                    exerciseName = group.exerciseName,
                    setNumber = nextSetNumber,
                    weightKg = lastSet?.weightKg,
                    reps = lastSet?.reps,
                    durationSeconds = lastSet?.durationSeconds,
                    inputType = group.inputType
                )
            )
        }
    }

    fun finishSession() {
        viewModelScope.launch {
            repository.finishSession(sessionId)
            _uiState.update { it.copy(isFinished = true) }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}