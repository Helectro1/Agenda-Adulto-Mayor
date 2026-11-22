package com.equipo2.agendaadultomayor.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CITA")
data class Cita(
    @PrimaryKey(autoGenerate = true)
    val lIdCita: Long = 0L,
    val lIdPerfil: Long = 1L, // Por defecto 1L mientras se integra el módulo Perfil
    val dtFechaHora: Long, // Guardamos la fecha y hora en milisegundos (Timestamp) para SQLite
    val strEspecialista: String,
    val strUbicacion: String,
    val strNotas: String = ""
)