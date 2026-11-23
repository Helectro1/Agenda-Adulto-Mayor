package com.example.cuidado.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuidado.data.local.CitaEntity
import com.example.cuidado.data.model.Activity
import com.example.cuidado.data.model.parseTimeToMinutes
import com.example.cuidado.data.repository.ActivityRepository
import com.example.cuidado.data.repository.CitaRepository
import com.example.cuidado.data.repository.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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

    val profileName: StateFlow<String> = ProfileRepository.getProfile()
        .map { profile -> profile?.name?.takeIf { it.isNotBlank() } ?: "Sin perfil registrado" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Sin perfil registrado"
        )

    /**
     * Lista de actividades pendientes para hoy (oculta las que ya tienen COMPLETED hoy) ordenadas cronológicamente.
     */
    val activities: StateFlow<List<ActivityItemState>> = combine(
        ActivityRepository.getActivities(),
        ActivityRepository.getLogsForDate(getCurrentDate())
    ) { allActivities, logs ->
        val completedIds = logs.filter { it.status == "COMPLETED" }.map { it.activityId }.toSet()
        allActivities.filter { activity ->
            !completedIds.contains(activity.id)
        }.map { activity ->
            ActivityItemState(
                activity = activity,
                isCompleted = false
            )
        }.sortedBy { parseTimeToMinutes(it.activity.time) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val proximasCitas: StateFlow<List<CitaEntity>> = run {
        val ahora = System.currentTimeMillis()
        val limite = ahora + TimeUnit.DAYS.toMillis(7)
        CitaRepository.getInstance().observarProximasCitas(ahora, limite)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

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

    /**
     * Registra el estado y notas de una actividad para hoy.
     */
    fun markActivityStatus(activity: Activity, status: String, notes: String) {
        viewModelScope.launch {
            ActivityRepository.markActivityStatus(
                activityId = activity.id,
                date = getCurrentDate(),
                status = status,
                time = getCurrentTime(),
                notes = notes
            )
        }
    }

    private fun getCurrentDate(): String = dateFormatter.format(Date())
    private fun getCurrentTime(): String = timeFormatter.format(Date())
}
