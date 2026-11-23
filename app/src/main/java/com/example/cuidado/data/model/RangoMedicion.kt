package com.example.cuidado.data.model

data class RangoMedicion(
    val tipo: String,
    val valorMin: Double,
    val valorMax: Double,
    val umbralConsecutivo: Int = 3
)
