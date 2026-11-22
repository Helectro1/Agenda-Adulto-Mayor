package com.example.cuidado.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mediciones")
data class MedicionEntity(
    @PrimaryKey
    val id: String,
    val tipo: String,
    val valor: Double,
    val fecha: String,
    val hora: String,
    val notas: String
)
