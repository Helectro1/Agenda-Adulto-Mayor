package com.example.cuidado.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuidado.data.model.Activity
import com.example.cuidado.data.repository.ActivityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Estado de una actividad en la pantalla de inicio, incluyendo si ya fue realizada.
 */
data class ActivityItemState(
    val activity: Activity,
    val isCompleted: Boolean = false
)

/**
 * ViewModel para la pantalla de inicio.
 */
class HomeViewModel : ViewModel() {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    /**
     * Lista de actividades para hoy con su estado de cumplimiento.
     */
    val activities: StateFlow<List<ActivityItemState>> = combine(
        ActivityRepository.getActivities(),
        ActivityRepository.getLogsForDate(getCurrentDate())
    ) { allActivities, logs ->
        val completedIds = logs.map { it.activityId }.toSet()
        allActivities.map { activity ->
            ActivityItemState(
                activity = activity,
                isCompleted = completedIds.contains(activity.id)
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Marca una actividad como realizada hoy.
     */
    fun toggleActivityCompletion(activity: Activity) {
        viewModelScope.launch {
            ActivityRepository.markActivityAsCompleted(
                activityId = activity.id,
                date = getCurrentDate(),
                time = getCurrentTime()
            )
        }
    }

    private fun getCurrentDate(): String = dateFormatter.format(Date())
    private fun getCurrentTime(): String = timeFormatter.format(Date())
}
