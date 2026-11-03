package com.example.cuidado.data.model

import java.util.UUID

/**
 * Representa la DEFINICIÓN de una actividad programada en la rutina.
 * Esta entidad está preparada para ser una tabla en Room.
 *
 * @param id Identificador único de la actividad.
 * @param name Nombre de la actividad (ej. "Medicamento").
 * @param description Notas adicionales o descripción.
 * @param time Hora programada en formato HH:mm.
 * @param type Categoría de la actividad (ej. "Medicamento", "Alimentación").
 * @param repeatDays Días de la semana en que se repite (ej. "1,2,3,4,5").
 * @param isActive Indica si la actividad está vigente en la rutina.
 * @param color Color asociado para personalización visual.
 */
data class Activity(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val time: String,
    val type: String = "General",
    val repeatDays: String = "",
    val isActive: Boolean = true,
    val color: Int? = null
)
