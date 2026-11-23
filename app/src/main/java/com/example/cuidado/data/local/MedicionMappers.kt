package com.example.cuidado.data.local

import com.example.cuidado.data.model.Medicion
import com.example.cuidado.data.model.RangoMedicion

fun MedicionEntity.toDomain(): Medicion {
    return Medicion(
        id = id,
        tipo = tipo,
        valor = valor,
        fecha = fecha,
        hora = hora,
        notas = notas
    )
}

fun Medicion.toEntity(): MedicionEntity {
    return MedicionEntity(
        id = id,
        tipo = tipo,
        valor = valor,
        fecha = fecha,
        hora = hora,
        notas = notas
    )
}

fun RangoMedicionEntity.toDomain(): RangoMedicion {
    return RangoMedicion(
        tipo = tipo,
        valorMin = valorMin,
        valorMax = valorMax,
        umbralConsecutivo = umbralConsecutivo
    )
}

fun RangoMedicion.toEntity(): RangoMedicionEntity {
    return RangoMedicionEntity(
        tipo = tipo,
        valorMin = valorMin,
        valorMax = valorMax,
        umbralConsecutivo = umbralConsecutivo
    )
}
