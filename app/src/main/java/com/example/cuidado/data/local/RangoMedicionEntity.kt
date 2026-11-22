package com.example.cuidado.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rangos_medicion")
data class RangoMedicionEntity(
    @PrimaryKey
    val tipo: String,
    val valorMin: Double,
    val valorMax: Double,
    val umbralConsecutivo: Int
)
