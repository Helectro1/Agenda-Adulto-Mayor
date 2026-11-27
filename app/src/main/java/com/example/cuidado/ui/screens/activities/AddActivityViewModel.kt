package com.example.cuidado.ui.screens.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.cuidado.data.model.Activity
import com.example.cuidado.data.repository.ActivityRepository
import com.example.cuidado.servicios.NotificacionScheduler
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
    val isInterval: Boolean = false,
    val selectedDays: Set<String> = emptySet(),
    val isFormValid: Boolean = false,
    val isEditing: Boolean = false
)

/**
 * ViewModel para gestionar el formulario de nueva o existente actividad.
 */
class AddActivityViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

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

                    val isInterval = activity.time.startsWith("EVERY_")
                    val rawTime = if (isInterval) activity.time.removePrefix("EVERY_") else activity.time

                    _uiState.update {
                        it.copy(
                            name = activity.name,
                            hour = rawTime,
                            isInterval = isInterval,
                            selectedDays = days,
                            isFormValid = activity.name.isNotBlank() && rawTime.isNotBlank()
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

    fun onIntervalModeChange(isInterval: Boolean) {
        _uiState.update { it.copy(isInterval = isInterval) }
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
     * Guarda o actualiza la actividad en el repositorio centralizado y programa su recordatorio exacto.
     */
    fun saveActivity() {
        val state = _uiState.value
        if (state.isFormValid) {
            val dayMapping = mapOf(
                "L" to "1", "M" to "2", "X" to "3", "J" to "4", "V" to "5", "S" to "6", "D" to "7"
            )
            val repeatDays = if (state.isInterval) {
                "1,2,3,4,5,6,7"
            } else {
                state.selectedDays
                    .mapNotNull { dayMapping[it] }
                    .sorted()
                    .joinToString(",")
            }

            val timeToSave = if (state.isInterval) "EVERY_${state.hour}" else state.hour

            viewModelScope.launch {
                val notificador = NotificacionScheduler(getApplication())
                if (!activityId.isNullOrBlank()) {
                    val existing = ActivityRepository.getActivityById(activityId)
                    val updated = (existing ?: Activity(id = activityId, name = state.name, time = timeToSave)).copy(
                        name = state.name,
                        time = timeToSave,
                        repeatDays = repeatDays
                    )
                    ActivityRepository.updateActivity(updated)
                    if (state.isInterval) {
                        val parts = state.hour.split(":")
                        val totalMins = (parts.getOrNull(0)?.toLongOrNull() ?: 0L) * 60 + (parts.getOrNull(1)?.toLongOrNull() ?: 0L)
                        if (totalMins > 0) {
                            notificador.programarAlarmaIntervalo(activityId, state.name, totalMins)
                        }
                    } else {
                        notificador.Recordatorio_ProgramarActividadDiaria(activityId, state.name, state.hour)
                    }
                } else {
                    val newActivity = Activity(
                        name = state.name,
                        time = timeToSave,
                        repeatDays = repeatDays
                    )
                    ActivityRepository.addActivity(newActivity)
                    if (state.isInterval) {
                        val parts = state.hour.split(":")
                        val totalMins = (parts.getOrNull(0)?.toLongOrNull() ?: 0L) * 60 + (parts.getOrNull(1)?.toLongOrNull() ?: 0L)
                        if (totalMins > 0) {
                            notificador.programarAlarmaIntervalo(newActivity.id, state.name, totalMins)
                        }
                    } else {
                        notificador.Recordatorio_ProgramarActividadDiaria(newActivity.id, state.name, state.hour)
                    }
                }
            }
        }
    }
}
