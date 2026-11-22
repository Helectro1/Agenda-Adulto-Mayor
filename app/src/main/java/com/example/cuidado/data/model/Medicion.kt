package com.example.cuidado.data.model

import java.util.UUID

data class Medicion(
    val id: String = UUID.randomUUID().toString(),
    val tipo: String,
    val valor: Double,
    val fecha: String,
    val hora: String,
    val notas: String = ""
)
