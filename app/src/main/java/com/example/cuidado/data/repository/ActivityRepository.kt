package com.example.cuidado.data.repository

import com.example.cuidado.data.local.ActivityDao
import com.example.cuidado.data.local.ActivityLogDao
import com.example.cuidado.data.local.toDomain
import com.example.cuidado.data.local.toEntity
import com.example.cuidado.data.model.Activity
import com.example.cuidado.data.model.ActivityLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repositorio centralizado para gestionar las actividades y sus registros.
 * Utiliza Room para la persistencia de datos.
 */
object ActivityRepository {

    private lateinit var activityDao: ActivityDao
    private lateinit var activityLogDao: ActivityLogDao
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Inicializa el repositorio con los DAOs necesarios.
     * Inserta datos de ejemplo si la base de datos está vacía.
     */
    fun initialize(dao: ActivityDao, logDao: ActivityLogDao) {
        activityDao = dao
        activityLogDao = logDao
        
        repositoryScope.launch {
            val currentActivities = withContext(Dispatchers.IO) {
                activityDao.getAllActivities()
            }
            if (currentActivities.isEmpty()) {
                insertSampleData()
            }
        }
    }

    /**
     * Devuelve el flujo de todas las actividades programadas desde Room.
     */
    fun getActivities(): Flow<List<Activity>> {
        return activityDao.observeActivities().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Agrega una nueva actividad a Room.
     */
    suspend fun addActivity(activity: Activity) = withContext(Dispatchers.IO) {
        activityDao.insertActivity(activity.toEntity())
    }
    
    /**
     * Actualiza una actividad existente en Room.
     */
    suspend fun updateActivity(activity: Activity) = withContext(Dispatchers.IO) {
        activityDao.updateActivity(activity.toEntity())
    }

    /**
     * Elimina una actividad de Room.
     */
    suspend fun deleteActivity(activity: Activity) = withContext(Dispatchers.IO) {
        activityDao.deleteActivity(activity.toEntity())
    }

    /**
     * Observa los registros de ejecución para una fecha específica.
     */
    fun getLogsForDate(date: String): Flow<List<ActivityLog>> {
        return activityLogDao.observeLogsForDate(date).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Registra una actividad como completada para una fecha y hora determinadas.
     * Utiliza OnConflictStrategy.REPLACE en el DAO para evitar duplicados.
     */
    suspend fun markActivityAsCompleted(activityId: String, date: String, time: String) = withContext(Dispatchers.IO) {
        val log = ActivityLog(
            activityId = activityId,
            date = date,
            status = "COMPLETED",
            executionTime = time
        )
        activityLogDao.insertLog(log.toEntity())
    }

    private suspend fun insertSampleData() = withContext(Dispatchers.IO) {
        val sampleActivities = listOf(
            Activity(
                name = "Medicamento",
                time = "08:00",
                repeatDays = "1,2,3,4,5,6,7",
                type = "Medicamento"
            ),
            Activity(
                name = "Desayuno",
                time = "09:00",
                repeatDays = "1,2,3,4,5,6,7",
                type = "Alimentación"
            ),
            Activity(
                name = "Caminata",
                time = "11:00",
                repeatDays = "1,3,5",
                type = "Ejercicio"
            ),
            Activity(
                name = "Medicamento",
                time = "14:00",
                repeatDays = "1,2,3,4,5,6,7",
                type = "Medicamento"
            )
        )
        activityDao.insertActivities(sampleActivities.map { it.toEntity() })
    }
}
