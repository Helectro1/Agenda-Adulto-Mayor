package com.example.cuidado.data.repository

import com.example.cuidado.data.local.CitaDao
import com.example.cuidado.data.local.CitaEntity
import com.example.cuidado.interfaces.datos.iCitaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar

class CitaRepository(
    private val objCitaDao: CitaDao
) : iCitaRepository {
    override suspend fun insertarCita(objCita: CitaEntity): Long = withContext(Dispatchers.IO) {
        objCitaDao.insertarCita(objCita)
    }

    override suspend fun actualizarCita(cita: CitaEntity) = withContext(Dispatchers.IO) {
        objCitaDao.actualizarCita(cita)
    }

    override fun observarTodasLasCitas(): Flow<List<CitaEntity>> {
        return objCitaDao.observarTodasLasCitas()
    }

    override suspend fun obtenerPorFecha(dtFecha: Long): List<CitaEntity> = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance().apply { timeInMillis = dtFecha }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val dtInicioDia = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val dtFinDia = cal.timeInMillis

        objCitaDao.obtenerPorFecha(dtInicioDia, dtFinDia)
    }

    override suspend fun obtenerPorMes(nMes: Int, nAnio: Int): List<CitaEntity> = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance()
        cal.set(nAnio, nMes - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val dtInicioMes = cal.timeInMillis

        val ultimoDia = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(nAnio, nMes - 1, ultimoDia, 23, 59, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val dtFinMes = cal.timeInMillis

        objCitaDao.obtenerPorMes(dtInicioMes, dtFinMes)
    }

    suspend fun obtenerProximasCitas(dtInicio: Long, dtFin: Long): List<CitaEntity> = withContext(Dispatchers.IO) {
        objCitaDao.obtenerCitasEnRango(dtInicio, dtFin)
    }

    fun observarProximasCitas(ahora: Long, limite: Long): Flow<List<CitaEntity>> {
        return objCitaDao.observarProximasCitas(ahora, limite)
    }

    override suspend fun eliminarCita(lIdCita: Long): Boolean = withContext(Dispatchers.IO) {
        objCitaDao.eliminarCita(lIdCita) > 0
    }

    companion object {
        @Volatile
        private var instance: CitaRepository? = null

        fun initialize(dao: CitaDao): CitaRepository {
            return instance ?: synchronized(this) {
                instance ?: CitaRepository(dao).also { instance = it }
            }
        }

        fun getInstance(): CitaRepository {
            return instance ?: throw IllegalStateException("CitaRepository must be initialized first")
        }
    }
}
