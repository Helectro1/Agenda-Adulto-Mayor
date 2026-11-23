package com.example.cuidado.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarCita(objCita: CitaEntity): Long

    @Query("SELECT * FROM CITA WHERE dtFechaHora >= :dtInicioDia AND dtFechaHora <= :dtFinDia ORDER BY dtFechaHora ASC")
    fun obtenerPorFecha(dtInicioDia: Long, dtFinDia: Long): List<CitaEntity>

    @Query("SELECT * FROM CITA WHERE dtFechaHora >= :dtInicioMes AND dtFechaHora <= :dtFinMes ORDER BY dtFechaHora ASC")
    fun obtenerPorMes(dtInicioMes: Long, dtFinMes: Long): List<CitaEntity>

    @Query("SELECT * FROM CITA WHERE dtFechaHora >= :dtInicio AND dtFechaHora <= :dtFin ORDER BY dtFechaHora ASC")
    fun obtenerCitasEnRango(dtInicio: Long, dtFin: Long): List<CitaEntity>

    @Query("SELECT * FROM CITA WHERE dtFechaHora >= :ahora AND dtFechaHora <= :limite ORDER BY dtFechaHora ASC")
    fun observarProximasCitas(ahora: Long, limite: Long): Flow<List<CitaEntity>>

    @Query("DELETE FROM CITA WHERE lIdCita = :lIdCita")
    fun eliminarCita(lIdCita: Long): Int
}
