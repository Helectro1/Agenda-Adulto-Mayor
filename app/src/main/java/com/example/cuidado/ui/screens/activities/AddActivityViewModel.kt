package com.example.cuidado.ui.screens.activities

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuidado.data.model.Activity
import com.example.cuidado.data.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la interfaz para la creación o edición de una actividad.
 */
data class AddActivityUiState(
    val name: String = "",
    val hour: String = "",
    val selectedDays: Set<String> = emptySet(),
    val isFormValid: Boolean = false,
    val isEditing: Boolean = false
)

/**
 * ViewModel para gestionar el formulario de nueva o existente actividad.
 */
class AddActivityViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val activityId: String? = savedStateHandle.get<String>("activityId")

    private val _uiState = MutableStateFlow(AddActivityUiState(isEditing = activityId != null))
    val uiState: StateFlow<AddActivityUiState> = _uiState.asStateFlow()

    init {
        if (!activityId.isNullOrBlank()) {
            viewModelScope.launch {
                ActivityRepository.getActivityById(activityId)?.let { activity ->
                    val dayMappingReverse = mapOf(
                        "1" to "L", "2" to "M", "3" to "X", "4" to "J", "5" to "V", "6" to "S", "7" to "D"
                    )
                    val days = activity.repeatDays.split(",")
                        .mapNotNull { dayMappingReverse[it.trim()] }
                        .toSet()

                    _uiState.update {
                        it.copy(
                            name = activity.name,
                            hour = activity.time,
                            selectedDays = days,
                            isFormValid = activity.name.isNotBlank() && activity.time.isNotBlank()
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { 
            it.copy(
                name = name, 
                isFormValid = name.isNotBlank() && it.hour.isNotBlank()
            ) 
        }
    }

    fun onHourChange(hour: String) {
        _uiState.update { 
            it.copy(
                hour = hour, 
                isFormValid = it.name.isNotBlank() && hour.isNotBlank()
            ) 
        }
    }

    fun toggleDay(day: String) {
        _uiState.update { state ->
            val newDays = if (day in state.selectedDays) {
                state.selectedDays - day
            } else {
                state.selectedDays + day
            }
            state.copy(selectedDays = newDays)
        }
    }

    /**
     * Guarda o actualiza la actividad en el repositorio centralizado.
     */
    fun saveActivity() {
        val state = _uiState.value
        if (state.isFormValid) {
            val dayMapping = mapOf(
                "L" to "1", "M" to "2", "X" to "3", "J" to "4", "V" to "5", "S" to "6", "D" to "7"
            )
            val repeatDays = state.selectedDays
                .mapNotNull { dayMapping[it] }
                .sorted()
                .joinToString(",")

            viewModelScope.launch {
                if (!activityId.isNullOrBlank()) {
                    val existing = ActivityRepository.getActivityById(activityId)
                    val updated = (existing ?: Activity(id = activityId, name = state.name, time = state.hour)).copy(
                        name = state.name,
                        time = state.hour,
                        repeatDays = repeatDays
                    )
                    ActivityRepository.updateActivity(updated)
                } else {
                    val newActivity = Activity(
                        name = state.name,
                        time = state.hour,
                        repeatDays = repeatDays
                    )
                    ActivityRepository.addActivity(newActivity)
                }
            }
        }
    }
}
