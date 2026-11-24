package com.example.cuidado.data.repository

import com.example.cuidado.data.local.MedicionDao
import com.example.cuidado.data.local.toDomain
import com.example.cuidado.data.local.toEntity
import com.example.cuidado.data.model.Medicion
import com.example.cuidado.data.model.RangoMedicion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

object MedicionRepository {

    private lateinit var medicionDao: MedicionDao

    fun initialize(dao: MedicionDao) {
        medicionDao = dao
    }

    fun getMediciones(): Flow<List<Medicion>> {
        return medicionDao.observeMediciones().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun saveMedicion(medicion: Medicion) = withContext(Dispatchers.IO) {
        medicionDao.insertMedicion(medicion.toEntity())
    }

    suspend fun getRango(tipo: String): RangoMedicion = withContext(Dispatchers.IO) {
        medicionDao.getRangoByType(tipo)?.toDomain() ?: getDefaultRango(tipo)
    }

    suspend fun saveRango(rango: RangoMedicion) = withContext(Dispatchers.IO) {
        medicionDao.insertOrUpdateRango(rango.toEntity())
    }

    private fun getDefaultRango(tipo: String): RangoMedicion {
        return when (tipo.lowercase()) {
            "glucosa" -> RangoMedicion(tipo, 70.0, 140.0, 3)
            "presión sistólica", "presionsistolica" -> RangoMedicion(tipo, 90.0, 130.0, 3)
            "presión diastólica", "presiondiastolica" -> RangoMedicion(tipo, 60.0, 85.0, 3)
            "peso" -> RangoMedicion(tipo, 40.0, 120.0, 5)
            "temperatura" -> RangoMedicion(tipo, 36.0, 37.5, 2)
            else -> RangoMedicion(tipo, 0.0, 100.0, 3)
        }
    }
}
