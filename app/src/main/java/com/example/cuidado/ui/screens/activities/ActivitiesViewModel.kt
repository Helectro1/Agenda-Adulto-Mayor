package com.example.cuidado.ui.screens.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuidado.data.model.Activity
import com.example.cuidado.data.repository.ActivityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel para la pantalla de gestión de actividades.
 */
class ActivitiesViewModel : ViewModel() {

    /**
     * Lista de actividades obtenida desde el repositorio.
     */
    val activities: StateFlow<List<Activity>> = ActivityRepository.getActivities()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
