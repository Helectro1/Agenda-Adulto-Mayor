package com.example.cuidado.data.model

import java.util.UUID

/**
 * Representa una ocurrencia histórica de una actividad.
 *
 * @param id Identificador único del registro.
 * @param activityId Referencia a la actividad original.
 * @param date Fecha en la que ocurrió (formato yyyy-MM-dd).
 * @param status Resultado de la actividad (COMPLETED, MISSED).
 * @param executionTime Hora real en la que se marcó como realizada (opcional).
 */
data class ActivityLog(
    val id: String = UUID.randomUUID().toString(),
    val activityId: String,
    val date: String,
    val status: String,
    val executionTime: String? = null
)
