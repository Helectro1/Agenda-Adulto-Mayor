package com.example.cuidado.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CITA")
data class CitaEntity(
    @PrimaryKey(autoGenerate = true)
    val lIdCita: Long = 0L,
    val lIdPerfil: Long = 1L,
    val dtFechaHora: Long,
    val strEspecialista: String,
    val strUbicacion: String,
    val strNotas: String = "",
    val strCategoria: String = "General"
)
