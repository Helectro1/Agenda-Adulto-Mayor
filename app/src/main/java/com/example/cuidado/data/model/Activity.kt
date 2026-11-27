package com.example.cuidado.data.model

import java.util.UUID

/**
 * Representa la DEFINICIÓN de una actividad programada en la rutina.
 * Esta entidad está preparada para ser una tabla en Room.
 *
 * @param id Identificador único de la actividad.
 * @param name Nombre de la actividad (ej. "Medicamento").
 * @param description Notas adicionales o descripción.
 * @param time Hora programada en formato HH:mm o EVERY_HH:mm.
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

fun parseTimeToMinutes(timeStr: String): Int {
    val cleanTime = if (timeStr.startsWith("EVERY_")) timeStr.removePrefix("EVERY_") else timeStr
    val parts = cleanTime.trim().split(":")
    if (parts.size == 2) {
        val h = parts[0].toIntOrNull()
        val m = parts[1].toIntOrNull()
        if (h != null && m != null && h in 0..23 && m in 0..59) {
            return h * 60 + m
        }
    }
    return Int.MAX_VALUE
}

fun formatActivityTime(timeStr: String): String {
    if (timeStr.startsWith("EVERY_")) {
        val raw = timeStr.removePrefix("EVERY_")
        val parts = raw.split(":")
        if (parts.size == 2) {
            val h = parts[0].toIntOrNull() ?: 0
            val m = parts[1].toIntOrNull() ?: 0
            return buildString {
                append("Cada ")
                if (h > 0) append("$h h ")
                if (m > 0 || h == 0) append("$m min")
            }.trim()
        }
        return "Cada $raw"
    }
    return timeStr
}
