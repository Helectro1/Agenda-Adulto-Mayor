package com.example.cuidado.ui.screens.activities

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
 * Estado de la interfaz para la creación de una actividad.
 */
data class AddActivityUiState(
    val name: String = "",
    val hour: String = "",
    val selectedDays: Set<String> = emptySet(),
    val isFormValid: Boolean = false
)

/**
 * ViewModel para gestionar el formulario de nueva actividad.
 */
class AddActivityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddActivityUiState())
    val uiState: StateFlow<AddActivityUiState> = _uiState.asStateFlow()

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
     * Guarda la actividad en el repositorio centralizado.
     */
    fun saveActivity() {
        val state = _uiState.value
        if (state.isFormValid) {
            
            // Mapeo de días a formato numérico para repeatDays (L=1, M=2, ...)
            val dayMapping = mapOf(
                "L" to "1", "M" to "2", "X" to "3", "J" to "4", "V" to "5", "S" to "6", "D" to "7"
            )
            val repeatDays = state.selectedDays
                .mapNotNull { dayMapping[it] }
                .sorted()
                .joinToString(",")

            val newActivity = Activity(
                name = state.name,
                time = state.hour,
                repeatDays = repeatDays
            )
            
            viewModelScope.launch {
                ActivityRepository.addActivity(newActivity)
            }
        }
    }
}
