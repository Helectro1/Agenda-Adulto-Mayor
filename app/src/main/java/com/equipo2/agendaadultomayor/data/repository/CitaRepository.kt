package com.equipo2.agendaadultomayor.data.repository

import com.equipo2.agendaadultomayor.data.entity.Cita
import com.equipo2.agendaadultomayor.data.local.dao.CitaDao
import com.equipo2.agendaadultomayor.interfaces.datos.iCitaRepository
import java.util.Calendar

class CitaRepository(
    private val objCitaDao: CitaDao
) : iCitaRepository {

    override suspend fun insertarCita(objCita: Cita): Long {
        return objCitaDao.insertarCita(objCita)
    }

    override suspend fun obtenerPorFecha(dtFecha: Long): List<Cita> {
        val cal = Calendar.getInstance().apply { timeInMillis = dtFecha }

        // Inicio del día (00:00:00.000)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val dtInicioDia = cal.timeInMillis

        // Fin del día (23:59:59.999)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val dtFinDia = cal.timeInMillis

        return objCitaDao.obtenerPorFecha(dtInicioDia, dtFinDia)
    }

    override suspend fun obtenerPorMes(nMes: Int, nAnio: Int): List<Cita> {
        val cal = Calendar.getInstance()

        // Primer día del mes a las 00:00:00 (nMes: 1 = Enero ... 12 = Diciembre)
        cal.set(nAnio, nMes - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val dtInicioMes = cal.timeInMillis

        // Último día del mes a las 23:59:59
        val ultimoDia = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(nAnio, nMes - 1, ultimoDia, 23, 59, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val dtFinMes = cal.timeInMillis

        return objCitaDao.obtenerPorMes(dtInicioMes, dtFinMes)
    }

    override suspend fun eliminarCita(lIdCita: Long): Boolean {
        return objCitaDao.eliminarCita(lIdCita) > 0
    }
}