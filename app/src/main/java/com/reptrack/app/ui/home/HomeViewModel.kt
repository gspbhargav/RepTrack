package com.reptrack.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reptrack.app.data.db.WorkoutTemplateEntity
import com.reptrack.app.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val templates: List<WorkoutTemplateEntity> = emptyList(),
    val isLoading: Boolean = true,
    val startingSessionId: Long? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
        }
        viewModelScope.launch {
            repository.getAllTemplates().collect { templates ->
                _uiState.update { it.copy(templates = templates, isLoading = false) }
            }
        }
    }

    fun startSession(template: WorkoutTemplateEntity) {
        viewModelScope.launch {
            val sessionId = repository.startNewSession(template.id, template.name)
            _uiState.update { it.copy(startingSessionId = sessionId) }
        }
    }

    fun onSessionNavigated() {
        _uiState.update { it.copy(startingSessionId = null) }
    }

    fun deleteTemplate(template: WorkoutTemplateEntity) {
        viewModelScope.launch {
            repository.deleteTemplate(template)
        }
    }
}