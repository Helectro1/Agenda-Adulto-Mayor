package com.example.cuidado.interfaces.datos

import com.example.cuidado.data.repository.ActivityRepository
import kotlinx.coroutines.flow.first

data class ActividadResumen(
    val lIdActividad: String,
    val strNombre: String,
    val strDescripcion: String,
    val strHora: String,
    val eTipo: String,
    val bActiva: Boolean = true
)

interface iActividadRepository {
    suspend fun obtenerActivas(): List<ActividadResumen>
}

class ActivityRepositoryAdapter : iActividadRepository {
    override suspend fun obtenerActivas(): List<ActividadResumen> {
        val activities = ActivityRepository.getActivities().first()
        return activities.map { activity ->
            ActividadResumen(
                lIdActividad = activity.id,
                strNombre = activity.name,
                strDescripcion = activity.description,
                strHora = activity.time,
                eTipo = activity.type,
                bActiva = activity.isActive
            )
        }
    }
}
