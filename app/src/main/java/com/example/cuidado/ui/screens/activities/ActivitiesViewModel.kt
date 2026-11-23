package com.example.cuidado.ui.screens.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuidado.data.model.Activity
import com.example.cuidado.data.repository.ActivityRepository
import com.example.cuidado.data.repository.ProfileRepository
import com.example.cuidado.servicios.NotificacionScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de gestión de actividades.
 */
class ActivitiesViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Lista de actividades obtenida desde el repositorio.
     */
    val activities: StateFlow<List<Activity>> = ActivityRepository.getActivities()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val routineTitle: StateFlow<String> = ProfileRepository.getProfile()
        .map { profile ->
            val name = profile?.name?.trim()
            if (!name.isNullOrBlank()) "Rutina de $name" else "Rutina del adulto mayor"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Rutina del adulto mayor"
        )

    fun deleteActivity(activity: Activity) {
        viewModelScope.launch {
            NotificacionScheduler(getApplication()).Recordatorio_CancelarActividadDiaria(activity.id)
            ActivityRepository.deleteActivity(activity)
        }
    }
}
