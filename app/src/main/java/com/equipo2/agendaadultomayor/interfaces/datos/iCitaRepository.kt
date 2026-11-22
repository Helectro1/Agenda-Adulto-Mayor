package com.equipo2.agendaadultomayor.interfaces.datos

import com.equipo2.agendaadultomayor.data.entity.Cita

interface iCitaRepository {
    suspend fun insertarCita(objCita: Cita): Long
    suspend fun obtenerPorFecha(dtFecha: Long): List<Cita>
    suspend fun obtenerPorMes(nMes: Int, nAnio: Int): List<Cita>
    suspend fun eliminarCita(lIdCita: Long): Boolean
}