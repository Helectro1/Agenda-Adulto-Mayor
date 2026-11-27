package com.example.cuidado.interfaces.datos

import com.example.cuidado.data.local.CitaEntity
import kotlinx.coroutines.flow.Flow

interface iCitaRepository {
    suspend fun insertarCita(objCita: CitaEntity): Long
    suspend fun actualizarCita(cita: CitaEntity)
    fun observarTodasLasCitas(): Flow<List<CitaEntity>>
    suspend fun obtenerPorFecha(dtFecha: Long): List<CitaEntity>
    suspend fun obtenerPorMes(nMes: Int, nAnio: Int): List<CitaEntity>
    suspend fun eliminarCita(lIdCita: Long): Boolean
}
