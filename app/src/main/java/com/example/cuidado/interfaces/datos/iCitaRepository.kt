package com.example.cuidado.interfaces.datos

import com.example.cuidado.data.local.CitaEntity

interface iCitaRepository {
    suspend fun insertarCita(objCita: CitaEntity): Long
    suspend fun obtenerPorFecha(dtFecha: Long): List<CitaEntity>
    suspend fun obtenerPorMes(nMes: Int, nAnio: Int): List<CitaEntity>
    suspend fun eliminarCita(lIdCita: Long): Boolean
}
