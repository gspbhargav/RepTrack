package com.reptrack.app.ui.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reptrack.app.data.db.SessionSetEntity
import com.reptrack.app.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PersonalBest(
    val weightKg: Float?,
    val reps: Int?,
    val durationSeconds: Int?
)

data class ExerciseDetailUiState(
    val exerciseName: String = "",
    val inputType: String = "",
    val currentSets: List<SessionSetEntity> = emptyList(),
    val personalBest: PersonalBest? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])
    private val exerciseDefId: Long = checkNotNull(savedStateHandle["exerciseDefId"])

    private val _uiState = MutableStateFlow(ExerciseDetailUiState())
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    init {
        loadExerciseInfo()
        observeCurrentSets()
        loadPersonalBest()
    }

    private fun loadExerciseInfo() {
        viewModelScope.launch {
            // Get name and inputType from the first set we find for this exercise
            repository.getSessionSets(sessionId).first { sets ->
                val match = sets.find { it.exerciseDefId == exerciseDefId }
                if (match != null) {
                    _uiState.update {
                        it.copy(
                            exerciseName = match.exerciseName,
                            inputType = match.inputType
                        )
                    }
                }
                true
            }
        }
    }

    private fun observeCurrentSets() {
        viewModelScope.launch {
            repository.getSessionSets(sessionId).collect { sets ->
                val filtered = sets
                    .filter { it.exerciseDefId == exerciseDefId }
                    .sortedBy { it.setNumber }
                _uiState.update { it.copy(currentSets = filtered, isLoading = false) }
            }
        }
    }

    private fun loadPersonalBest() {
        viewModelScope.launch {
            repository.getAllSessions().first { sessions ->
                val allSets = mutableListOf<SessionSetEntity>()
                sessions
                    .filter { it.finishedAt != null && it.id != sessionId }
                    .forEach { session ->
                        val sets = repository.getSessionSets(session.id)
                            .first()
                            .filter { it.exerciseDefId == exerciseDefId }
                        allSets.addAll(sets)
                    }

                val best = when {
                    allSets.isEmpty() -> null
                    allSets.first().inputType == "WEIGHT_REPS" -> {
                        val top = allSets.maxByOrNull { (it.weightKg ?: 0f) }
                        PersonalBest(top?.weightKg, top?.reps, null)
                    }
                    allSets.first().inputType == "REPS_ONLY" -> {
                        val top = allSets.maxByOrNull { (it.reps ?: 0) }
                        PersonalBest(null, top?.reps, null)
                    }
                    else -> {
                        val top = allSets.maxByOrNull { (it.durationSeconds ?: 0) }
                        PersonalBest(null, null, top?.durationSeconds)
                    }
                }
                _uiState.update { it.copy(personalBest = best) }
                true
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

    fun addSet() {
        viewModelScope.launch {
            val currentSets = _uiState.value.currentSets
            val nextSetNumber = (currentSets.maxOfOrNull { it.setNumber } ?: 0) + 1
            val lastSet = currentSets.lastOrNull()
            repository.insertSessionSet(
                SessionSetEntity(
                    sessionId = sessionId,
                    exerciseDefId = exerciseDefId,
                    exerciseName = _uiState.value.exerciseName,
                    setNumber = nextSetNumber,
                    weightKg = lastSet?.weightKg,
                    reps = lastSet?.reps,
                    durationSeconds = lastSet?.durationSeconds,
                    inputType = _uiState.value.inputType
                )
            )
        }
    }
}